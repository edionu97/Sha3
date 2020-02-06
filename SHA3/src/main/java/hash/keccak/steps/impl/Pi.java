package hash.keccak.steps.impl;

import hash.keccak.steps.IStep;
import utils.keccak.IMatrixManipulator;
import utils.keccak.MatrixManipulator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pi implements IStep {

    @Override
    public List<Integer> applyStep(final List<Integer> inBits, final int ...args) {

        //check the length
        if (inBits.size() != BITS) {
            throw new RuntimeException("The number of bits must be " + BITS);
        }

        //create the matrix manipulator for the original matrix
        final IMatrixManipulator<Integer> matrixManipulatorOriginal = MatrixManipulator.build(inBits);

        //create a matrix manipulator for the new matrix
        final IMatrixManipulator<Integer> matrixManipulatorNew = MatrixManipulator.build(
                Stream.generate(() -> 0).limit(BITS).collect(Collectors.toList())
        );

        //permute the lines
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLS; ++j) {
                final var depthLine = matrixManipulatorOriginal.getZAxisDepthRow(i, j);
                matrixManipulatorNew.setZAxisDepthRow(j, (2 * i + 3 * j) % COLS, depthLine);
            }
        }

        //return the 1D array
        return matrixManipulatorNew.as1DArray();
    }
}
