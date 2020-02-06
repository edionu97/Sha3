package application.config;

import application.service.impl.DirectoryServiceMonitor;
import application.service.impl.abs.AbstractDirectoryMonitor;
import hasher.IHasher;
import hasher.impl.Sha3Hasher;
import integrity.IIntegrity;
import integrity.impl.Integrity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import utils.hasher.SHAType;
import utils.threads.ThreadHelper;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    ExecutorService service() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public IHasher<Byte[], String> hasher() {
        return new Sha3Hasher<>(
                (byteRep) -> {
                    final List<Integer> bits = new ArrayList<>();

                    //transform each byte into integer byte
                    for (final byte bit : byteRep) {
                        final String s = Integer.toBinaryString(bit);
                        for (int i = 0; i < s.length(); ++i) {
                            bits.add(s.charAt(i) == '0' ? 0 : 1);
                        }
                    }

                    return bits;
                },
                SHAType.SHA224, false
        );
    }

    @Bean
    public IIntegrity integrity() {
        return new Integrity(hasher(), ThreadHelper.getInstance());
    }

    @Bean
    public AbstractDirectoryMonitor monitor() throws Exception {
        return new DirectoryServiceMonitor(
                FileSystems.getDefault().newWatchService(),
                integrity(),
                service()
        );
    }


}

