/**
 * IntrovertCells do not interact with all of their neighbors!
 * For each neighbor, there's chance the cell will avoid contact with them for the entire simulation.
 * IntrovertCells will most likely have a neighborhood that lacks some of its moore neighbors
 * 
 * @author Alex Wills
 */
public class IntrovertCell extends SIRCell {

    private double avoidanceRate = 0.5; // The chance a cell will avoid its neighbor

    /**
     * Same constructor as an SIRCell
     * @param initState starting SIR state for this cell
     * @param infectionRate rate that this cell will spread the infection
     * @param recoveryRate chance that cell will recover each day inf infected
     */
    public IntrovertCell(SIRState initState, double infectionRate, double recoveryRate){
        super(initState, infectionRate, recoveryRate);
    }   


    /**
     * Adds a neighbor to this cell's neighborhood based on a random chance, determined by avoidanceRate
     * Not every attempt to add a neighbor will add a neighbor to the neighborhood
     * 
     * @param cell the cell that you are attempting to add to the neighborhood
     */
    public void addNeighbor( Cell cell ){

        // avoidanceRate is the chance the neighbor will not be added
        if(fate.nextDouble() > avoidanceRate){
            super.addNeighbor(cell);
        }
    }
}
