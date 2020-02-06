package utils.keccak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatrixManipulator implements IMatrixManipulator<Integer> {

    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int DEPTH = 64;

    private Integer[][][] depthMatrix;

    private MatrixManipulator(final List<Integer> toBeTransformed) {
        this.depthMatrix = as3DMatrix(toBeTransformed);
    }

    public static MatrixManipulator build(final List<Integer> toBeTransformed) {
        return new MatrixManipulator(toBeTransformed);
    }

    @Override
    public Integer[][][] as3DMatrix() {
        return depthMatrix;
    }

    @Override
    public List<Integer> as1DArray() {
        //declare the list
        final List<Integer> list = new ArrayList<>();
        //reconstruct the 1d array
        for (int z = 0; z < DEPTH; ++z) {
            for (int x = 0; x < ROWS; ++x) {
                list.addAll(Arrays.asList(depthMatrix[z][x]).subList(0, COLS));
            }
        }
        //return the list
        return list;
    }

    @Override
    public Integer[] getVerticalColumn(int x, int z) {

        //check if values are negatives so that we reset the values to the end values
        if (x < 0) {
            x = ROWS + x;
        }
        if (z < 0) {
            z = DEPTH + z;
        }

        //keep values between [0,..., ROWS] respectively [0,...,DEPTH]
        x = x % ROWS;
        z = z % DEPTH;

        //create the column
        final Integer[] values = new Integer[COLS];
        for (int y = 0; y < COLS; ++y) {
            values[y] = getZAxisDepthRow(y, x)[z];
        }

        //return the value
        return values;
    }

    @Override
    public void setDepthMatrix(final Integer[][][] depthMatrix) {
        this.depthMatrix = depthMatrix;
    }


    @Override
    public Integer[] getZAxisDepthRow(final int x,
                                      final int y) {

        final Integer[] row = new Integer[DEPTH];
        for (int z = 0; z < DEPTH; ++z) {
            row[z] = depthMatrix[z][x][y];
        }

        return row;
    }

    @Override
    public void setZAxisDepthRow(final int x,
                                 final int y,
                                 final Integer[] depthRow) {
        for (int z = 0; z < DEPTH; ++z) {
            depthMatrix[z][x][y] = depthRow[z];
        }
    }

    /**
     * Split the list into chunks of size ROWS * COLS
     *
     * @param toBeChunked: the list that will be chunked
     * @return a list of chunks
     */
    private List<Integer[][]> splitInChunks(final List<Integer> toBeChunked) {

        //declare the list
        final var chunks = new ArrayList<Integer[][]>();

        //compute the number of repetitions
        final int chunkNumber = (int) Math.ceil((toBeChunked.size() + .0) / (ROWS * COLS));

        //iterate through all chunks of length ROWS * COLS
        for (int chunkIdx = 0; chunkIdx < chunkNumber; ++chunkIdx) {
            //get the boundaries for current chunk
            final int leftIdx = ROWS * COLS * chunkIdx;

            //copy the chunk
            final Integer[][] chunk = new Integer[ROWS][COLS];
            for (int row = 0, bitIdx = leftIdx; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    chunk[row][col] = toBeChunked.get(bitIdx++);
                }
            }

            //add chunk into list
            chunks.add(chunk);
        }

        return chunks;
    }

    /**
     * Converts an 1D array to a 3D matrix
     *
     * @param toBeTransformed: the array that will be converted
     * @return a 3d matrix
     */
    private Integer[][][] as3DMatrix(final List<Integer> toBeTransformed) {

        //spit into chunks
        final List<Integer[][]> chunks = splitInChunks(toBeTransformed);

        //create the 3d matrix
        int depth = 0;
        final var matrix = new Integer[DEPTH][ROWS][COLS];
        for (final var chunk : chunks) {
            matrix[depth++] = chunk;
        }

        //return the matrix
        return matrix;
    }
}
