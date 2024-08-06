package parallelAbelianSandpile;

import java.util.concurrent.RecursiveTask;

public class ParallelArraySum extends RecursiveTask<Boolean> {

    int hi,lo, rowLength;
    int[][] grid;
    int[][] updateGrid;
    static final int SEQUENTIAL_CUTOFF= 100;

    public ParallelArraySum(int[][] grid, int[][] updateGrid, int lo, int hi) {
        this.grid = grid;
        this.updateGrid = updateGrid;
        this.hi = hi;
        this.lo = lo;
        // -2 to disregard borders
        this.rowLength = grid[0].length -2;
    }

    @Override
    protected Boolean compute() {
        /**
         * Idea:
         * Turn matrix into sequence.
         * Map sequence index to matrix index
         * To avoid borders, have methods to get row and column index of grid
         * Use sum array algorithm
         * return change flag
         */
        if ( (hi - lo) < SEQUENTIAL_CUTOFF) {
            boolean change = false;
            for (int k = lo; k < hi; k++) {
                // re-mapping from sequential index to grid index
                int i = getRowIndex(k);
                int j = getColumnIndex(k);

                // change entry in matrix
                updateGrid[i][j] = (grid[i][j] % 4) +
                                (grid[i-1][j] / 4) +
                                grid[i+1][j] / 4 +
                                grid[i][j-1] / 4 +
                                grid[i][j+1] / 4;

                // checks for change
                if (grid[i][j] != updateGrid[i][j]) {
                    change = true;
                }
            }
            return change;
        } else {
            // splitting array
            int mid = (hi+lo)/2;
            ParallelArraySum left = new ParallelArraySum(grid,updateGrid, lo, mid);
            ParallelArraySum right= new ParallelArraySum(grid,updateGrid,mid , hi);

            left.fork();
            boolean rightChange = right.compute();
            boolean leftChange = left.join();

            // pass up the change flag
            return rightChange || leftChange;
        }
    }

    // remaps 1D array to 2D array. Additionally, avoids border
    private int getRowIndex(int index) {
        return index % (this.rowLength)+ 1;
    }

    // remaps 1D array to 2D array. Additionally, avoid border
    private int getColumnIndex(int index) {
        // no need to floor/ cast to integer since it is integer division. Automatically returns integer result
        return ( index / (this.rowLength) ) + 1;
    }
}
