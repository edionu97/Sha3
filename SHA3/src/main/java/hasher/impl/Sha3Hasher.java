package hasher.impl;

import hash.IHashAlgorithm;
import hash.impl.SHA3;
import hash.keccak.IKeccak;
import hash.keccak.ffunction.IFFunction;
import hash.keccak.ffunction.impl.FKeecakFunction;
import hash.keccak.impl.Keccak;
import hasher.IHasher;
import hasher.bit.IBitExtractor;
import utils.hasher.SHAType;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sha3Hasher<THashIn> implements IHasher<THashIn, String> {

    private final Random random;

    private final IBitExtractor<THashIn> bitExtractor;
    private final SHAType shaType;
    private final IFFunction function;
    private final boolean multipleHashSameKey;
    private volatile Map.Entry<List<Integer>, List<Integer>> initialState;

    public Sha3Hasher(
            final IBitExtractor<THashIn> bitExtractor,
            final SHAType shaType,
            final boolean multipleHashSameKey) {

        this.random = multipleHashSameKey ? new Random() : new Random(1);
        this.multipleHashSameKey = multipleHashSameKey;

        //get the parameters
        final var parameters = shaType.get();

        this.shaType = shaType;
        this.bitExtractor = bitExtractor;

        this.function = new FKeecakFunction(parameters.getR(), parameters.getC());
    }

    @Override
    public String getCheckSum(final THashIn toBeHashed) {

        Map.Entry<List<Integer>, List<Integer>>  initialState;
        synchronized (this) {
            //get the integer initial state
            initialState = setInitialState();
        }

        //get the algorithm for state
        final IHashAlgorithm<String, List<Integer>> hashAlgorithm = getAlgorithmForState(initialState);

        //get the hash value
        return hashAlgorithm.encode(bitExtractor.getBits(toBeHashed));
    }

    @Override
    public boolean isSameChecksum(final THashIn first, final THashIn second) {
        //compute the first hash
        final String firstHash = getCheckSum(first);
        //compute the second hash with the same initial state
        final String secondHash = getAlgorithmForState(initialState)
                .encode(bitExtractor.getBits(second));
        //check if same hash
        return firstHash.equals(secondHash);
    }

    private IHashAlgorithm<String, List<Integer>> getAlgorithmForState(final Map.Entry<List<Integer>, List<Integer>> initialState) {
        //instantiate the keccak
        final IKeccak keccak = new Keccak(
                initialState.getKey().size(),       // get r bits size
                initialState.getValue().size(),     // get c bits size
                function,                           // set the function
                initialState.getKey(),              // get rbits
                initialState.getValue()             // get cbits
        );

        //create the hash algorithm
        return new SHA3(
                keccak,
                shaType.get().getOut()
        );
    }

    /**
     * Creates a initial state
     *
     * @return a pair of two integer lists representing the initial state
     */
    private Map.Entry<List<Integer>, List<Integer>> setInitialState() {

        final var parameters = shaType.get();

        if (!multipleHashSameKey && initialState != null) {
            return initialState;
        }

        //generate the initial state's r bits
        final var rBits = Stream.generate(() -> random.nextInt(2))
                .limit(parameters.getR())
                .collect(Collectors.toList());

        //generate the initial state's c bits
        final var cBits = Stream.generate(() -> random.nextInt(2))
                .limit(parameters.getC())
                .collect(Collectors.toList());

        //set the initial state
        this.initialState = new AbstractMap.SimpleEntry<>(rBits, cBits);

        //return the initial state
        return this.initialState;
    }

}
