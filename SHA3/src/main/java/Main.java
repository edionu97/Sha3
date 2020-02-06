import application.config.Config;
import application.service.impl.abs.AbstractDirectoryMonitor;
import org. springframework.context.annotation.AnnotationConfigApplicationContext;
import utils.constants.ConstantsManager;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class Main {

    public static void main(final String... args) throws Exception {

        final AbstractDirectoryMonitor directoryMonitor = new AnnotationConfigApplicationContext(Config.class)
                .getBean(AbstractDirectoryMonitor.class);

        //register for create, modify, delete
        final String path = ConstantsManager.getInstance().get("mainDirectory");
        directoryMonitor.registerDirectoryAndAllSubdirectories(
                path,
                new WatchEvent.Kind<?>[]{
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE
                }
        );
        directoryMonitor.startService();
    }
}
