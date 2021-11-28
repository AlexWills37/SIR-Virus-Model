/**
 * SIR Cellular Automata v3
 * 
 * Compile: javac MixedSIR.java
 * Execute: java MixedSIR <percentMasked> <infectionRate> <recoveryRate> <maxDays> <gridSize> 
 *              <frameDuration> <outFileName> <percentQuarantine> <percentContactTracing> <percentIntrovert>
 * 
 *      all optional arguments:
 *          precentMasekd: (double) the percentage of the population that is wearing a mask and thus spreading the virus at lower rates
 *              default 0.5
 *          infectionRate: (double) representing the probability a cell will get infected by each infectious neighbor each day
 *              default 0.166
 *          recoveryRate:  (double) representing the probability an infected cell will recover each day
 *              default 0.037
 *          maxDays:       (int) the maximum number of days the simulation will run
 *              default 90
 *          gridSize:      (int) the length of one row of the grid of cells (population count is this value, squared)
 *              default 500
 *          frameDuration: (long) milliseconds between each frame
 *              default 50
 *          outFileName:   (String) the name of the output csv file to create
 *              default "", which outputs a file that uses the other values in the name
 *          percentQuarantine:  (double) the percent of the population that will quarantine when infected
 *              default 0.0
 *          percentContactTracing:  (double) the percent of the population that will quarantine if it's neighbors are infected
 *              default 0.0
 *          percentIntrovert:   (double) the percent of the population that will not be in contact with all of its neighbors
 *              default 0.0
 * 
 *          
 * Example: java SIR 1.0 0.3 0.05 100 75 50 example_file.csv
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
public class MixedSIR extends SIR {
    
    protected double maskedRate;
    protected double quarantineRate;
    protected double contactTracingRate;
    protected double introvertRate;
    
    /**
     * Constructs a MixedSIR simulation, which is the same as an SIR simulation, but utilizes the MixedSIRGrid instead of a normal SIRGrid
     *
     * @param infectionRate the rate at which the virus being studied spreads between two neighboring individuals
     * @param recoveryRate the probability each day that an infected cell will recover
     * @param maxDays the maximum number of days the simulation will run
     * @param size the size of one row of the square population of cells
     * @param frameDuration time (in milliseconds) between each frame of the simulation
     * @param outFileName the name of the output file ("" for default)
     * @param maskedRate the rough percentage of the population that is Masked
     * @param quarantinePercent the percentage of the population that quarantines when infected
     * @param contactTracingPercent the percentage of the population that is contact tracing into quarantine
     * @param introvertRate the percentage of the population that does not interact with all of its neighbors
     */
    public MixedSIR(double infectionRate, double recoveryRate, int maxDays, int size, long frameDuration, String outFileName, double maskedRate,
        double quarantinePercent, double contactTracingPercent, double introvertRate){

        super(infectionRate, recoveryRate, maxDays, size, frameDuration, outFileName);
        this.maskedRate = maskedRate;
        this.quarantineRate = quarantinePercent;
        this.contactTracingRate = contactTracingPercent;
        this.introvertRate = introvertRate;
    }

    /**
     * Helper method to properly initialize the MixedSIRGrid and give the Vis the correct references
     * 
     * NOTE: maybe not the most efficient use of inheritance? The main issue is that (grid) is saved as an SIRGrid,
     *  but to populate the grid, it must be recognized as a MixedSIRGrid. Overwriting the default grid with a newly
     *  populated MixedSIRGrid does not work either because then the Vis has a reference to the old, unpopulated grid
     */
    public void initializeObjects(){

        MixedSIRGrid workingGrid = new MixedSIRGrid(size);

        // Try infection rate of 1 percent
        double initInfectedRate = 0.001;
        workingGrid.populate(initInfectedRate, infectionRate, recoveryRate, maskedRate, quarantineRate, contactTracingRate, introvertRate);

        // Ensure at least 1 cell is infected
        while(workingGrid.getInfectious() < 1){
            initInfectedRate += 0.005;
            workingGrid.populate(initInfectedRate, infectionRate, recoveryRate, maskedRate, quarantineRate, contactTracingRate, introvertRate);
        }

        // Assign new grid to the grid and vis
        this.grid = workingGrid;
        this.vis.setGrid(grid);
    }


    /**
     * The main method for this simulation. Creates and runs a MixedSIR simulation.
     * Commandline arguments can be used to change specifications of the simulation
     * @param args commandline arguments, as specified by the file header for the MixedSIR class.
     *      ex: 1.0 0.3 0.05 100 75 50 example_file.csv
     */
    public static void main(String[] args){
        // Default values
        double maskedRate = 0.5;        // Amount of population wearing masks
        double quarantineRate = 0;
        double contactTracingRate = 0;
        double introvertRate = 0;
        double infectionRate = 0.166;   // Probability that a susceptible cell will become infected by each infectious neighbor
        double recoveryRate = 0.037;    // Probability that an infected individual cell will recover each day
        int maxDays = 90;               // The maximum number of days to simulate
        int gridSize = 500;              // The number of people in one row of the population
        long frameDuration = 50;        // The number of milliseconds to display each frame
        String outFileName = "";        // Name of output file

        // Replace default values with commandline arguments in order: maskedRate infectionRate recoveryRate maxDays gridSize frameDuration outFileName
        if(args.length > 0){
            maskedRate = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            infectionRate = Double.parseDouble(args[1]);
        }
        if(args.length > 2){
            recoveryRate = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            maxDays = Integer.parseInt(args[3]);
        }
        if(args.length > 4){
            gridSize = Integer.parseInt(args[4]);
        }
        if(args.length > 5){
            frameDuration = Long.parseLong(args[5]);
        }
        if(args.length > 6){
            outFileName = args[6];
        }
        if(args.length > 7){
            quarantineRate = Double.parseDouble(args[7]);
        }
        if(args.length > 8){
            contactTracingRate = Double.parseDouble(args[8]);
        }
        if(args.length > 9){
            introvertRate = Double.parseDouble(args[9]);
        }

        // Warn user of unintended results if their percentages do not add up
        if( maskedRate + quarantineRate + contactTracingRate + introvertRate > 1){
            System.out.println("WARNING: Population percentages exceed 100%. Population distribution prioritizes\n" +
                            "\tcontact tracing, then quarantine, then masked, then unmasked cells.");
            System.out.println("This population will have no:\n" + "\tUnmasked cells");
            if(quarantineRate + contactTracingRate + maskedRate >= 1){
                System.out.println("\tIntroverted cells");
            }
            if(quarantineRate + contactTracingRate >= 1){
                System.out.println("\tMasked cells");
            }
            if(contactTracingRate >= 1){
                System.out.println("\tQuarantining cells");
            }
        }
        
        // Create a new cellular automaton
        MixedSIR simulation = new MixedSIR(infectionRate, recoveryRate, maxDays, gridSize, frameDuration, outFileName, maskedRate, 
                quarantineRate, contactTracingRate, introvertRate);
        simulation.initializeObjects();
        // Simulate model
        simulation.simulate();
    }
}
