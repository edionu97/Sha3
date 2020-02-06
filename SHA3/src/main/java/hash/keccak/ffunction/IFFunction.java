package hash.keccak.ffunction;

import java.util.List;
import java.util.Map;

public interface IFFunction {
    /**
     * @param rBits: the value of r bits
     * @param cBits: the value of c bits
     * @return a pair of items
     * -> key -> r bits
     * -> value -> c bits
     */
    Map.Entry<List<Integer>, List<Integer>> apply(final List<Integer> rBits,
                                                  final List<Integer> cBits);

}
