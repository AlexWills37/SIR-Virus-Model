/**
 * This cell will go into quarantine and not spread the virus if it becomes infected,
 * and it will also go into quarantine if it detects any neighbors that are infectious
 * 
 * @author Alex Wills
 */
public class ContactTracingCell extends QuarantineCell {

    protected boolean inQuarantineNext;

    /**
     * Constructs a ContactTracing cell with the added field of being in quarantine
     * @param initState cell's initial SIR state
     * @param infectionRate probability of spreading virus to neighbors if infectious
     * @param recoveryRate probability of recovering from virus if infectious
     */
    public ContactTracingCell(SIRState initState, double infectionRate, double recoveryRate){
        super(initState, infectionRate, recoveryRate);
        inQuarantine = false;
        inQuarantineNext = false;
    }

    /**
     * Checks cell's current status and neighbors' statuses to determine if it's status will change
     * and/or if the cell will go into quarantine
     */
    public void updateNextState(){
        
        // If quarantined, don't change state, but check if it's safe to come out of quarantine
        if(inQuarantine){

            boolean quarantineOver = true;

            // Stay in quarantine if cell is infected
            if(currentState == SIRState.INFECTIOUS){

                // Try to recover if infected
                if(fate.nextDouble() < recoveryRate){
                    nextState = SIRState.RECOVERED;
                    quarantineOver = true;  // Cell is allowed to come out if it recovers

                // Do not leave quarantine if still infected
                } else {
                    quarantineOver = false;
                }
            }

            // Stay in quarantine if any neighbors are infectious
            for(int i = 0; i < totalNeighbors; i++){
                if(neighborhood[i].getCurrentState() == SIRState.INFECTIOUS){
                    quarantineOver = false;
                }
            }

            // quarantineOver is true only if self and no neighbors are infectious
            if(quarantineOver){
                inQuarantineNext = false;
            }

        // If cell is not in quarantine, check neighbors to see if quarantine is needed
        } else {

            for(int i = 0; i < totalNeighbors; i++){

                // If neighbor is infectious, go into quarantine
                if(neighborhood[i].getCurrentState() == SIRState.INFECTIOUS){

                    // If susceptible, see if neighbor will infect cell before going into quarantine
                    if(currentState == SIRState.SUSCEPTIBLE && fate.nextDouble() < neighborhood[i].getInfectionRate()){
                        nextState = SIRState.INFECTIOUS;
                    }

                    inQuarantineNext = true;
                }
            }

            // If cell is infected, go into quarantine and try to recover
            // This should only occur if cell is initialized Infectious
            if(currentState == SIRState.INFECTIOUS){

                inQuarantineNext = true;
                if(fate.nextDouble() < recoveryRate){
                    nextState = SIRState.RECOVERED;
                }
            }
        }
    }

    /**
     * Make sure that cell does not leave quarantine if neighbors are still infectious (unlike QuarantineCell)
     */
    public void updateCurrentState(){
        currentState = nextState;
        inQuarantine = inQuarantineNext;
    }

    /**
     * Test method to see if the ContactTracing cell is working properly
     * @param args commandline arguments
     */
    public static void main(String[] args){
        double inf = 0.1;
        double rec = 0.03;
        SIRCell cell1 = new ContactTracingCell(SIRState.SUSCEPTIBLE, inf, rec);
        SIRCell cell2 = new ContactTracingCell(SIRState.SUSCEPTIBLE, inf, rec);
        SIRCell cell3 = new ContactTracingCell(SIRState.INFECTIOUS, inf, rec);
        SIRCell cell4 = new ContactTracingCell(SIRState.SUSCEPTIBLE, inf, rec);
        SIRCell cell5 = new ContactTracingCell(SIRState.SUSCEPTIBLE, inf, rec);

        SIRCell[] city = {cell1, cell2, cell3, cell4, cell5};

        for(int i = 1; i < city.length; i++){
            cell1.addNeighbor(city[i]);
        }

        System.out.println(cell1);
        cell1.updateNextState();
        System.out.println(cell1);

    }
}
