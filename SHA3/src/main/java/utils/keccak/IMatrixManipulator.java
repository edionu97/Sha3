package utils.keccak;

import java.util.List;

public interface IMatrixManipulator<T> {

    /**
     * Get the row
     *
     * @param x: the value of x (x-axis)
     * @param y: the value of y (y-axis)
     * @return the line from z-axis
     */
    T[] getZAxisDepthRow(final int x, final int y);

    /**
     * Sets the depth axis for a depthMatrix
     *
     * @param x:        the x-axis coordinate
     * @param y:        the y-axis coordinate
     * @param depthRow: the values that will be set as depth row
     */
    void setZAxisDepthRow(final int x,
                          final int y,
                          final T[] depthRow);

    /**
     * @return the 3d representation
     */
    T[][][] as3DMatrix();

    /**
     * @return the 1D representation
     */
    List<T> as1DArray();

    /**
     * Get the vertical column which is neighbor with the element from (x,Z)
     * The calculations will be made mod 5
     * if x or Z  = -1 than x or Z =  BITS - 1
     * if x or Z = BITS than x or Z = 0
     *
     * @param x: the x coordinate
     * @param z: the z coordinate
     * @return an array which represents the column
     */
    T[] getVerticalColumn(final int x, final int z);

    /**
     * Sets the depth matrix
     * @param depthMatrix: the 3d matrix
     */
    void setDepthMatrix(final T[][][] depthMatrix);
}
