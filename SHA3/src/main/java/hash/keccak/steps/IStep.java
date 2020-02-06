package hash.keccak.steps;

import java.util.List;

@FunctionalInterface
public interface IStep {
    int ROWS = 5;
    int COLS = 5;
    int DEPTH = 64;
    int BITS = ROWS * COLS * DEPTH;

    /**
     * @param inBits: the set of input bits
     * @param otherData: the other necessary data necessary for step
     * @return a new set of bits with the same length as the input bits's length
     */
    List<Integer> applyStep(final List<Integer> inBits, final int... otherData);
}
