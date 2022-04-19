package Main;

import java.util.*;

public class Solver {
    public Grid grid; // do not modify outside of class
    private Queue<Integer> solvedCoords;
    private Queue<Integer> changedCoords;

    /**
     * Creates a new sudoku solver for the given grid
     * @param grid the grid to be solved
     */
    public Solver(Grid grid){
        this.grid = grid;
        changedCoords = new QueueSet();
        for(int i = 0; i < 81; i++){
            changedCoords.add(i);
        }
        solvedCoords = new QueueSet();
        Iterator<Cell> cells = grid.iterator();
        for(int i = 0; i < 81; i++){
            if(cells.next().isSolved()){
                solvedCoords.add(i);
            }
        }
    }

    /**
     * Solves the grid that is stored in this logically
     * @return true if the grid was solved
     */
    public boolean solve(){
        //Todo: fix this logic
        //Todo: try having solve methods return the list of cells modified
        while(!grid.isSolved() && (!solvedCoords.isEmpty() || !changedCoords.isEmpty())){
            while(!grid.isSolved() && !changedCoords.isEmpty()){
                int changed = changedCoords.remove();
                int row = changed / 9;
                int column = changed % 9;
                removeRookBox(row, column);
            }
            while(!grid.isSolved() && !solvedCoords.isEmpty()){
                int solved = solvedCoords.remove();
                int row = solved / 9;
                int column = solved % 9;
                removeRookBox(row, column);
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
    public boolean removeRookBox(int row, int column){
        int val = grid.getVal(row, column);
        boolean c1 = removeFromItr(grid.rowItr(row), val);
        boolean c2 = removeFromItr(grid.columnItr(column), val);
        boolean c3 = removeFromItr(grid.boxItr(row, column), val);
        return c1 || c2 || c3;
    }

    /**
     * Removes the given value from all non solved cells in the iterator
     * @param itr the iterator of cells to be removed
     * @param value the value to be removed from those cells
     * @return true if a cell was modified
     */
    private boolean removeFromItr(Iterator<Cell> itr, int value){
        boolean changed = false;
        while(itr.hasNext()){
            Cell next = itr.next();
            if(!next.isSolved()){
                boolean removed = next.remove(value);
                if(removed){
                    changed = true;
                    changedCoords.add(next.getCoord());
                    if(next.isSolved()){
                        solvedCoords.add(next.getCoord());
                    }
                }
            }
        }
        return changed;
    }




}
