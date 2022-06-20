package Main;

import java.nio.charset.IllegalCharsetNameException;
import java.util.*;

public class Solver {
    public Grid grid; // do not modify outside of class
    private Queue<Integer> changedCoords;
    private final boolean DEBUG = true;
    private Grid solved;
    private Set<Integer> removedRookBox; //a hashset of all cell coords that removeRookBox has been called on

    private void checkRep(){
        if(DEBUG){
            assert(!grid.hasDuplicate());
            if(solved == null){
                solved = grid.clone();
                assert(Generator.bruteForceSolver(solved));
            }
            for(int r = 0; r < 9; r++){
                for(int c = 0; c < 9; c++){
                    assert(grid.getCands(r, c).contains(solved.getVal(r, c)));
                }
            }
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
        runNakedSingle(changedCoords);
        while(!grid.isSolved() && !changedCoords.isEmpty()){
            int changed = changedCoords.remove();
            int row = changed / 9;
            int column = changed % 9;

            if(!grid.isSolved(row, column)){
                hiddenSingle(row, column, changedCoords);
                for(int n = 2; n <= 7; n++){
                    nakedCandidateN(row, column, n, changedCoords);
                }
                pointingCandidates(row, column, changedCoords);
                //System.out.println(row + " " + column);
                claimingCandidates(row, column, changedCoords);
                xWing(row, column, changedCoords);
                xYWing(row, column, changedCoords);
                checkRep();
            }
        }
        return grid.isSolved();
    }

    public boolean runNakedSingle(Queue<Integer> changedCoords){
        boolean changed = false;
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                changed |= nakedSingle(r, c, changedCoords);
            }
        }
        return changed;
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
     * Checks for n cells in a local container that have n candidates which are the same.
     * This is a naked n-set. Removes those candidates from the rest of the container.
     * This is done for n of 2 through 5 inclusive
     * @param row the local row to be checked
     * @param column the local column to be checked
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true if a change was made
     */
    public boolean nakedCandidates(int row, int column, Queue<Integer> changedCoords){
        return nakedCandidateN(row, column, 2, changedCoords)
                | nakedCandidateN(row, column, 3, changedCoords)
                | nakedCandidateN(row, column, 4, changedCoords)
                | nakedCandidateN(row, column, 5, changedCoords);
    }

    /**
     * Checks for n cells in a local container that have n candidates which are the same.
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

    public boolean hiddenCandidates(int row, int column, Queue<Integer> changedCoords){
        return hiddenCandidateN(row, column, 2, changedCoords)
                | hiddenCandidateN(row, column, 3, changedCoords)
                | hiddenCandidateN(row, column, 4, changedCoords);
    }

    public boolean hiddenCandidateN(int row, int column, int n, Queue<Integer> changedCoords){
        return false;
    }

    /**
     * If in a box all candidates of a certain digit are confined to a row or column,
     * that digit cannot appear outside of that block in that row or column.
     * @param row the row of the local box to be looked at
     * @param column the column of the local box to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    public boolean pointingCandidates(int row, int column, Queue<Integer> changedCoords){
        List<Cell> unsolvedInBox = keepCandRange(grid.getBoxCells(row, column), 2, 9);
        Map<Integer, Integer> frequency = grid.getBoxCands(row, column);
        boolean didChange = false;
        for(int cand = 1; cand <= 9; cand++){
            //only look at cands that appear 2 or three times in the box
            if(frequency.get(cand) == 2 || frequency.get(cand) == 3){
                Set<Integer> rowSet = new HashSet<>(9);
                Set<Integer> columnSet = new HashSet<>(9);
                for(Cell cell: unsolvedInBox){
                    if(cell.contains(cand)){
                        rowSet.add(cell.getRow());
                        columnSet.add(cell.getColumn());
                    }
                }
                //if found a pointing set in the row
                if(rowSet.size() == 1){
                    int r = rowSet.iterator().next();
                    for(int c = 0; c < 9; c++){
                        if(!columnSet.contains(c)){
                            didChange |= removeAndCallNaked(r, c, cand, changedCoords);
                        }
                    }
                }
                //if found a pointing set in the column
                if(columnSet.size() == 1){
                    int c = columnSet.iterator().next();
                    for(int r = 0; r < 9; r++){
                        if(!rowSet.contains(r)){
                            didChange |= removeAndCallNaked(r, c, cand, changedCoords);
                        }
                    }
                }
            }
        }
        return didChange;
    }

    /**
     * If in a column or row, all candidates of a certain digit are confined in a single
     * box, that digit cannot appear outside of that row or column in that box
     * @param row the row of the local box to be looked at
     * @param column the column of the local box to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    public boolean claimingCandidates(int row, int column, Queue<Integer> changedCoords){
        return
                claimingCandidates(grid.getRowCells(row), grid.getRowCands(row), changedCoords) |
                claimingCandidates(grid.getColumnCells(column), grid.getColumnCands(column), changedCoords);
    }

    /**
     * If in a column or row, all candidates of a certain digit are confined in a single
     * box, that digit cannot appear outside of that row or column in that box.
     * @param cells list of cells of a column or row to be looked at
     * @param frequency map of candidate values to their frequency in that row or column
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    private boolean claimingCandidates(List<Cell> cells, Map<Integer, Integer> frequency, Queue<Integer> changedCoords){
        List<Cell> unsolved = keepCandRange(cells, 2, 9);
        boolean didChange = false;

        for(int cand = 1; cand <= 9; cand++){
            //only look at cands that appear 2 or three times in the group
            if(frequency.get(cand) == 2 || frequency.get(cand) == 3){
                Set<Integer> boxCoords = new HashSet<>(9);
                List<Cell> contains = new ArrayList<>();
                for(Cell cell: unsolved){
                    if(cell.contains(cand)){
                        contains.add(cell);
                        boxCoords.add(cell.getColumn() / 3 + 3 * (cell.getRow() / 3));
                    }
                }
                //if found a pointing set in the set
                if(boxCoords.size() == 1){
                    Cell contained = contains.get(0);
                    for(Cell cell: grid.getBoxCells(contained.getRow(), contained.getColumn())){
                        if(!contains.contains(cell)){
                            didChange |= removeAndCallNaked(cell, cand, changedCoords);
                        }
                    }
                }
            }
        }
        return didChange;
    }

    /**
     * If in two columns, all candidates of a specific digit in both rows are contained
     * in the columns, all candidates in the columns that are not part of the rows can be eliminated.
     * Also true for switched rows and columns above.
     * @param row one primary row to be looked at
     * @param column one primary column to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    public boolean xWing(int row, int column, Queue<Integer> changedCoords){
        boolean didChange = xWingRows(grid, row, changedCoords);
        //rotate 90 and do x wings to rotated grid, then merge
        Grid clockwise = Permuter.rotateClockwise(grid);
        Solver clockwiseSolver = new Solver(clockwise);
        didChange |= clockwiseSolver.xWingRows(clockwise, column, new QueueSet<Integer>());
        mergeGrid(Permuter.rotateCounterClockwise(clockwise), changedCoords);
        return didChange;
    }

    /**
     * Performs x wings with row bases.
     * @param grid the grid for x wing to be performed on
     * @param row one base row to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    private boolean xWingRows(Grid grid, int row, Queue<Integer> changedCoords){
        boolean didChange = false;
        Map<Integer, Integer> frequency = grid.getRowCands(row);
        for(int cand: frequency.keySet()){
            if(frequency.get(cand) == 2){
                //Rows will contain the two rows with the candidate
                List<Integer> columns = new ArrayList<>();
                for(Cell cell: grid.getRowCells(row)){
                    if(cell.contains(cand)){
                        columns.add(cell.getColumn());
                    }
                }
                for(int r = 0; r < 9; r++){
                    if(r == row){
                        continue;
                    }
                    Map<Integer, Integer> frequencyTwo = grid.getRowCands(r);
                    //If found an xwing
                    if(frequencyTwo.get(cand) == 2
                            && grid.getCands(r, columns.get(0)).contains(cand)
                            && grid.getCands(r, columns.get(1)).contains(cand)){
                        //remove from the two column in all other rows
                        for(int r2 = 0; r2 < 9; r2++){
                            if(r2 == r || r2 == row){
                                continue;
                            }
                            didChange |= removeAndCallNaked(r2, columns.get(0), cand, changedCoords);
                            didChange |= removeAndCallNaked(r2, columns.get(1), cand, changedCoords);
                        }
                    }
                }
            }
        }
        return didChange;
    }

    /**
     * Takes the grid from, and keeps only the intersection of candidates in each cell of this.grid
     * and the Grid from.
     * @param from the grid to be merged with this.grid
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    private boolean mergeGrid(Grid from, Queue<Integer> changedCoords){
        boolean didChange = false;
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                Set<Integer> gridCands = grid.getCands(r, c);
                Set<Integer> fromCands = from.getCands(r, c);
                if(fromCands.size() < gridCands.size()){
                    for(int cand: gridCands){
                        if(!fromCands.contains(cand)){
                            didChange |= removeAndCallNaked(r, c, cand, changedCoords);
                        }
                    }
                }
            }
        }
        return didChange;
    }

    /**
     * Checks for an xy wing with any of the seen cells of this row and column as a pivot
     * @param row one primary row to be looked at
     * @param column one primary column to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    public boolean xYWing(int row, int column, Queue<Integer> changedCoords){
        boolean didChange = false;
        for(Cell cell: grid.getSeenCells(row, column)){
            didChange |= xYWingOneCell(cell.getRow(), cell.getColumn(), changedCoords);
            checkRep();
        }
        return didChange;
    }

    /**
     * Checks for an xy wing with this row and column as the pivot
     * @param row one primary row to be looked at
     * @param column one primary column to be looked at
     * @param changedCoords a queue of coords. All changed cells will have their coords
     *        added to changedCoords
     * @return true iff a change was made
     */
    private boolean xYWingOneCell(int row, int column, Queue<Integer> changedCoords){
        boolean didChange = false;
        if(grid.getCands(row, column).size() == 2){
            Iterator<Integer> candItr = grid.getCands(row, column).iterator();
            int candOne = candItr.next();
            int candTwo = candItr.next();
            List<Cell> seen = grid.getSeenCells(row, column);
            List<Cell> possiblePincer = keepCandRange(seen, 2, 2);
            possiblePincer.removeIf(next -> !next.contains(candOne) && !next.contains(candTwo)
                    || next.contains(candOne) && next.contains(candTwo));
            Map<Integer, Integer> pincerFrequency = grid.getCandsMapping(possiblePincer);
            for(int cand = 1; cand <= 9; cand++){
                //don't check for a cand already in pivot cell
                if(cand == candOne || cand == candTwo){
                    continue;
                }
                if(pincerFrequency.get(cand) == 2){
                    //found xywing
                    //find pincer cells
                    List<Cell> pincers = new ArrayList<>();
                    for(Cell cell: possiblePincer){
                        if(cell.contains(cand)){
                            pincers.add(cell);
                        }
                    }
                    Cell pincOne = pincers.get(0);
                    Cell pincTwo = pincers.get(1);
                    //if pincers don't have one each of candOne and candTwo - this is not xy wing
                    if(pincOne.contains(candOne) && pincTwo.contains(candOne) ||
                            pincOne.contains(candTwo) && pincTwo.contains(candTwo)){
                        continue;
                    }
                    //if found an x wing, find the intersection of their seen sets
                    List<Cell> setOne = grid.getSeenCells(pincers.get(0).getRow(), pincers.get(0).getColumn());
                    List<Cell> setTwo = grid.getSeenCells(pincers.get(1).getRow(), pincers.get(1).getColumn());
                    Collection<Cell> intersection = new ArrayList<>();
                    for(Cell cell: setTwo){
                        if(setOne.contains(cell)){
                            intersection.add(cell);
                        }
                    }
                    intersection.removeAll(pincers);
                    for(Cell cell: intersection){
                        didChange |= removeAndCallNaked(cell, cand, changedCoords);
                    }
                    if(didChange){
                        //prevent from checking for multiple x wings with non updated frequency counts
                        //x wings will be called again because this row and column will have been added
                        //to changedCoords again
                        return true;
                    }
                }
            }
        }
        return didChange;
    }

    public boolean basicFish(int row, int column, Queue<Integer> changedCoords){
        return false;
    }

    public boolean forcingChains(int row, int column, int length, Queue<Integer> changedCoords){
        return false;
    }

    /**
     * Removes all solved cells and cells with more than maxCands or less than minCands candidates
     * from a list of cells, inclusive
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
}
