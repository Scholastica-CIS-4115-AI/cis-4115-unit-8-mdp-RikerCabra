
import java.text.*;

/**
 * MDP implements a Markov Decision Processes in a grid world
 *
 * @author Original author: Tom Gibbons. Updated by: STUDENT NAME HERE
 * @version Spring 2017 version
 */
public class MDP {

    /**
     * Different grids to try out. Uncomment the one grid you want to try. In Netbeans Ctrl-slash toggles the comments on the highlighted lines.
     */
    
//	 int grid[][] = { {0, 0, 0, 100}, 
//			          {0, 0, 0, 0}, 
//			          {0, 0, 0, 0},  
//			          {0, -999, -999, 0}};
    
//	int grid[][] = { { -100, 0, 0, +100 }, 
//			 { 0, 0, 0, 0 }, 
//			 { 0, 0, 0, 0 } };
    int grid[][] = {{0, 0, 0, +100},
    {0, -999, 0, -100},
    {0, 0, 0, 0}};

//	int grid[][] = { {100, 0, 0, 0, 0, 0, 0, 100}, 
//			 {0, 0, 0, 0, 0, 0, 0, 0}, 
//			 {0, 0, 0, 0, 0, -999, 0, 0}, 
//			 {0, 0, 0, 0, 0, -999, -999, 0}, 
//			 {0, 0, -999, 0, 0, 0, 0, 0},
//			 {0, -999, -999, 0, 0, 0, 0, 0}, 
//			 {0, 0, -999, 0, 0, 0, 0, 0}, 
//			 {0, 0, 0, 0, -999, 0, -999, 0}, 
//			 {0, 0, 0, 0, 0, 0, 0, 0}, 
//			 {0, -999, 0, -999, 0, -999, 0, -999}};
    /**
     * Structures for handling the movement in the four directions.
     */
    int delta[][] = {{-1, 0},       // go left, x = x -1, y = y
    {0, -1},                        // go down, x = x, y = y -1
    {1, 0},                         // go right,
    {0, 1}};                        // go up

    char delta_name[] = {'^', '<', 'v', '>'}; // Use these when creating your policy grid.
    double[][] value = new double[grid.length][grid[0].length];
    char[][] policy = new char[grid.length][grid[0].length];
    
     /**
     * Parameters students can change to affect the MDP performance
     */
    final double successProb = 0.8;
    final double failureProb = (1.0 - successProb) / 2.0; // Probability(stepping left) = prob(stepping right) = failure_prob
    final int collisionCost = 0;
    final int wallValue = -999;
    final int stepCostR = 0;


     /**
     * initialize value array to given value
     */
    private void initValues(double newVal) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                value[r][c] = newVal;
            }
        }
    }
     /**
     * print value array
     */
    private void printValues() {
        DecimalFormat df = new DecimalFormat("000");
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                System.out.print(df.format(value[r][c]) + " ");
            }
            System.out.println(" ");
        }
        System.out.println("============================");
    }
     /**
     * initialize policy array to given value
     * --------------------Added by student in participation activity----------------
     */
    private void initPolicy(char newVal) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                policy[r][c] = newVal;
            }
        }
    }
     /**
     * print policy array
     * --------------------Added by student in participation activity----------------
     */
    private void printPolicy() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 0) {
                    //normal open space, print direction
                    System.out.print((policy[r][c]) + " ");
                } else if (grid[r][c] == wallValue) {
                    // wall here
                    System.out.print("X ");
                } else {
                    // must be a goal
                    System.out.print("G ");
                }
            }
            System.out.println(" ");
        }
        System.out.println("============================");
    }
     /**
     * Calculate the value of a single move. x and y is the position of the new
     *    move and prob is the probability of reaching that location
     * @param x and y are the location to move to
     * @param prob is the probabilty between 0.0 and 1.0 of moving to this location
     * @param currVal is the current value we are moving from, in case we hit a wall 
     * @return the value of moving to this location 
     */
    private double move(int x, int y, double prob, double currVal) {
        double val = 0;
        if ((x >= 0) && (x < grid.length) && (y >= 0) && (y < grid[0].length)) {
            if (grid[x][y] == -999) {
                // This is an obstacle, bounce back and return the current value
                val = (currVal - collisionCost) * prob;
            }
            // this location is inside the grid
            val = value[x][y] * prob;
        } else {
            // ran into an exterior wall, bounce back and return the current value
            val = (currVal - collisionCost) * prob;
        }
        return val;
    }
     /**
     * check all possible moves at this location x, y
     * 
     * @return largest value of the possible moves
     */
    private double checkMoves(int x, int y) {
        double newValue = -9999;		// initialize value to a large negative number since we want to find the max of them
        for (int move = 0; move < delta.length; move++) {
            // determine new x and y for after move
            int x2 = x + delta[move][0];
            int y2 = y + delta[move][1];
            double value2 = stepCostR; // track new value for this move
            // check successful move
            // is this move outside grid and to empty spot in grid
            value2 += move(x2, y2, successProb, value[x][y]);
            if ((move == 0) || (move == 2)) {
                // if moving left or right, the two failure states or at y+1 and y-1
                value2 += move(x, y + 1, failureProb, value[x][y]);
                value2 += move(x, y - 1, failureProb, value[x][y]);
            }
            if ((move == 1) || (move == 3)) {
                // if moving down or up, the two failure states or at x+1 and x-1
                value2 += move(x + 1, y, failureProb, value[x][y]);
                value2 += move(x - 1, y, failureProb, value[x][y]);
            }
            //if the new moves gives us a better value save the new value to find the max of all the moves
            if (value2 > newValue) {
                newValue = value2;
                policy[x][y] = delta_name[move];    // ----------------------------------------added by student
            }
        }
        return newValue;
    }
     /**
     * calculate all the values of the MDP
     */

    public void stochasticValue() {
        initValues(0);
        initPolicy(delta_name[0]); 				// ----------------------------------------added by student
        boolean change = true;
        // keep updating the values until they no longer change
        while (change) {
            printValues();
            printPolicy();
            change = false; // assume no change to start
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    // Check for an obstacle
                    if (grid[x][y] == -999) {
                        value[x][y] = 0; 						// this is an obstacle set its value to zero
                    } else if (grid[x][y] == 0) {
                        // is you can move to this location (0 is for open
                        // position, -999 is for blocked position)
                        // check all possible moves at this location. If a change is made, track it
                        double newValue = checkMoves(x, y);
                        if (Math.abs(value[x][y] - newValue) > 1.0) {
                            value[x][y] = newValue;
                            change = true;
                        }
                    } else // this is a goal, check to see if the value has been set
                    if (value[x][y] != grid[x][y]) {
                        // change this value to the goal value since it is a goal we have not seen yet
                        value[x][y] = grid[x][y];
                        change = true;
                    }

                }
            }

        }
        printValues();
        printPolicy(); 							// ----------------------------------------added by student

    }

    // main routine that runs the k-means algorithm
    public static void main(String[] args) {

        MDP mdp = new MDP();
        mdp.stochasticValue();
    }

}
