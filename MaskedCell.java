/**
 * MaskedCell.java is a child of SIRCell.java
 * MaskedCells operate in the same way as SIRCells, but they spread the virus at a 70% lower rate
 * and contract the virus at a 20% lower rate.
 * In other words, having a mask protects others by lowering rates 70%, and protects the wearer by lowering rates 20%
 * 
 * 
 * @author Alex Wills
 */
public class MaskedCell extends SIRCell{

    // Values reported from CDC on impact of wearing a mask
    private final double spreadCoeff = 0.3; // The coefficient to multiply rate of virus spread  (0.3 for 70% decrease)
    private final double contractCoeff = 0.8; // The coefficient to multiply chance that cell will become infected (0.8 for 20% decrease)

    /**
     * Construct a masked cell in the same manner as the SIRCell
     * @param initState the initial SIRState of the cell
     * @param infectionRate the probability for this cell to spread the virus if infectious
     * @param recoveryRate the probability for this cell to recover each day if it is infectious
     */
    public MaskedCell(SIRState initState, double infectionRate, double recoveryRate){
        super(initState, infectionRate, recoveryRate);
    }

    /**
     * Updates the next state of the cell by checking the states of its neighbors and finding the probability of
     * changing to a new state.
     * 
     * Same as the standard SIRCell updateNextState, but the chance of infection is lowered by 20% due to the mask
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

                    infectRate = neighborhood[i].getInfectionRate() * contractCoeff;      // Multiply rate by 0.8 to lower chance by 20%
                    
                    // infectRate is the chance of this statement occuring, spreaidng the virus
                    if( fate.nextDouble() < infectRate ){
                        nextState = SIRState.INFECTIOUS;
                    }
                }
            }    
        
        // If cell is infecitous, it could recover
        } else if (currentState == SIRState.INFECTIOUS){

            // recoveryRate is the chance of the cell recovering
            if( fate.nextDouble() < recoveryRate ){
                nextState = SIRState.RECOVERED;
            }
        }
        // If cell is recovered, it will stay recovered. State will not change
    }

    /**
     * When other cells ask for the infeciton rate, this cell will lower it's infection rate
     * by 70% (multiply by 0.3)
     * @return the infection rate of this cell
     */
    public double getInfectionRate(){
        return infectionRate * spreadCoeff;
    }

    /**
     * testing method for this MaskedCell class
     * @param args commandlne arguments
     */
    public static void main(String[] args){
        MaskedCell celly = new MaskedCell(SIRState.INFECTIOUS, 0.3, 0.3);
        System.out.println(celly);
    }
}