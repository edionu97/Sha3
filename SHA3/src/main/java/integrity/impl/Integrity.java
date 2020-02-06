package integrity.impl;

import hasher.IHasher;
import integrity.IIntegrity;
import utils.threads.ThreadHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class Integrity implements IIntegrity {

    private static final int HASHER_BIT_INPUT_LENGTH = 10000;
    private static final int CHUNK_BYTE_SIZE = HASHER_BIT_INPUT_LENGTH / 8;

    private final IHasher<Byte[], String> hasher;
    private final ThreadHelper threadCompressor;

    public Integrity(final IHasher<Byte[], String> hasher,
                     final ThreadHelper threadCompressor) {
        this.hasher = hasher;
        this.threadCompressor = threadCompressor;
    }

    @Override
    public boolean isSame(final Byte[] first, final Byte[] second) {

        //split in chunks
        final List<Byte[]> firstInChunks = chunks(first);
        final List<Byte[]> secondChunks = chunks(second);

        //if they do not the same number of chunks => different objects
        if (firstInChunks.size() != secondChunks.size()) {
            return false;
        }

        //check if each chunk corresponds
        for (int chunkIdx = 0; chunkIdx < firstInChunks.size(); ++chunkIdx) {
            if (hasher.isSameChecksum(firstInChunks.get(chunkIdx), secondChunks.get(chunkIdx))) {
                continue;
            }
            return false;
        }

        return true;
    }

    @Override
    public String getHashBlock(final Byte[] object) {

        //if the object is empty -> get the empty hash value
        if (object.length == 0) {
            return getHash(object);
        }

        //distribute job on tasks
        final List<Future<String>> parallelJobResults = threadCompressor.distributeOnThreadsAndSubmit(
                chunks(object),
                (byteList) -> {
                    final StringBuilder builder = new StringBuilder();
                    byteList.forEach(chunk -> builder.append(hasher.getCheckSum(chunk)));
                    return builder.toString();
                }
        );

        //create the builder
        final StringBuilder builder = new StringBuilder();
        parallelJobResults.forEach(future -> {
            try {
                builder.append(future.get());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        //get the checksum
        return builder.toString();
    }

    @Override
    public String getHash(final Byte[] object) {
        return hasher.getCheckSum(object);
    }

    /**
     * Split bytes into chunks
     *
     * @param bytes: the byte array
     * @return a list of chunks
     */
    private List<Byte[]> chunks(final Byte[] bytes) {
        //declare the number of chunks
        final int chunks = (int) Math.ceil((bytes.length + .0) / CHUNK_BYTE_SIZE);

        //create a sublist of chunks
        final List<Byte[]> chunkList = new ArrayList<>();
        for (int i = 0; i < chunks; ++i) {
            //copy bytes
            final List<Byte> byteList = new ArrayList<>(
                    Arrays
                            .asList(bytes)
                            .subList(i * CHUNK_BYTE_SIZE,
                                    Math.min(
                                            bytes.length,
                                            (i + 1) * CHUNK_BYTE_SIZE
                                    )
                            )
            );
            //add bytes into list
            chunkList.add(byteList.toArray(new Byte[0]));
        }

        //return chunk list
        return chunkList;
    }
}
