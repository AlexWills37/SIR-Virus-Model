/**
 * SIRCell that goes into quarantine as soon as it becomes infectious, preventing it from spreading the infection
 * 
 * @author Alex Wills
 */
public class QuarantineCell extends SIRCell {
    
    protected boolean inQuarantine;

    /**
     * Constructs a QuarantineCell in the same way as an SIRCell
     * @param initState cell's initial SIRState
     * @param infectionRate probability cell will spread virus to neighbors
     * @param recoveryRate probability cell will recover from virus if infected
     */
    public QuarantineCell(SIRState initState, double infectionRate, double recoveryRate){
        super(initState, infectionRate, recoveryRate);
        inQuarantine = false;
    }


    /**
     * If cell is infectious, go into quarantine. If cell is recovered, leave quarantine
     */
    public void updateCurrentState(){
        currentState = nextState;

        if(currentState == SIRState.INFECTIOUS){
            inQuarantine = true;
        } else {
            inQuarantine = false;
        }
    }

    /**
     * If the cell is in quarantine, it will not spread the virus
     * @return 0 - the cell will never spread the virus
     */
    public double getInfectionRate(){
        double rate = infectionRate;
        // Cell won't spread virus if in quarantine
        if (inQuarantine) {
            rate = 0;
        }

        return rate;
    }
    
    /**
     * Accessor for cell's quarantine status
     * @return the inQuarantine boolean representing if this cell is in quarantine
     */
    public boolean getQuarantine(){
        return inQuarantine;
    }

    /**
     * Pretty print the cell's SIR state and quarantine status
     */
    public String toString(){

        String str = "Cell is " + currentState;
        if(inQuarantine){
            str += " and in quarantine";
        }

        return str;
    }
}
