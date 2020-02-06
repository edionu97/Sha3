package hash.keccak.steps.impl;

import hash.keccak.steps.IStep;
import utils.keccak.IMatrixManipulator;
import utils.keccak.MatrixManipulator;

import java.util.List;

public class Theta implements IStep {

    @Override
    public List<Integer> applyStep(final List<Integer> inBits, final int ...args) {

        //check the length
        if (inBits.size() != BITS) {
            throw new RuntimeException("The number of bits must be " + BITS);
        }

        //create the matrix manipulator for the original matrix
        final IMatrixManipulator<Integer> matrixManipulator = MatrixManipulator.build(inBits);

        //constructs the new matrix
        final Integer[][][] newMatrix = new Integer[DEPTH][ROWS][COLS];
        for (int z = 0; z < DEPTH; ++z) {
            for (int x = 0; x < ROWS; ++x) {
                for (int y = 0; y < COLS; ++y) {
                    //get the column from left side of (x,y); column (x - 1, z)
                    final Integer[] leftSame = matrixManipulator.getVerticalColumn(x - 1, z);
                    //get the column from the right side of(x,y); column(x + 1, z - 1)
                    final Integer[] rightFront = matrixManipulator.getVerticalColumn(x + 1, z - 1);
                    //the new matrix element value (z,x,y) = xor sum of all neighborhoods
                    newMatrix[z][x][y] = xorSum(leftSame) ^ xorSum(rightFront) ^ matrixManipulator.as3DMatrix()[z][x][y];
                }
            }
        }

        //set the depth matrix to value of the new matrix
        matrixManipulator.setDepthMatrix(newMatrix);

        //get the array
        return matrixManipulator.as1DArray();
    }

    private int xorSum(final Integer[] array) {
        int xor = array[0];
        for (int i = 1; i < COLS; ++i) {
            xor ^= array[i];
        }
        return xor;
    }
}
