package hash.keccak.steps.impl;

import hash.keccak.steps.IStep;
import utils.keccak.IMatrixManipulator;
import utils.keccak.MatrixManipulator;

import java.util.List;

import static utils.bits.BitUtils.applyLogicalOperator;
import static utils.bits.BitUtils.negateLine;

public class Chi implements IStep {

    @Override
    public List<Integer> applyStep(final List<Integer> inBits, final int... args) {

        //check the length
        if (inBits.size() != BITS) {
            throw new RuntimeException("The number of bits must be " + BITS);
        }

        //build the matrix manipulator over inBits
        final IMatrixManipulator<Integer> matrixManipulator = MatrixManipulator.build(inBits);

        //apply the function
        for (int x = 0; x < ROWS; ++x) {
            for (int y = 0; y < COLS; ++y) {

                //get the current line
                final var currentLine = matrixManipulator.getZAxisDepthRow(x, y);
                //get the immediate line after the current line
                final var firstLine = matrixManipulator.getZAxisDepthRow((x + 1) % ROWS, y);
                //get the second line after the current line
                final var secondLine = matrixManipulator.getZAxisDepthRow((x + 2) % ROWS, y);

                //compute the new depth line
                final var newDepthLine = applyLogicalOperator(
                        currentLine,               // the current line
                        applyLogicalOperator(
                                negateLine(firstLine), // negate the next line
                                secondLine,      // the operator second member
                                (a, b) -> a & b    // apply AND binary operator
                        ),
                        (a, b) -> a ^ b            // apply XOR binary operator
                );

                //set the depth axis
                matrixManipulator.setZAxisDepthRow(x, y, newDepthLine);
            }
        }

        //return the matrix
        return matrixManipulator.as1DArray();
    }
}
