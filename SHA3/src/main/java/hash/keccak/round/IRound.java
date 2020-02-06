package hash.keccak.round;

import java.util.List;

@FunctionalInterface
public interface IRound <T> {
    /**
     * Apply the round of transformation functions
     * @param inBits: the bits
     * @return a new list of bits representing the value after transformations
     */
    List<T> apply(final List<T> inBits);
}
