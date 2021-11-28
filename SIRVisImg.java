import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 *  Defines a visualization tool useful for a Susceptible-Infectious-Recovered (SIR) epidemiological model
 *  of viral spread, in the style of a cellular automaton (CA). <br>
 *  <br>
 *  Can be run directly for testing purposes. The execution command below will:
 *  Create a 600x600 grid of cells and progress through 90 days of the SIR model, demonstrating infection and recovery. <br>
 *  <br>
 *  Compilation:  javac SIRVisImg.java <br>
 *  Execution:    java SIRVisImg   <br>
 *  <br>
 *  @author Caitrin Eaton, 2021
 */
public class SIRVisImg implements Vis {

    // Dimensions
    private final int windowSize;         //  number of pixels along each side of the square window
    private  int cellSize = 1;            //  number of pixels along each side of each square cell within the window
    private final int gridSize;           //  number of cells along each side of the grid
    private long frameTime = 500;         //  duration of each frame (day) in milliseconds

    // Graphics components
    private JFrame window;                 //  the window in which the model's animation is displayed
    private BufferedImage img;             //  the grid of rectangles used to visualize all of the cells

    // SIR components
    private SIRGrid grid;                 //  the grid of references to the actual cell objects themselves
    private SIRState[][] state;           //  a grid in which each cell's state is represented as an enumerated value

    // Cell color code: Susceptible (white), Infectious (green), Recovered (black), Invalid state (light gray)
    private final static Color[] colorCode = { Color.WHITE, Color.GREEN, Color.BLACK, Color.LIGHT_GRAY};

    /**
     * SIRVis constructor, creates an animated visualization for the given grid of cells. <br>
     * @param world SIRGrid, the 2D collection of cells that defines this particular SIR cellular automaton
     */
    public SIRVisImg(SIRGrid world ) {
        // Determine the dimensions of components that will be visualized
        this.grid = world;
        this.gridSize = world.getSize();
        this.windowSize = gridSize*cellSize + 20;

        // Configure the graphics window
        this.window = new JFrame();
        this.img = new BufferedImage( gridSize * cellSize, gridSize*cellSize, BufferedImage.TYPE_INT_RGB );
        initWindow();
        //update( 0 );
    }

    /**
     * Sets the grid the visualization is using to a new grid
     * @param newGrid
     */
    public void setGrid(SIRGrid newGrid){
        grid = newGrid;
    }

    /**
     * Set the window's title string for the current frame of the animation
     * @param day (int) current day of the animation
     */
    private void setTitle( int day ){
        int[] demo = grid.getDemographics();
        int popSize = grid.getSize() * grid.getSize();
        double s = (double) demo[ SIRState.SUSCEPTIBLE.ordinal() ] / popSize * 100;
        double i = (double) demo[ SIRState.INFECTIOUS.ordinal() ] / popSize * 100;
        double r = (double) demo[ SIRState.RECOVERED.ordinal() ] / popSize * 100;
        String title = String.format("Day %d, S: %.2f%%, I: %.2f%%, R: %.2f%%", day, s, i, r);
        this.window.setTitle( title );
    }

    /**
     * Initialize the graphics window and its contents.
     */
    private void initWindow() {

        // Configure the window itself
        window.setSize( windowSize, windowSize );

        // Center the image in the graphics window
        ImageIcon icon = new ImageIcon( img );
        JLabel label = new JLabel( icon );
        window.add( label );

        // Make the graphics window visible
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        long initial_delay_ms = 2000;
        window.repaint( initial_delay_ms );

        // Hold the initial population steady for 2 seconds before starting the simulation
        try {
            Thread.sleep(initial_delay_ms);
        } catch (InterruptedException e) { }
    }

    /**
     * Control the speed of the animation. <br>
     * @param milliseconds long, the duration of a single frame, in milliseconds
     */
    public void setFrameTime(long milliseconds ){
        this.frameTime = milliseconds;
    }

    /**
     * Update the graphical representation of the grid to reflect the grid's current state.
     * @param day (int) current day in the simulation
     */
    public void update( int day ) {
        // Update the current state map
        this.state = this.grid.getPopulation();

        // Use the window's title to track population demographics
        setTitle( day );

        // Configure the color of each cell according to its state: occupied or unoccupied
        state = grid.getPopulation();
        Color c;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++ ){

                // Find the color that reflects this cell's state
                if ( state[row][col] != null ) {
                    c = colorCode[ state[row][col].ordinal() ];
                } else {
                    c = colorCode[ SIRState.values().length ];  // Unrecognized state! Highlight in the ERROR color.
                }

                // in case a cell has more than 1 pixel along each edge
                for( int dr=0; dr<cellSize; dr++) {
                    for (int dc = 0; dc < cellSize; dc++) {
                        img.setRGB( col*cellSize+dc, row*cellSize+dr, c.getRGB() );
                    }
                }
            }
        }

        window.repaint( frameTime );
        try {
            Thread.sleep( frameTime );
        } catch (InterruptedException e) { }
    }

    /**
     * Test the SIRVis class by simulating a small population over the course of 1 week. <br>
     * @param args String[] command line arguments
     */
    public static void main(String[] args) {

        // Initialize a SIRGrid object to drive the game mechanics
        int gridSize = 600;
        int simLength = 90;
        double percentInfected = 0.0001; //20.0 /(gridSize*gridSize);
        double percentMasked = 0.50;
        MixedSIRGrid testGrid = new MixedSIRGrid( gridSize );
        testGrid.populate( percentInfected, 0.166, 0.037, percentMasked, 0, 0, 0 );

        // Initialize a SIRVis object to show the user what's going on in the game
        SIRVisImg testVis = new SIRVisImg( testGrid );
        testVis.setFrameTime( 50 );

        // Run the simulation until either no infectious cells remain or the time limit has been reached
        int day = 0;
        while( testGrid.getInfectious() > 0 && day < simLength ){
            day++;
            testGrid.update();
            // System.out.println( testGrid );
            testVis.update( day );
        }
    }
}
