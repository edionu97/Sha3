package hash.keccak.steps.impl;

import hash.keccak.steps.IStep;
import utils.keccak.IMatrixManipulator;
import utils.keccak.MatrixManipulator;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public class Iota implements IStep {

    //table of round constants
    private static BigInteger[] CONSTANT_TABLE = {
            BigInteger.valueOf(0x0000000000000001L), BigInteger.valueOf(0x0000000000008082L),
            BigInteger.valueOf(0x800000000000808AL), BigInteger.valueOf(0x8000000080008000L),
            BigInteger.valueOf(0x000000000000808BL), BigInteger.valueOf(0x0000000080000001L),
            BigInteger.valueOf(0x8000000080008081L), BigInteger.valueOf(0x8000000000008009L),
            BigInteger.valueOf(0x000000000000008AL), BigInteger.valueOf(0x0000000000000088L),
            BigInteger.valueOf(0x0000000080008009L), BigInteger.valueOf(0x000000008000000AL),
            BigInteger.valueOf(0x000000008000808BL), BigInteger.valueOf(0x800000000000008BL),
            BigInteger.valueOf(0x8000000000008089L), BigInteger.valueOf(0x8000000000008003L),
            BigInteger.valueOf(0x8000000000008002L), BigInteger.valueOf(0x8000000000000080L),
            BigInteger.valueOf(0x000000000000800AL), BigInteger.valueOf(0x800000008000000AL),
            BigInteger.valueOf(0x8000000080008081L), BigInteger.valueOf(0x8000000000008080L),
            BigInteger.valueOf(0x0000000080000001L), BigInteger.valueOf(0x8000000080008008L)
    };

    private static BigInteger parseBigIntegerPositive(final String num) {
        final BigInteger b = new BigInteger(num);
        if (b.compareTo(BigInteger.ZERO) < 0) {
            return b.add(BigInteger.ONE.shiftLeft(64));
        }
        return b;
    }

    @Override
    public List<Integer> applyStep(final List<Integer> inBits, final int... args) {

        //check the length
        if (inBits.size() != BITS) {
            throw new RuntimeException("The number of bits must be " + BITS);
        }

        if (args.length != 1) {
            throw new RuntimeException("The iota function needs the round value");
        }

        //create the matrix manipulator for the original matrix
        final IMatrixManipulator<Integer> matrixManipulator = MatrixManipulator.build(inBits);
        //the round number
        final int roundNr = args[0];

        //get the depthRow and transform it to number
        final String binaryNumber = Stream
                .of(matrixManipulator.getZAxisDepthRow(0, 0))
                .map(Object::toString)
                .reduce("", (k, v) -> k + v);

        final BigInteger depthLineAsNumber = new BigInteger(binaryNumber, 2);

        //obtain the new representation
        final String newLineBinaryValue = addZerosToString(
                parseBigIntegerPositive(
                        depthLineAsNumber.add(CONSTANT_TABLE[roundNr]).toString()
                ).toString(2),
                DEPTH
        );

        //convert from binary to list
        final Integer[] newLineValueDepthRow = new Integer[DEPTH];
        for (int i = 0; i < DEPTH; ++i) {
            newLineValueDepthRow[i] = Integer.parseInt(newLineBinaryValue.charAt(i) + "");
        }

        //set the depth row
        matrixManipulator.setZAxisDepthRow(0, 0, newLineValueDepthRow);

        //return the line
        return matrixManipulator.as1DArray();
    }

    private String addZerosToString(final String toBePadded, final int desiredSize) {

        final StringBuilder stringBuilder = new StringBuilder(toBePadded);

        int nrZerosToBeAdded = desiredSize - toBePadded.length();
        while (nrZerosToBeAdded-- > 0) {
            stringBuilder.insert(0, "0");
        }

        return stringBuilder.toString();
    }

}
