/**
 * SIRGrid holds many SIRCell objects for an SIR Virus model
 * This class creates all the Cell objects, connects them through their neighborhood,
 * and shares demograhpic information about the population with other classes
 * 
 * @author Alex Wills
 */
public class SIRGrid implements Grid{
    
    protected final SIRCell[][] population;
    protected final int sideSize;

    /**
     * Construct an SIRGrid object without any cells
     * @param size the size for each side of the grid
     */
    public SIRGrid(int size){
        sideSize = size;
        population = new SIRCell[size][size];
    }

    /**
     * Accessor for the number of infectious cells a grid has
     * @return numInfected (int) the number of infectious cells a grid has
     */
    public int getInfectious(){
        int numInfected = 0;
        // Go through all cells and tally up infectious ones
        for(SIRCell[] row : population){
            for(SIRCell cell : row){

                // This increments once for every simulated cell that is currently infectious
                if(cell.getCurrentState() == SIRState.INFECTIOUS){
                    numInfected++;
                }
            }
        }
        //this.numInfected = numInfected;
        return numInfected;
    }

    /**
     * Accessor for the demographics of a grid. 
     * index 0 is the number of susceptible cells
     * index 1 is the number of infectious cells
     * index 2 is the number of recovered cells
     * @return int[] containing demographic information about the cells in a grid
     */
    public int[] getDemographics(){
        
        int numSusceptible = 0;
        int numInfected = 0;
        int numRecovered = 0;

        // Go through all cells and count current states
        for(SIRCell[] row : population){
            for(SIRCell cell : row){

                // If susceptible, tally the numSusceptible
                if(cell.getCurrentState() == SIRState.SUSCEPTIBLE){
                    numSusceptible++;

                // If infectious, tally up numInfected
                } else if (cell.getCurrentState() == SIRState.INFECTIOUS){
                    numInfected++;

                // Else, cell is recovered, so tally that count
                } else {
                    numRecovered++;
                }
            }
        }
        
        // Create array and load values
        int[] demographics = new int[3];
        demographics[0] = numSusceptible;
        demographics[1] = numInfected;
        demographics[2] = numRecovered;

        return demographics;    // Return array
    }

    /**
     * Accessor for grid size
     * @return (int) the number of cells along each side of the gird
     */
    public int getSize(){
        return sideSize;
    }

    /**
     * Accessor for a copy of all the cell's current states. Returns a grid
     * of SIRStates to avoid exposing references to the cells, while still providing
     * relevant information about the population
     * @return 2D array with all the cells' current states
     */
    public SIRState[][] getPopulation(){
        
        // Create 2D States array to hold population information
        SIRState[][] populationStates = new SIRState[sideSize][sideSize];
        
        // Go through every cell and put its state in the array to be returned
        int col;
        for(int row = 0; row < sideSize; row++){
            for(col = 0; col < sideSize; col++){

                populationStates[row][col] = population[row][col].getCurrentState();

            }
        }

        return populationStates;
    }

    /**
     * Ask each cell within the grid to update itself
     */
    public void update(){
        // For each cell in the population, update the next state
        for(SIRCell[] row : population){
            for(SIRCell cell : row){
                cell.updateNextState();
            }
        }

        // Now that all cells know their next state, update their current states
        for(SIRCell[] row : population){
            for(SIRCell cell : row){
                cell.updateCurrentState();
            }
        }
    }

    /**
     * Initialize the population of cells, randomly choosing which cells will start infectious
     * @param initInfectedPercent the probability for each cell to begin the simulation infected
     * @param infectionRate the probability for each cell to spread its infection each turn
     * @param recoveryRate the probability for all infected cells to recover each day
     */
    public void populate(double initInfectedPercent, double infectionRate, double recoveryRate){
        
        // Create all the SIRCells
        int col;
        SIRState initState;
        for(int row = 0; row < sideSize; row++){
            for(col = 0; col < sideSize; col++){

                // This should occur roughly initInfectedPercent of the time
                if(Math.random() < initInfectedPercent){ // Initialize cell to be infectious
                    initState = SIRState.INFECTIOUS;
                } else {
                    initState = SIRState.SUSCEPTIBLE;
                }

                // Create cell
                population[row][col] = new SIRCell(initState, infectionRate, recoveryRate);
            }
        }
        
        // Properly create all of the neighborhoods
        makeNeighborhoods();
    }

    /**
     * Add references to every cell's neighbors to the cell's neighborhoods
     */
    protected void makeNeighborhoods(){

        int dr, dc; // Offset values for the row and columns

        // Iterate through every cell as the center
        int cellCol;
        for(int cellRow = 0; cellRow < sideSize; cellRow++){
            for(cellCol = 0; cellCol < sideSize; cellCol++){

                // Iterate through all Moore neighbors around the cell
                for(dr = -1; dr <= 1; dr++){
                    for(dc = -1; dc <= 1; dc++){

                        // Skip cell if there's no offset (cell shouldnt be its own neighbor)
                        if( dr != 0 || dc != 0){

                            // Make sure neighbor is in-bounds
                            if( (cellRow + dr >= 0 && cellRow + dr < sideSize) && (cellCol + dc >= 0 && cellCol + dc < sideSize)) {
                                // Finally, we know the cell is a neighbor and can add it to the neighborhood!
                                population[cellRow][cellCol].addNeighbor( population[cellRow + dr][cellCol + dc]);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Pretty prints the grid
     *
    public String toString(){
        String grid = "";

        // Demographics string from Professor Eaton's SIRVis setTitle method
        int[] demo = getDemographics();
        int popSize = sideSize * sideSize;
        double s = (double) demo[ SIRState.SUSCEPTIBLE.ordinal() ] / popSize * 100;
        double i = (double) demo[ SIRState.INFECTIOUS.ordinal() ] / popSize * 100;
        double r = (double) demo[ SIRState.RECOVERED.ordinal() ] / popSize * 100;
        String title = String.format("S: %.2f%%, I: %.2f%%, R: %.2f%%", s, i, r);

        grid += title + "\n";

        int col;
        SIRCell cell;
        for(int row = 0; row < sideSize; row++){
            for(col = 0; col < sideSize; col++){
                cell = population[row][col];

                if(cell.getCurrentState() == SIRState.SUSCEPTIBLE){
                    grid += "S ";
                } else if (cell.getCurrentState() == SIRState.INFECTIOUS){
                    grid += "I ";
                } else {
                    grid += "R ";
                }
            }
            if(row < sideSize - 1){
                grid += "\n";
            }
        }

        return grid;
    } 
    */

    public void printCells(){
        for(Cell[] row : population){
            for(Cell cell : row){
                System.out.println(cell);
            }
        }
    }
    
    /**
     * Test the SIRGrid class by creating a Test grid and performing some functions on it
     * @param args
     */
    public static void main(String[] args){
        SIRGrid test = new SIRGrid(10);
        test.populate(0.3, 0.2, 0.3);
        System.out.println(test);
        int time = 1;
        test.update();
        while(test.getInfectious() != 0){
            System.out.println("\nTime: " + time);
            System.out.println(test);
            test.update();
            time++;
        }

        System.out.println("\nTime: " + time);
        System.out.println(test);
    }
}
