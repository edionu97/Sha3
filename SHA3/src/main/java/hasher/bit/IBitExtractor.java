package hasher.bit;

import java.util.List;

@FunctionalInterface
public interface IBitExtractor <TObject> {
    /**
     * Get the bits for a specific object
     * @param object: the value that will be processed
     * @return a value that represents the bits
     */
    List<Integer> getBits(final TObject object);
}
