package parallelAbelianSandpile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class ParallelGrid {

    private int rows, columns;
    // serial index size of grid
    private int serialIndex;
    private int [][] grid;
    private int [][] updateGrid;
    private static final ForkJoinPool fjPool = new ForkJoinPool();


    public ParallelGrid(int[][] newGrid) {
        rows = newGrid.length+2; //for the "sink" border
        columns = newGrid[0].length+2; //for the "sink" border

        serialIndex = newGrid.length * newGrid[0].length; // size of serial index

        grid = new int[this.rows][this.columns];
        updateGrid=new int[this.rows][this.columns];

        /* grid  initialization */
        for(int i=0; i<this.rows; i++ ) {
            for( int j=0; j<this.columns; j++ ) {
                grid[i][j]=0;
                updateGrid[i][j]=0;
            }
        }

        // copying over grid information
        //- 1 for not copying over sink border
        for(int i=1; i<rows-1; i++ ) {
            for( int j=1; j<columns-1; j++ ) {
                this.grid[i][j]=newGrid[i-1][j-1];
            }
        }
    }


    //for the next time step - copy updateGrid into grid
    public void nextTimeStep() {
        for(int i=1; i<rows-1; i++ ) {
            for( int j=1; j<columns-1; j++ ) {
                this.grid[i][j]=updateGrid[i][j];
            }
        }
    }

    //key method to calculate the next update grid
    public boolean update() {

        // invoke returns change flag which determines when sand pile stabilises
        if (fjPool.invoke(new ParallelArraySum(grid, updateGrid, 0, serialIndex))) {
            nextTimeStep();
            return true;
        }
        return false;
    }

    // experimental calculation update grid
    public boolean update2() {
        // invoke returns change flag which determines when sand pile stabilises
        if (fjPool.invoke(new ParallelColumn(1, grid.length-1,1,grid[0].length-1, grid, updateGrid))) {
            nextTimeStep();
            return true;
        }
        return false;
    }

    //display the grid in text format
    public void printGrid( ) {
        int i,j;
        //not border is not printed
        System.out.printf("Grid:\n");
        System.out.printf("+");
        for( j=1; j<columns-1; j++ ) System.out.printf("  --");
        System.out.printf("+\n");
        for( i=1; i<rows-1; i++ ) {
            System.out.printf("|");
            for( j=1; j<columns-1; j++ ) {
                if ( grid[i][j] > 0)
                    System.out.printf("%4d", grid[i][j] );
                else
                    System.out.printf("    ");
            }
            System.out.printf("|\n");
        }
        System.out.printf("+");
        for( j=1; j<columns-1; j++ ) System.out.printf("  --");
        System.out.printf("+\n\n");
    }

    // create image from grid information
    public void gridToImage(String fileName) {
        try {
            BufferedImage dstImage =
                    new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);
            //integer values from 0 to 255.
            int a = 0;
            int g = 0;//green
            int b = 0;//blue
            int r = 0;//red

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    g = 0;//green
                    b = 0;//blue
                    r = 0;//red

                    switch (grid[i][j]) {
                        case 0:
                            break;
                        case 1:
                            g = 255;
                            break;
                        case 2:
                            b = 255;
                            break;
                        case 3:
                            r = 255;
                            break;
                        default:
                            break;

                    }
                    // Set destination pixel to mean
                    // Re-assemble destination pixel.
                    int dpixel = (0xff000000)
                            | (a << 24)
                            | (r << 16)
                            | (g << 8)
                            | b;
                    dstImage.setRGB(i, j, dpixel); //write it out


                }
            }

            File dstFile = new File(fileName);
            ImageIO.write(dstImage, "png", dstFile);
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
