package Main;

import java.util.Queue;

@FunctionalInterface
public interface SolverFunction {
    /**
     * A solve function of the Solver class. Must be tied to the correct sudoku grid
     * @param row the row for the solve to be applied to
     * @param column the column for the solve to be applied to
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    abstract boolean solveMethod(int row, int column, Queue<Integer> changedCoords);
}
