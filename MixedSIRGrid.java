/**
 * Operates the same way as an SIRGrid, but this time populating with SIRCells and MaskedCells
 * along with other types of cells
 * 
 * @author Alex Wills
 */

import java.util.Random;

public class MixedSIRGrid extends SIRGrid{

    /**
     * Constructs a MixedSIRGrid with an empty array of SIRCells, just like the SIRGrid
     * @param size the size of one row in the population (which is a square array)
     */
    public MixedSIRGrid(int size){
        super(size);
    }



    /**
     * Populates the grid with different cells according to their rates
     * 
     * @param initInfectedPercent the percentage of cells to start out infected with the virus
     * @param infectionRate the rate of spread of the virus. The chance a cell will spread the virus to each of its neighbors
     * @param recoveryRate the rate of recovery of the virus. The chance an infected cell will recovery each day
     * @param maskedPercent the percentage of cells to wear masks (3rd priority)
     * @param quarantinePercent the percentage of cells to go into quarantine when infected (2nd priority)
     * @param contactTracingPercent the percentage of cells to go into quarantine if a neighbor is infected (1st prioirty)
     * @param introvertPercent the percentage of cells to avoid all contact with some of its neighbors (4th priority)
     */
    public void populate(double initInfectedPercent, double infectionRate, double recoveryRate, double maskedPercent,
            double quarantinePercent, double contactTracingPercent, double introvertPercent){

        // Random number generator to determine masked and infected cells
        Random fate = new Random();

        // Partition range from 0 to 1 to get probability for determining what kind of cell
        // The chance of being from 0 to contactCutoff is contactTracingPercent, chance of being from
        // that cutoff to the quarantineCutoff is quarantinePercent, etc.
        double contactCutoff = contactTracingPercent;
        double quarantineCutoff = contactTracingPercent + quarantinePercent;
        double maskedCutoff = quarantineCutoff + maskedPercent;
        double introvertCutoff = maskedCutoff + introvertPercent;
        
        // Create all the SIRCells
        SIRState initState;
        int col;
        double random;
        for(int row = 0; row < sideSize; row++){
            for(col = 0; col < sideSize; col++){

                // This should occur roughly initInfectedPercent of the time
                if(fate.nextDouble() < initInfectedPercent){ // Initialize cell to be infectious
                    initState = SIRState.INFECTIOUS;
                } else {
                    initState = SIRState.SUSCEPTIBLE;
                }

                // Determine which kind of cell this cell will be. The first cutoff the random double doesn't meet determines the type
                random = fate.nextDouble();
        
                if( random < contactCutoff ){
                    population[row][col] = new ContactTracingCell(initState, infectionRate, recoveryRate);
                } else if ( random < quarantineCutoff ){
                    population[row][col] = new QuarantineCell(initState, infectionRate, recoveryRate);
                } else if ( random < maskedCutoff){
                    population[row][col] = new MaskedCell(initState, infectionRate, recoveryRate);
                } else if ( random < introvertCutoff){
                    population[row][col] = new IntrovertCell(initState, infectionRate, recoveryRate);
                } else {
                    population[row][col] = new SIRCell(initState, infectionRate, recoveryRate);
                }
            }
        }
        
        // Properly create all of the neighborhoods
        makeNeighborhoods();
    }


    /**
     * Pretty prints a grid's population
     * Masked cells are denoted with brackets
     * Susceptible cells are not marked
     * Infectious cells are marked with I
     * Recovered cells are marked with R
     * 
     * Cells will be followed by a flag:
     * Unmasked cell        
     * Masked cell              `
     * Quarantine cell          =
     * Contact tracing cell     ~
     * Introvert cell           \
     * 
     * @return a string representation of the grid
     */
    public String toString(){

        String str = "";
        for(SIRCell[] row : population){
            for(SIRCell cell : row){

                // represent quarantined cells with brackets
                if(cell instanceof QuarantineCell ){
                    if( ((QuarantineCell)cell).getQuarantine()){
                        if (cell.getCurrentState() == SIRState.SUSCEPTIBLE){
                            str += "[ ]";
                        } else if (cell.getCurrentState() == SIRState.INFECTIOUS){
                            str += "[I]";
                        } else if (cell.getCurrentState() == SIRState.RECOVERED){
                            str += "[R]";
                        }
                    } else {
                        if (cell.getCurrentState() == SIRState.SUSCEPTIBLE){
                        str += "   ";
                        } else if (cell.getCurrentState() == SIRState.INFECTIOUS){
                                str += " I ";
                        } else if (cell.getCurrentState() == SIRState.RECOVERED){
                                str += " R ";
                        }
                    } 
                } else {
                    if (cell.getCurrentState() == SIRState.SUSCEPTIBLE){
                        str += "   ";
                    } else if (cell.getCurrentState() == SIRState.INFECTIOUS){
                        str += " I ";
                    } else if (cell.getCurrentState() == SIRState.RECOVERED){
                        str += " R ";
                    }
                } 
                
                // Masked cells get a - flag
                if (cell instanceof MaskedCell){
                    str += "`";
                // Quarantine cells get a = flag 
                } else if (cell.getClass() == QuarantineCell.class) {
                    str += "=";
                } else if (cell instanceof ContactTracingCell){
                    str += "~";
                } else if (cell instanceof IntrovertCell){
                    str += "\\";
                } else {
                    str += " ";
                }
                
            }
            str += "\n";
        }

        return str;
    } //*/



    public void printCells(){
        for(Cell[] row : population){
            for(Cell cell : row){
                System.out.println(cell);
            }
        }
    }

    /**
     * testing method for MixedSIRGrid
     */
    public static void main(String[] args){
        MixedSIRGrid city = new MixedSIRGrid(40);
        city.populate(0.01, 0.3, 0.2, 0.2, 0.2, 0.2, 0.2);
        System.out.println(city);
        for(int i = 0; i < 10; i++){
            city.update();
            System.out.println("\nDAY " + i + "\n" + city);
        }
    }
}