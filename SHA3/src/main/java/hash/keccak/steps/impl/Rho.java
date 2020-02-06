package hash.keccak.steps.impl;

import hash.keccak.steps.IStep;
import utils.keccak.IMatrixManipulator;
import utils.keccak.MatrixManipulator;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Rho implements IStep {

    /*
        this matrix represents the table for rotation constants for
        rho step, translated into a matrix of 5 by 5 elements
     */
    private static final int[][] ROTATION_CONSTANTS = {
            {0, 36, 3, 41, 18},
            {1, 44, 10, 45, 2},
            {62, 6, 43, 15, 61},
            {28, 55, 25, 21, 56},
            {27, 20, 39, 8, 14}
    };


    private IMatrixManipulator<Integer> matrixManipulator;

    @Override
    public List<Integer> applyStep(final List<Integer> inBits, final int ...args) {

        //check if the number of bits is equal to bit number
        if (inBits.size() != BITS) {
            throw new RuntimeException("The number of bits must be of length " + BITS);
        }
        //set the manipulator
        this.matrixManipulator = MatrixManipulator.build(inBits);

        //rotate the lines
        for (int x = 0; x < ROWS; ++x) {
            for (int y = 0; y < COLS; ++y) {
                //rotate the z line
                final var depthLine = rotateZAxisLine(x, y, ROTATION_CONSTANTS[x][y]);
                //reset the line
                matrixManipulator.setZAxisDepthRow(x, y, depthLine);
            }
        }

        //convert the matrix into 1D array
        return matrixManipulator.as1DArray();
    }

    /**
     *
     * @param x: the x-axis coordinate
     * @param y: the y-axis coordinate
     * @param stepNumber: the number of steps to rotate
     * @return a new array of integers representing the rotated line
     */
    private Integer[] rotateZAxisLine(final int x, final int y, int stepNumber) {

        //create a queue with z-axis
        final Queue<Integer> queue = new ArrayDeque<>(
                Arrays.asList(matrixManipulator.getZAxisDepthRow(x, y))
        );

        //rotate the line
        while (stepNumber-- > 0) {
            queue.add(queue.remove());
        }

        //create a new z-axis line
        final Integer[] line = new Integer[queue.size()];
        queue.toArray(line);

        return line;
    }
}
