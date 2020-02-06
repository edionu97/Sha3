package hash.keccak.round.impl;

import hash.keccak.round.IRound;
import hash.keccak.steps.IStep;
import hash.keccak.steps.impl.*;

import java.util.Arrays;
import java.util.List;

public class Round implements IRound<Integer> {

    private final int roundNumber;

    private final List<IStep> steps;

    public Round(final int roundNumber) {

        //initialize the steps
        steps = Arrays.asList(
                new Theta(),
                new Rho(),
                new Pi(),
                new Chi(),
                new Iota()
        );

        this.roundNumber = roundNumber;
    }

    @Override
    public List<Integer> apply(final List<Integer> inBits) {

        //chain the operators
        var afterProcessing = inBits;
        for (final IStep step : steps) {
            afterProcessing = step.applyStep(afterProcessing, roundNumber);
        }

        return afterProcessing;
    }
}
