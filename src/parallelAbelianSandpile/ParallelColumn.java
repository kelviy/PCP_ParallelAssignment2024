package parallelAbelianSandpile;

import java.util.concurrent.RecursiveTask;

public class ParallelColumn extends RecursiveTask<Boolean> {
    private int columnHigh, columnLow, rowHigh, rowLow;
    private int[][] grid;
    private int[][] updateGrid;
    private static final int SEQUENTIAL_CUTOFF = 1;
            ;

    public ParallelColumn(int columnLow, int columnHigh, int rowLow, int rowHigh, int[][] grid, int[][] updateGrid) {
        this.columnHigh = columnHigh;
        this.columnLow = columnLow;
        this.rowHigh = rowHigh;
        this.rowLow = rowLow;
        this.grid = grid;
        this.updateGrid = updateGrid;
    }

    @Override
    protected Boolean compute() {
        if ( (columnHigh - columnLow) < SEQUENTIAL_CUTOFF) {
            boolean change = false;
            for(int i = rowLow; i < rowHigh; i++) {
                for (int j = columnLow; j < columnHigh; j++) {
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
            }
            return change;
        } else {
            // splitting array
            int split = (columnHigh+columnLow)/2;
            ParallelColumn left = new ParallelColumn(columnLow, split, rowLow, rowHigh, grid,updateGrid);
            ParallelColumn right= new ParallelColumn(split, columnHigh, rowLow, rowHigh, grid,updateGrid);

            left.fork();
            boolean rightChange = right.compute();
            boolean leftChange = left.join();

            // pass up the change flag
            return rightChange || leftChange;
        }
    }
}
