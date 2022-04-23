package Main;

import java.util.*;

public class Solver {
    public Grid grid; // do not modify outside of class
    private Queue<Integer> changedCoords;

    /**
     * Creates a new sudoku solver for the given grid
     * @param grid the grid to be solved
     */
    public Solver(Grid grid){
        this.grid = grid;
        changedCoords = new QueueSet<Integer>();
        for(int i = 0; i < 81; i++){
            changedCoords.add(i);
        }
    }

    /**
     * Solves the grid that is stored in this logically
     * @return true if the grid was solved
     */
    public boolean solve(){
        while(!grid.isSolved() && !changedCoords.isEmpty()){
            int changed = changedCoords.remove();
            int row = changed / 9;
            int column = changed % 9;

            if(grid.isSolved(row, column)){
                removeRookBox(row, column, changedCoords);
            }
        }
        return grid.isSolved();
    }

    /**
     * If the given cell is solved, removes that cells value as a candidate
     * from cells in row, column, and box
     * @param row row of cell
     * @param column column of cell
     * @return true if a change was made
     */
    public boolean removeRookBox(int row, int column, Queue<Integer> changedCoords){
        Queue<Integer> changed = new QueueSet<Integer>();
        int val = grid.getVal(row, column);
        return
            removeFromItr(grid.rowItr(row), val, changedCoords) |
            removeFromItr(grid.columnItr(column), val, changedCoords) |
            removeFromItr(grid.boxItr(row, column), val, changedCoords);
    }

    /**
     * Removes the given value from all non solved cells in the iterator
     * @param itr the iterator of cells to be removed
     * @param value the value to be removed from those cells
     * @return true if a cell was modified
     */
    private boolean removeFromItr(Iterator<Cell> itr, int value, Queue<Integer> changedCoords){
        boolean changed = false;
        while(itr.hasNext()){
            Cell next = itr.next();
            if(!next.isSolved()){
                boolean removed = next.remove(value);
                if(removed){
                    changed = true;
                    changedCoords.add(next.getCoord());
                }
            }
        }
        return changed;
    }
}
