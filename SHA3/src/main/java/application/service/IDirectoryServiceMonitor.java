package application.service;

import java.nio.file.WatchEvent;

public interface IDirectoryServiceMonitor {

    /**
     * Registers a directory for modifications
     *
     * @param directoryPath: the path of the directory
     * @param notifiedWhen:  the events for that we register the folder
     * @throws Exception: if te file cannot be found
     */
    void registerDirectory(final String directoryPath,
                           final WatchEvent.Kind<?>[] notifiedWhen) throws Exception;

    /**
     * Register the current directory and all its subdirectories
     *
     * @param directoryPath: the path of the directory
     * @param notifiedWhen:  the types of events
     * @throws Exception if something is wrong
     */
    void registerDirectoryAndAllSubdirectories(final String directoryPath,
                                               final WatchEvent.Kind<?>[] notifiedWhen) throws Exception;

    /**
     * Starts the service on another thread
     */
    void startService();
}
