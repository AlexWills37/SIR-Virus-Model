/**
 * SIR Cellular Automata v1
 * 
 * Compile: javac SIR.java
 * Execute: java SIR <infectionRate> <recoveryRate> <maxDays> <gridSize> <frameDuration> <outFileName>
 * 
 *      all optional arguments:
 *          infectionRate: (double) representing the probability a cell will get infected by each infectious neighbor each day
 *              default 0.166
 *          recoveryRate:  (double) representing the probability an infected cell will recover each day
 *              default 0.037
 *          maxDays:       (int) the maximum number of days the simulation will run
 *              default 90
 *          gridSize:      (int) the length of one row of the grid of cells (population count is this value, squared)
 *              default 50
 *          frameDuration: (long) milliseconds between each frame
 *              default 50
 *          outFileName:   (String) the name of the output csv file to create
 *              default "", which outputs a file that uses the other values in the name
 * Example: java SIR 0.3 0.05 100 75 50 example_file.csv
 * 
 * This program simulates viral spread and recovery according to an SIR model, where there is a grid of people, each person
 * interacting with its 8 Moore Neighbors. Each person can be Susceptible, Infectious, or Recovered, and its state depends on
 * rates and the states of its neighbors.
 * 
 * The simulation runs until there are no infectious neighbors, OR until the maxDays limit has been reached
 * 
 * The demographics over time will be output into a file, which by defualt is in the program directory
 * with the name "SIR_<infectionRate>_<recoveryRate>_<maxDays>_<gridSize>"
 * 
 * @author Alex Wills
 */


public class SIR implements CellularAutomaton {
    
    protected SIRGrid grid;
    protected final SIRVisImg vis;
    protected final SIRWriter writer;
    protected final int maxDays;
    protected final int totalSize;
    protected final int size;
    protected int day;
    protected double infectionRate;
    protected double recoveryRate;


    /**
     * Construct an SIR model for simulation
     * @param infectionRate the rate for infected cells to spread infecition
     * @param recoveryRate the rate for infected cells to recover
     * @param maxDays the maximum time for the simulation to run 
     * @param size the size of each row in the population, and the number of rows in the population
     * @param frameDuration milliseconds between each frame of the visualization
     * @param outFileName the name of the output file ("" for default filename)
     */
    public SIR(double infectionRate, double recoveryRate, int maxDays, int size, long frameDuration, String outFileName){
        
        // Grid to simulate population
        grid = new SIRGrid( size );

        // Vis to show user population demographics over time
        vis = new SIRVisImg( grid );
        vis.setFrameTime( frameDuration );

        // Writer to record population demographics over time
        writer = new SIRWriter(outFileName);
        writer.open(maxDays, infectionRate, recoveryRate, size);

        // Other feilds belonging to the simulation
        this.maxDays = maxDays;
        this.totalSize = size * size;
        this.size = size;
        this.day = 0;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;
    }


    /**
     * Helper method to populate the SIRGrid and ensure that at least 1 cell is infected
     */
    public void populateGrid(){

        // Try infection rate of 1 percent
        double initInfectedRate = 0.001;
        grid.populate(initInfectedRate, infectionRate, recoveryRate);

        // Ensure at least 1 cell is infected
        while(grid.getInfectious() < 1){
            initInfectedRate += 0.005;
            grid.populate(initInfectedRate, infectionRate, recoveryRate);
        }
    }

    /**
     * Update the Grid population, visuals, and csv writer for 1 day
     */
    public void update(){
        // Update grid and vis
        this.grid.update();
        vis.update(day);

        // Get demographic information
        int[] demographics = grid.getDemographics();
        double[] percentages = new double[3];
        percentages[0] = demographics[0] / (double)totalSize;
        percentages[1] = demographics[1] / (double)totalSize;
        percentages[2] = demographics[2] / (double)totalSize;

        // Write information to csv
        writer.update(day, demographics, percentages);

        // Increment day
        day++;
    }

    /**
     * Runs a simulation on the SIR model until there are no more infected cells,
     * OR until the maxDays has been reached
     */
    public void simulate(){

        day = 0;    // Make sure day starts at 0

        // Keep updating until no more cells infected or maxDays reached
        while(grid.getInfectious() > 0 && day <= maxDays){
            update();
        }

        // Close writer object
        writer.close();
    }


    public static void main(String[] args){

        // Default values
        double infectionRate = 0.166;   // Probability that a susceptible cell will become infected by each infectious neighbor
        double recoveryRate = 0.037;    // Probability that an infected individual cell will recover each day
        int maxDays = 90;               // The maximum number of days to simulate
        int gridSize = 500;              // The number of people in one row of the population
        long frameDuration = 50;        // The number of milliseconds to display each frame
        String outFileName = "";        // Name of output file

        // Replace default values with commandline arguments in order: infectionRate recoveryRate maxDays gridSize frameDuration outFileName
        if(args.length > 0){
            infectionRate = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            recoveryRate = Double.parseDouble(args[1]);
        }
        if(args.length > 2){
            maxDays = Integer.parseInt(args[2]);
        }
        if(args.length > 3){
            gridSize = Integer.parseInt(args[3]);
        }
        if(args.length > 4){
            frameDuration = Long.parseLong(args[4]);
        }
        if(args.length > 5){
            outFileName = args[5];
        }
        

        // Create a new cellular automaton
        SIR simulation = new SIR(infectionRate, recoveryRate, maxDays, gridSize, frameDuration, outFileName);
        simulation.populateGrid();
        // Simulate model
        simulation.simulate();
    }
}
