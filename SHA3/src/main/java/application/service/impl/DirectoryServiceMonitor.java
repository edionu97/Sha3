package application.service.impl;

import application.service.impl.abs.AbstractDirectoryMonitor;
import integrity.IIntegrity;
import utils.bits.BitUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DirectoryServiceMonitor extends AbstractDirectoryMonitor {

    private final IIntegrity integrity;
    private final ExecutorService executorService;
    private final EventHandler eventHandler = new EventHandler();

    public DirectoryServiceMonitor(final WatchService watchService,
                                   final IIntegrity integrity,
                                   final ExecutorService executorService) {
        super(watchService);
        this.integrity = integrity;
        this.executorService = executorService;
    }


    @Override
    protected void applyFolderRestriction(final Map.Entry<WatchEvent.Kind<?>, Path> eventInfo, final WatchEvent<?> e) {
        executorService.submit(() -> eventHandler.handleEvent(eventInfo, e));
    }


    private class EventHandler {

        final ConcurrentHashMap<File, String> hashValueForItems = new ConcurrentHashMap<>();

        /**
         * Handle the event
         *
         * @param eventInfo: the event info
         * @param e:         the event
         */
        void handleEvent(final Map.Entry<WatchEvent.Kind<?>, Path> eventInfo, final WatchEvent<?> e) {
            //get the event type and the directory path
            final var eventKind = eventInfo.getKey();
            final var directoryPath = eventInfo.getValue();

            //if the event type is overflow than do nothing
            if (eventKind.equals(StandardWatchEventKinds.OVERFLOW)) {
                return;
            }

            //get the file path
            final File newFile = directoryPath.resolve((Path) e.context()).toFile();

            //treat the directory event
            if (newFile.isDirectory()) {
                treatDirectoryEvent(newFile, eventKind);
                return;
            }

            //treat the delete event
            if (eventKind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                treatDeleteEvent(newFile);
                return;
            }

            //treat the create event
            if (eventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                treatCreateEvent(directoryPath, newFile);
            }
        }

        /**
         * Private threat the directory event
         *
         * @param directory: the directory
         * @param eventType: the type of event
         */
        private void treatDirectoryEvent(final File directory,
                                         final WatchEvent.Kind<?> eventType) {

            //if the event is folder delete
            if (eventType.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                try {
                    directory.toPath().register(watchService, folderEvents).cancel();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            //if the file is the rejected directory than do nothing
            if (directory.getAbsolutePath().contains(REJECTED_FILE_NAME)) {
                return;
            }
            //otherwise add the folder to notification
            try {
                registerFile(directory.getAbsolutePath(), folderEvents);
            } catch (final Exception ignore) {
            }
        }

        /**
         * Threat the delete event
         *
         * @param newFile: the new current file
         */
        private void treatDeleteEvent(final File newFile) {
            if (newFile.getAbsolutePath().contains(REJECTED_FILE_NAME)) {
                return;
            }
            hashValueForItems.remove(newFile);
        }

        /**
         * Treat the create event
         *
         * @param directoryPath: the path of the current directory
         * @param newFile:       the new file that is added into the directory
         */
        private void treatCreateEvent(final Path directoryPath, final File newFile) {

            //get the file bytes
            try {
                //compute the hash for files
                computeHashForDirectoryFilesExcepting(Paths.get(mainDirectory).toFile(), newFile);

                //get the file bytes
                final Byte[] bytes = BitUtils.convertByteToObject(Files.readAllBytes(newFile.toPath()));

                // if in folder exists a file with the same hash -> reject this one
                if (hashValueForItems.containsValue(integrity.getHashBlock(bytes))) {
                    rejectFile(directoryPath, newFile);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Reject the file
         *
         * @param directoryPath: the directory in which the file will be rejected
         * @param newFile:       the file
         */
        private synchronized void rejectFile(final Path directoryPath, final File newFile) {

            //try to create the rejected directory
            final Path rejectedDirectoryPath = directoryPath.resolve(REJECTED_FILE_NAME);
            try {
                Files.createDirectory(rejectedDirectoryPath);
                Files.setAttribute(rejectedDirectoryPath, REJECTED_FILE_CREATE_MODE, true);
            } catch (final Exception ignore) {
            }

            //move the file
            try {
                Files.move(
                        newFile.toPath(),
                        rejectedDirectoryPath.resolve(newFile.toPath().getFileName()),
                        REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * For each file from the directory compute the hash function and associate the file name with the computed hash
         *
         * @param directory:        the main directory
         * @param fileToBeExcluded: the file that will be excluded for hash calculation
         */
        private void computeHashForDirectoryFilesExcepting(final File directory, final File fileToBeExcluded) {

            // iterate through 1 level directory
            for (final File file : Objects.requireNonNull(directory.listFiles())) {

                //if the file is directory than check also for files
                if (file.equals(fileToBeExcluded)) {
                    continue;
                }

                //do not take in consideration the rejected file
                if (file.getAbsolutePath().contains(REJECTED_FILE_NAME)) {
                    continue;
                }

                //if is directory do also in its file
                if (file.isDirectory()) {
                    computeHashForDirectoryFilesExcepting(file, fileToBeExcluded);
                    continue;
                }

                //compute the hash for items
                hashValueForItems.computeIfAbsent(file, (currentFile) -> {
                    try {
                        // get the  file bytes
                        final Byte[] bytes = BitUtils.convertByteToObject(Files.readAllBytes(currentFile.toPath()));
                        //return the hash value
                        return integrity.getHashBlock(bytes);
                    } catch (final IOException ignored) {
                    }
                    return null;
                });

            }
        }
    }
}
