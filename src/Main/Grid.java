package Main;

import java.util.*;

public class Grid {

    //make iterators for row, column, or box?

    private final Cell[] cells;

    public Grid(){
        cells = new Cell[81];
        for(int i = 0; i < cells.length; i++){
            cells[i] = new Cell();
        }
    }

    /**
     * Solves the cell at the given row and column to the given value
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @param value the value to be solved
     */
    public void solveCell(int row, int column, int value){
        cells[row * 9 + column].solve(value);
    }

    /**
     * Returns the candidates of the cell at the given row and column
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @return the set of candidates at this position
     */
    public Set<Integer> getCands(int row, int column){
        return cells[row * 9 + column].getCandidates();
    }

    /**
     * Removes value from this cell's candidates
     * @param row the row of the cell to be deleted
     * @param column the column of the cell to be deleted
     * @param value the value to be removed
     * @return true iff this cell's candidates contained value
     */
    public boolean remove(int row, int column, int value){
        return cells[row * 9 + column].remove(value);
    }

    //issolved, getval, size




}
