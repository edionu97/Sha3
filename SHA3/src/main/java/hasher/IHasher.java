package hasher;

import java.util.List;
import java.util.Map;

public interface IHasher <THashIn, THashOut> {

    /**
     * Get the value of the hash or a specific entity
     * @param tHashIn: the object for which we want to process for calculating bytes
     * @return a value that represents the hash code
     */
    THashOut getCheckSum(final THashIn tHashIn);

    /**
     * Check if the checksum of those two objects is the same
     * @param first: first object
     * @param second: the second object
     * @return true if those objects have the same checksum or false otherwise
     */
    boolean isSameChecksum(final THashIn first, final THashIn second);
}
