package parallelAbelianSandpile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParallelSimulation {
    //debugging flag
    static final boolean DEBUG=false;

    //timers - in milliseconds
    static long startTime = 0;
    static long endTime = 0;

    private static void tick(){ //start timing
        startTime = System.currentTimeMillis();
    }
    private static void tock(){ //end timing
        endTime=System.currentTimeMillis();
    }

    // generate array from csv
    public static int [][] readArrayFromCSV(String filePath) {
        int [][] array = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line != null) {
                String[] dimensions = line.split(",");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                System.out.printf("Rows: %d, Columns: %d\n", width, height); //Do NOT CHANGE  - you must ouput this

                array = new int[height][width];
                int rowIndex = 0;

                while ((line = br.readLine()) != null && rowIndex < height) {
                    String[] values = line.split(",");
                    for (int colIndex = 0; colIndex < width; colIndex++) {
                        array[rowIndex][colIndex] = Integer.parseInt(values[colIndex]);
                    }
                    rowIndex++;
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return array;
    }

    public static void main(String[] args) {

        int counter = 0;
        ParallelGrid grid;

        if (args.length!=2) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }
        // Extract argument values
        String inputFileName = args[0];  //input file name
        String outputFileName=args[1]; // output file name

        grid = new ParallelGrid(readArrayFromCSV(inputFileName));

        tick();
        if(DEBUG) {
            System.out.printf("starting config: %d \n",counter);
            grid.printGrid();
        }
        while(grid.update2()) {      //calculations, till sand pile stabilises.
            if(DEBUG) {
                System.out.printf("starting config: %d \n",counter);
                grid.printGrid();
            }
            counter++;
        }
        tock();

        // Output
        System.out.println("Simulation complete, writing image...");
        grid.gridToImage(outputFileName); //write grid as an image - you must do this.
        //Do NOT CHANGE below!
        //simulation details - you must keep these lines at the end of the output in the parallel versions
        System.out.printf("Number of steps to stable state: %d \n",counter);
        System.out.printf("Time: %d ms\n",endTime - startTime );	// Total computation time


    }
}
//input/1001_by_1001_all_8.csv output/log/1top001_parallel.png