package hash.keccak;

import java.util.List;

public interface IKeccak {

    /**
     * Get the bits
     *
     * @param bits: the input bits that will be transformed
     * @return a list of bits
     */
    List<Integer> getBits(final List<Integer> bits);
}
