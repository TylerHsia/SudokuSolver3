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
                nakedSingle(r, c, changedCoords);
            }
        }
        while(!grid.isSolved() && !changedCoords.isEmpty()){
            int changed = changedCoords.remove();
            int row = changed / 9;
            int column = changed % 9;

            if(!grid.isSolved(row, column)){
                hiddenSingle(row, column, changedCoords);
                nakedCandidatePair(row, column, changedCoords);
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
    public boolean nakedSingle(int row, int column, Queue<Integer> changedCoords){
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
                nakedSingle(coord / 9, coord % 9, changedCoords);
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
     * @param row local row to be checked
     * @param column local column to be checked
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean hiddenSingle(int row, int column, Queue<Integer> changedCoords){
        return
            hiddenSingle(grid.rowItr(row), grid.getRowCands(row), changedCoords) |
            hiddenSingle(grid.columnItr(column), grid.getColumnCands(column), changedCoords) |
            hiddenSingle(grid.boxItr(row, column), grid.getBoxCands(row, column), changedCoords);
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
    private boolean hiddenSingle(Iterator<Cell> itr, Map<Integer, Integer> frequency, Queue<Integer> changedCoords){
        boolean didChange = false;
        for(int cand: frequency.keySet()){
            if(frequency.get(cand) == 1){
                while(itr.hasNext()) {
                    Cell cell = itr.next();
                    if(cell.contains(cand)){
                        cell.solve(cand);
                        nakedSingle(cell.getRow(), cell.getColumn(), changedCoords);
                        didChange = true;
                        break;
                    }
                }
            }
        }
        return didChange;
    }

    /**
     * Checks for two cells in a local container that have two candidates which are the same.
     * This is a naked pair. Removes those candidates from the rest of the container.
     * @param row local row to be checked
     * @param column local column to be checked
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean nakedCandidatePair(int row, int column, Queue<Integer> changedCoords){
        return
            nakedCandidatePair(grid.getRowCells(row), changedCoords) |
            nakedCandidatePair(grid.getColumnCells(column), changedCoords) |
            nakedCandidatePair(grid.getBoxCells(row, column), changedCoords);
    }

    /**
     * Checks for two cells in a local container that have two candidates which are the same.
     * This is a naked pair. Removes those candidates from the rest of the container.
     * @param cells a list of cells of the container to be checked
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    private boolean nakedCandidatePair(List<Cell> cells, Queue<Integer> changedCoords){
        List<Cell> twoCands = keepCandRange(cells, 2, 2);
        List<Cell> moreThanTwoCands = keepCandRange(cells, 3, 9);
        for(int i = 0; i < twoCands.size(); i++){
            Cell one = twoCands.get(i);
            for(int j = i + 1; j < twoCands.size(); j++){
                Cell two = twoCands.get(j);
                //If two distinct cells have same candidate pair
                if(one.equals(two)){
                    for(Cell cell: moreThanTwoCands){
                        boolean didChange = cell.removeAll(twoCands.get(i).getCands());
                        if(didChange && cell.isSolved()){
                            nakedSingle(cell.getRow(), cell.getColumn(), changedCoords);
                        } else if(didChange){
                            changedCoords.add(cell.getCoord());
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean nakedCandidateN(int row, int column, int n, Queue<Integer> changedCoords){
        return false;
    }

    public boolean hiddenCandidatePair(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean hiddenCandidateN(int row, int column, int n, Queue<Integer> changedCoords){
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

    /**
     * Removes all solved cells and cells with more than maxCands or less than minCands candidates
     * from a list of cells
     * @param cells the list of cells for cells to be removed from
     * @param minCands the minimum number of cands to remain in the list
     * @param maxCands the maximum number of cands to remain in the list
     */
    private List<Cell> keepCandRange(List<Cell> cells, int minCands, int maxCands){
        List<Cell> kept = new ArrayList<>();
        for(Cell cell: cells){
            if(cell.isSolved() && cell.size() <= maxCands && cell.size() >= minCands){
                kept.add(cell);
            }
        }
        return kept;
    }

    //Todo: add a check rep for if the current grid is valid
}
