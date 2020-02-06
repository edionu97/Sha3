package hash.keccak.ffunction.impl;

import hash.keccak.ffunction.IFFunction;
import hash.keccak.round.IRound;
import hash.keccak.round.impl.Round;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FKeecakFunction implements IFFunction {

    private final int rBits;
    private final int cBits;
    private static final int ROUNDS_NUMBER = 24;

    public FKeecakFunction(final int rBits, final int cBits) {
        this.cBits = cBits;
        this.rBits = rBits;
    }

    @Override
    public Map.Entry<List<Integer>, List<Integer>> apply(final List<Integer> rBits,
                                                         final List<Integer> cBits) {
        //check the value of r
        if (rBits.size() != this.rBits) {
            throw new RuntimeException("The r value must be of length " + this.rBits);
        }

        //check the value of c
        if (cBits.size() != this.cBits) {
            throw new RuntimeException("The c value must be of length  " + this.cBits);
        }

        //concatenate bits
        List<Integer> inBits = Stream
                .concat(rBits.stream(), cBits.stream())
                .collect(Collectors.toList());

        //apply the rounds
        for (int i = 0; i < ROUNDS_NUMBER; ++i) {
            final IRound<Integer> round = new Round(i);
            inBits = round.apply(inBits);
        }

        //return the r bits and the c bits
        return new AbstractMap.SimpleEntry<>(
                inBits.subList(0, this.rBits),
                inBits.subList(this.rBits, this.rBits + this.cBits)
        );
    }
}
