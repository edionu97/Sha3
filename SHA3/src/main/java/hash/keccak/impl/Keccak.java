package hash.keccak.impl;

import hash.keccak.ffunction.IFFunction;
import utils.bits.BitUtils;

import java.util.*;

public class Keccak extends AbstractKeccak {

    private final IFFunction fFunction;
    private final List<Integer> initialCBits;
    private final List<Integer> initialRBits;

    public Keccak(final int rBitsNumber,
                  final int cBitsNumber,
                  final IFFunction fFunction,
                  final List<Integer> initialRBits,
                  final List<Integer> initialCBits) {

        super(rBitsNumber, cBitsNumber);

        if (initialRBits.size() != getRBits()) {
            throw new RuntimeException("The number of r bits should be " + getRBits());
        }

        if (initialCBits.size() != getCBits()) {
            throw new RuntimeException("The number of c bits should be " + getCBits());
        }

        this.initialCBits = initialCBits;
        this.initialRBits = initialRBits;
        this.fFunction = fFunction;
    }

    @Override
    public List<Integer> getBits(final List<Integer> bits) {
        //split into chunks
        final var xSes = preprocess(new ArrayList<>(bits));

        //create the initial state
        Map.Entry<List<Integer>, List<Integer>> bitState =
                new AbstractMap.SimpleEntry<>(initialRBits, initialCBits);

        //declare arrays
        final Integer[] arrayA = new Integer[getRBits()];
        final Integer[] arrayB = new Integer[getRBits()];

        //over each chunk apply the f function
        for (final var x : xSes) {
            final List<Integer> stateR = bitState.getKey();
            //xor the x state with the previous state
            final List<Integer> xorStateX = Arrays.asList(BitUtils.applyLogicalOperator(
                    x.toArray(arrayA),
                    stateR.toArray(arrayB),
                    (a, b) -> a ^ b
            ));
            //apply f function
            bitState = fFunction.apply(new ArrayList<>(xorStateX), bitState.getValue());
        }

        //return the y0
        return bitState.getKey();
    }

    private List<List<Integer>> preprocess(List<Integer> inBits) {
        //pad the bits
        padBits(inBits);

        //split in chunks
        final int chunkN = inBits.size() / getRBits();
        final List<List<Integer>> chunks = new ArrayList<>();

        for (int chunkIdx = 0; chunkIdx < chunkN; ++chunkIdx) {
            chunks.add(inBits.subList(chunkIdx * getRBits(), (chunkIdx + 1) * getRBits()));
        }

        //get the chunks
        return chunks;
    }

}
