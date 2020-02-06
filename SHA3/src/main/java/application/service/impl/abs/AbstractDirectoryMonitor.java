package application.service.impl.abs;

import application.service.IDirectoryServiceMonitor;
import org.apache.commons.io.FileDeleteStrategy;
import utils.constants.ConstantsManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

public abstract class AbstractDirectoryMonitor implements IDirectoryServiceMonitor {

    protected static final String REJECTED_FILE_CREATE_MODE = ConstantsManager.getInstance().get("rejectedFileType");
    protected static final String REJECTED_FILE_NAME = ConstantsManager.getInstance().get("rejectedFileName");

    protected final WatchService watchService;
    protected WatchEvent.Kind<?>[] folderEvents;
    protected String mainDirectory;

    protected AbstractDirectoryMonitor(final WatchService watchService) {
        this.watchService = watchService;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void startService() {
        Executors.newSingleThreadExecutor().submit(() -> {
            System.out.println("Service started, looking for changes in " + mainDirectory);
            try {
                for (; ; ) {
                    //wait until an event appears
                    final var key = watchService.take();
                    //get all the events and inform the methods
                    key
                            .pollEvents()
                            .forEach(watchEvent ->
                                    applyFolderRestriction(
                                            new AbstractMap.SimpleEntry<>(watchEvent.kind(), (Path) key.watchable()),
                                            watchEvent
                                    )
                            );
                    key.reset();
                }
            } catch (final Exception ignored) {
            }
        });
    }

    /**
     * In this method will be implemented the folder restrictions
     *
     * @param eventInfo: the event info
     * @param event:     the event
     */
    protected abstract void applyFolderRestriction(final Map.Entry<WatchEvent.Kind<?>, Path> eventInfo,
                                                   final WatchEvent<?> event);


    @Override
    public void registerDirectory(final String directoryPath,
                                  final WatchEvent.Kind<?>[] events) throws Exception {

        //if is first time delete all the rejected files form folders
        final Path rejectedDirectoryPath = Paths.get(directoryPath).resolve(REJECTED_FILE_NAME);
        try {
            FileDeleteStrategy.FORCE.delete(rejectedDirectoryPath.toFile());
        } catch (Exception ignore) {
        }

        //construct the file
        final var file = new File(directoryPath);

        //check if the file exists
        if (!file.exists()) {
            throw new Exception("The file cannot be found");
        }

        //check if the file is a directory
        if (!file.isDirectory()) {
            throw new Exception("The file must be a directory");
        }

        //register the file for modifications
        file.toPath().register(watchService, events);
    }

    @Override
    public void registerDirectoryAndAllSubdirectories(final String directoryPath,
                                                      final WatchEvent.Kind<?>[] notifiedWhen) throws Exception {

        this.folderEvents = notifiedWhen;
        registerDirectory((this.mainDirectory = directoryPath), notifiedWhen);

        //get the directory
        final File directory = new File(directoryPath);

        //register all subdirectories
        for (final File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                continue;
            }
            registerFile(file.getAbsolutePath(), notifiedWhen);
        }
    }

    /**
     * This method is used to recursively register all folders and sub folders
     *
     * @param directoryPath: the root directory
     * @param notifiedWhen:  the event types
     * @throws Exception: if something went wrong
     */
    protected void registerFile(final String directoryPath,
                                final WatchEvent.Kind<?>[] notifiedWhen) throws Exception {

        registerDirectory(directoryPath, notifiedWhen);

        //get the directory
        final File directory = new File(directoryPath);

        //register all subdirectories
        for (final File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                continue;
            }
            registerFile(file.getAbsolutePath(), notifiedWhen);
        }
    }

}
