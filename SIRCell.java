/**
 * SIRCell objects to represent people in an SIR Virus model
 * Cells can have states based on the SIRState enum class
 * Cells can figure out their next state based on their neighbors, and then update to that state
 * Publicly facing, cells can return their current state and next state
 * 
 * @author Alex Wills
 */

import java.util.Random;

public class SIRCell implements Cell {

    // Fields that belong to individual cells
    protected SIRCell[] neighborhood; // Array of all neighbors
    protected int totalNeighbors;
    protected static Random fate = new Random();
    
    protected SIRState currentState;
    protected SIRState nextState;
    protected final double infectionRate;   // Chance for this cell to spread infection
    protected final double recoveryRate;    // Chance for this cell to recover from infection

    /**
     * Construct a new cell for an SIR virus model
     * @param initState the initial state of the cell
     * @param infectionRate the rate at which the cell spreads infection
     * @param recoveryRate the rate at which the cell will recover from infection
     */
    public SIRCell(SIRState initState, double infectionRate, double recoveryRate){
        
        // Assign possibly unique values
        currentState = initState;
        this.infectionRate = infectionRate;
        this.recoveryRate = recoveryRate;

        // Initialize default values
        neighborhood = new SIRCell[ NEIGHBORHOOD_SIZE ];
        totalNeighbors = 0;
        nextState = currentState;
        //fate = new Random();
    }


    /**
     * Add a cell to this object's neighborhood (if there's room)
     * @param cell the SIRCell to add to this cell's neighborhood
     */
    public void addNeighbor( Cell cell ){

        // Only add cell if neighborhood is not yet full
        if( totalNeighbors < NEIGHBORHOOD_SIZE ) {
            neighborhood[ totalNeighbors ] = (SIRCell) cell;
            totalNeighbors++;

        } else { // Let the user know if the function did not add a neighbor
            System.out.println("ERROR: A cell had a full neighborhood, but an attempt was made to add another neighbor.");
        }
    }

    /**
     * Accessor for a cell's current state
     * @return this cell's current SIRState (See SIRState.java)
     */
    public SIRState getCurrentState(){
        return currentState;
    }
    
    /**
     * Accessor for a cell's next state
     * @return the cell's next SIRState (See SIRState.java)
     */
    public SIRState getNextState(){
        return nextState;
    }

    /**
     * Update a cell's current state to match its next state
     */
    public void updateCurrentState(){
        currentState = nextState;
    }

    /**
     * Update a cell's next state based on its current status and the status
     * of its neighbors
     */
    public void updateNextState(){

        // If cell is susceptible, it could become infectious
        if(currentState == SIRState.SUSCEPTIBLE){

            // For every infected neighbor, roll the dice to see if this cell will be infected
            double infectRate;
            // Iterate through all neighbors, get their infection rate (rate of spread), and see if the cell will pass on the virus
            for(int i = 0; i < totalNeighbors; i++){ 

                // Only look at neighbors if they are infectious
                if(neighborhood[i].getCurrentState() == SIRState.INFECTIOUS){
                    infectRate = neighborhood[i].getInfectionRate();
                    
                    // infectRate is the chance of this statement occuring, spreaidng the virus
                    if( fate.nextDouble() < infectRate ){
                        nextState = SIRState.INFECTIOUS;
                    }
                }
            }    
        
        // If cell is infectious, it could recover
        } else if (currentState == SIRState.INFECTIOUS){

            // recoveryRate is the chance of the cell recovering
            if( fate.nextDouble() < recoveryRate ){
                nextState = SIRState.RECOVERED;
            }
        }
        // If cell is recovered, it will stay recovered. State will not change
    }

    /**
     * Accessor for a cell's infection rate
     * @return infectionRate (double)
     */
    public double getInfectionRate(){
        return infectionRate;
    }


    /**
     * pretty print a cell's SIR State
     *
    public String toString(){
        return "Cell is " + currentState;
    }*/

    /**
     * used for testing this class
     * @param args
     */
    public static void main(String[] args){

        double inf = 0.2;
        double rec = 0.9;

        SIRCell one = new SIRCell(SIRState.SUSCEPTIBLE, inf, rec);
        SIRCell two = new SIRCell(SIRState.INFECTIOUS, inf, rec);
        one.addNeighbor( new SIRCell(SIRState.INFECTIOUS, inf, rec));
        one.addNeighbor( new SIRCell(SIRState.INFECTIOUS, inf, rec));
        one.addNeighbor( new SIRCell(SIRState.SUSCEPTIBLE, inf, rec));
        one.addNeighbor( new SIRCell(SIRState.SUSCEPTIBLE, inf, rec));
        one.addNeighbor( new SIRCell(SIRState.SUSCEPTIBLE, inf, rec));
        one.addNeighbor( new SIRCell(SIRState.SUSCEPTIBLE, inf, rec));
        one.addNeighbor(two);
      
        for(int i = 0; i < 10; i++){
            one.updateNextState();
            one.updateCurrentState();
            System.out.println(one);


        }


    }

}
