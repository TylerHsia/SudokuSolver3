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
                for(int n = 2; n <= 7; n++){
                    nakedCandidateN(row, column, n, changedCoords);
                }
                checkRep();
            }
        }
        return grid.isSolved();
    }

    private boolean removeAndCallNaked(int row, int column, int val, Queue<Integer> changedCoords){
        return removeAndCallNaked(grid.getCell(row, column), val, changedCoords);
    }

    private boolean removeAndCallNaked(Cell cell, int val, Queue<Integer> changedCoords){
        if(cell.remove(val)){
            changedCoords.add(cell.getCoord());
            if(cell.isSolved()){
                nakedSingle(cell.getRow(), cell.getColumn(), changedCoords);
            }
            return true;
        }
        return false;
    }

    private boolean removeAllAndCallNaked(int row, int column, Collection<Integer> vals, Queue<Integer> changedCoords){
        return removeAllAndCallNaked(grid.getCell(row, column), vals, changedCoords);
    }

    private boolean removeAllAndCallNaked(Cell cell, Collection<Integer> vals, Queue<Integer> changedCoords){
        boolean didChange = false;
        for(Integer val: vals){
            didChange |= removeAndCallNaked(cell, val, changedCoords);
        }
        return didChange;
    }

    private void solveAndCallNaked(int row, int column, int val, Queue<Integer> changedCoords){
        solveAndCallNaked(grid.getCell(row, column), val, changedCoords);
    }

    private void solveAndCallNaked(Cell cell, int val, Queue<Integer> changedCoords){
        cell.solve(val);
        nakedSingle(cell.getRow(), cell.getColumn(), changedCoords);
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
            return
                removeFromItr(grid.rowItr(row), val, changedCoords) |
                removeFromItr(grid.columnItr(column), val, changedCoords) |
                removeFromItr(grid.boxItr(row, column), val, changedCoords);
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
                didChange |= removeAndCallNaked(next, value, changedCoords);
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
                        solveAndCallNaked(cell, cand, changedCoords);
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
     * This is a naked n-set. Removes those candidates from the rest of the container.
     * @param row the local row to be checked
     * @param column the local column to be checked
     * @param n the size of set of naked candidates to be checked
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean nakedCandidateN(int row, int column, int n, Queue<Integer> changedCoords){
        return
                nakedCandidateN(grid.getRowCells(row), n, changedCoords) |
                nakedCandidateN(grid.getColumnCells(column), n, changedCoords) |
                nakedCandidateN(grid.getBoxCells(row, column), n, changedCoords);
    }

    /**
     * Sets up and calls the recursive naked candidate finder for naked candidate sets
     * of size n
     * @param cells a list of cells for the naked candidate to be found in
     * @param n the size of naked candidate sets. 2 <= n <= 8
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    private boolean nakedCandidateN(List<Cell> cells, int n, Queue<Integer> changedCoords){
        List<Cell> unsolved = keepCandRange(cells, 2, 9);
        List<Cell> nCands = keepCandRange(unsolved, 2, n);

        //if not enough number of cells with 2 to n candidates or not enough unsolved cells such that
        //no change would be made
        if(nCands.size() < n || unsolved.size() < n + 1){
            return false;
        }
        return nakedCandidateNRecursiveHelper(unsolved, nCands, new StackSet(), new Stack<>(),
                n, 0, changedCoords);
    }

    /**
     * Recursive checker for naked candidate sets
     * @param unsolved The list of unsolved cells in the local group
     * @param nCands the list of unsolved cells in the local group with [2,n] candidates
     * @param stackSet a set of cands that are in the current set of cells being looked at
     * @param cellsOnStack the cells that have had their candidates put in stackSet
     *                     by this method
     * @param n the size of the naked candidate set to be checked
     * @param start the start index in nCands to start looking at - keeps track of where to add
     *              cell cands to the start from at the beginning of each method call
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords

     * @return true iff a change was made
     */
    private boolean nakedCandidateNRecursiveHelper(List<Cell> unsolved, List<Cell> nCands,
                                                   StackSet stackSet, Stack<Cell> cellsOnStack,
                                                   int n, int start, Queue<Integer> changedCoords){
        //general idea of method is to recursively consider the set union of all nCands.size() choose n possibilities
        //if there aren't enough remaining cells to be permuted over, return false
        if(nCands.size() - start + cellsOnStack.size() < n){
            return false;
        }
        boolean didChange = false;
        for(int i = start; i < nCands.size(); i++) {
            cellsOnStack.push(nCands.get(i));
            stackSet.push(nCands.get(i).getCands());
            if(cellsOnStack.size() == n){
                if(stackSet.asSet().size() == stackSet.size()){
                    for(Cell cell: unsolved){
                        if(!cellsOnStack.contains(cell)){
                            didChange |= removeAllAndCallNaked(cell, stackSet.asSet(), changedCoords);
                        }
                    }
                }
            } else if (stackSet.size() <= n){
                didChange |= nakedCandidateNRecursiveHelper(unsolved, nCands, stackSet, cellsOnStack, n, start + 1, changedCoords);
            }
            start++;
            stackSet.pop();
            cellsOnStack.pop();
        }
        return didChange;
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
            if(!cell.isSolved() && cell.size() <= maxCands && cell.size() >= minCands){
                kept.add(cell);
            }
        }
        return kept;
    }

    //Todo: add a check rep for if the current grid is valid
}
