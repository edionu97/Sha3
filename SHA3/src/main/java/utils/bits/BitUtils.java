package utils.bits;

import java.util.function.BiFunction;

public class BitUtils {

    /**
     * This function performs bitwise complement of a current line
     *
     * @param depthLine: the line
     * @return the bitwise complement
     */
    public static Integer[] negateLine(final Integer[] depthLine) {

        //create the newDepthLine
        final var notDeptLine = new Integer[depthLine.length];

        for (int i = 0; i < depthLine.length; ++i) {

            //check the value of the bits
            if (depthLine[i] != 0 && depthLine[i] != 1) {
                throw new RuntimeException("The bits does not have the value in [0,1] interval, found " + depthLine[i]);
            }

            //negate the line
            notDeptLine[i] = depthLine[i] == 0 ? 1 : 0;
        }

        return notDeptLine;
    }

    /**
     * This method perform local binary operator over lines element by element
     *
     * @param depthLineA: the first line
     * @param depthLineB: the second line
     * @return the result after applying the local binary operator
     */
    public static Integer[] applyLogicalOperator(final Integer[] depthLineA,
                                                 final Integer[] depthLineB,
                                                 final BiFunction<Integer, Integer, Integer> logicalOperator) {

        //create the newDepthLine
        final var notDeptLine = new Integer[depthLineA.length];

        if (depthLineA.length != depthLineB.length) {
            throw new RuntimeException("The lines must be of the same length");
        }

        for (int i = 0; i < depthLineA.length; ++i) {

            //check the value of the bits
            if (depthLineA[i] != 0 && depthLineA[i] != 1) {
                throw new RuntimeException("The bits does not have the value in [0,1] interval, found " + depthLineA[i]);
            }

            //check the value of the bits
            if (depthLineB[i] != 0 && depthLineB[i] != 1) {
                throw new RuntimeException("The bits does not have the value in [0,1] interval, found " + depthLineB[i]);
            }

            //compute the line value
            notDeptLine[i] = logicalOperator.apply(depthLineA[i], depthLineB[i]);
        }

        return notDeptLine;
    }

    /**
     * Convert the byte array into object byte array
     * @param bytes: the byte array
     * @return an object byte array
     */
    public static Byte[] convertByteToObject(final byte[] bytes){
        final Byte[] objectByte = new Byte[bytes.length];
        for (int i = 0; i <  bytes.length; i++) {
            objectByte[i] = bytes[i];
        }
        return objectByte;
    }
}
