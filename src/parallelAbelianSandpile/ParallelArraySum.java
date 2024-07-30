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
        this.rowLength = grid[0].length -2;
    }

    @Override
    protected Boolean compute() {
        /**
         * Idea:
         * Turn matrix into sequence.
         * Map sequence index to matrix index
         * To avoid borders, have methods to get row and column index
         * Use sum array algorithm
         */
        if ( (hi - lo) < SEQUENTIAL_CUTOFF) {
            boolean change = false;
            for (int k = lo; k < hi; k++) {
                int i = getRowIndex(k);
                int j = getColumnIndex(k);

                updateGrid[i][j] = (grid[i][j] % 4) +
                                (grid[i-1][j] / 4) +
                                grid[i+1][j] / 4 +
                                grid[i][j-1] / 4 +
                                grid[i][j+1] / 4;

                if (grid[i][j] != updateGrid[i][j]) {
                    change = true;
                }
            }
            return change;
        } else {
            ParallelArraySum left = new ParallelArraySum(grid,updateGrid, lo, (hi+lo)/2);
            ParallelArraySum right= new ParallelArraySum(grid,updateGrid,(hi+lo)/2, hi);

            left.fork();
            boolean rightChange = right.compute();
            boolean leftChange = left.join();

            return rightChange || leftChange;
        }
    }

    private int getRowIndex(int index) {
        return index % (this.rowLength)+ 1;
    }

    private int getColumnIndex(int index) {
        return ( index / (this.rowLength) ) + 1;
    }
}
