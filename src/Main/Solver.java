package Main;

import java.util.*;

public class Solver {
    public Grid grid; // do not modify outside of class
    private Queue<Integer> changedCoords;
    private final boolean DEBUG = true;
    private Set<Integer> removedRookBox; //a hashset of all cell coords that removeRookBox has been called on

    private void checkRep(){
        if(DEBUG){
            assert(!grid.hasDuplicate());
        }
    }

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
        removedRookBox = new HashSet<>(81);
    }

    /**
     * Solves the grid that is stored in this logically
     * @return true if the grid was solved
     */
    public boolean solve(){
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                removeRookBox(r, c, changedCoords);
            }
        }
        while(!grid.isSolved() && !changedCoords.isEmpty()){
            int changed = changedCoords.remove();
            int row = changed / 9;
            int column = changed % 9;

            if(!grid.isSolved(row, column)){
                onlyCandidateLeft(row, column, changedCoords);
                checkRep();
            }
        }
        return grid.isSolved();
    }

    /**
     * If the given cell is solved, removes that cells value as a candidate
     * from cells in row, column, and box
     * @param row row of cell
     * @param column column of cell
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean removeRookBox(int row, int column, Queue<Integer> changedCoords){
        boolean didChange = false;
        if(grid.isSolved(row, column) && !removedRookBox.contains(row * 9 + column)){
            removedRookBox.add(row * 9 + column);
            int val = grid.getVal(row, column);
            Queue<Integer> changed = new QueueSet<Integer>();
            removeFromItr(grid.rowItr(row), val, changed);
            removeFromItr(grid.columnItr(column), val, changed);
            removeFromItr(grid.boxItr(row, column), val, changed);

            changedCoords.addAll(changed);
            didChange = !changedCoords.isEmpty();
            for(int coord: changed){
                removeRookBox(coord / 9, coord % 9, changedCoords);
            }
        }
        return didChange;
    }

    /**
     * Removes the given value from all non solved cells in the iterator
     * @param itr the iterator of cells to be removed
     * @param value the value to be removed from those cells
     * @return true if a cell was modified
     */
    private boolean removeFromItr(Iterator<Cell> itr, int value, Queue<Integer> changedCoords){
        boolean didChange = false;
        while(itr.hasNext()){
            Cell next = itr.next();
            if(!next.isSolved()){
                boolean removed = next.remove(value);
                if(removed){
                    didChange = true;
                    changedCoords.add(next.getCoord());
                }
            }
        }
        return didChange;
    }

    /**
     * Checks for a cell within the given row, column, or local box that is the only
     * cell with a given candidate in that row, column, or box and solves it to that candidate
     * @param row row of cell
     * @param column column of cell
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean onlyCandidateLeft(int row, int column, Queue<Integer> changedCoords){
        return
            onlyCandidateLeft(grid.rowItr(row), grid.getRowCands(row), changedCoords) |
            onlyCandidateLeft(grid.columnItr(column), grid.getColumnCands(column), changedCoords) |
            onlyCandidateLeft(grid.boxItr(row, column), grid.getBoxCands(row, column), changedCoords);
    }

    /**
     * Checks for a cand key in frequency that has frequency 1, then solves its corresponding cell
     * to that cand
     * @param itr the itr of the cells corresponding to the frequency map
     * @param frequency a frequency map corresponding to the get___cands from Grid
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    private boolean onlyCandidateLeft(Iterator<Cell> itr, Map<Integer, Integer> frequency, Queue<Integer> changedCoords){
        boolean didChange = false;
        for(int cand: frequency.keySet()){
            if(frequency.get(cand) == 1){
                while(itr.hasNext()) {
                    Cell cell = itr.next();
                    if(cell.contains(cand)){
                        cell.solve(cand);
                        removeRookBox(cell.getRow(), cell.getColumn(), changedCoords);
                        didChange = true;
                        break;
                    }
                }
            }
        }
        return didChange;
    }

    public boolean nakedCandidate(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean hiddenCandidate(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean candidateLines(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean pointingPairRookToBox(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean xWing(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean forcingChains(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean bruteForce(){
        return false;
    }

    public boolean isValid(){
        return false;
    }

    //Todo: add a check rep for if the current grid is valid
}
