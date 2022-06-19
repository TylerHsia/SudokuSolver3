package Main;

import java.util.*;

public class Grid implements Iterable<Cell> {

    private final Cell[] cells;

    /**
     * Constructs a new empty sudoku grid
     */
    public Grid(){
        cells = new Cell[81];
        for(int i = 0; i < cells.length; i++){
            cells[i] = new Cell(i / 9, i % 9);
        }
    }

    /**
     * Constructs a sudoku grid from the given input string, where the string must
     * consist of only numbers 0 through 9, 0 corresponding to an empty cell
     * and any other number corresponding to that number in the cell.
     * Cells should be in order from left to right then top to bottom
     * ignores characters in index greater than 80
     * @requires string is in proper format
     */
    public Grid(String board){
        this();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                getCell(r, c).solve(board.charAt(r * 9 + c) - 48);
            }
        }
    }

    /**
     * Solves the cell at the given row and column to the given value
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @param value the value to be solved
     */
    public void solveCell(int row, int column, int value){
        getCell(row, column).solve(value);
    }

    /**
     * Returns the candidates of the cell at the given row and column
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @return the set of candidates at this position
     */
    public Set<Integer> getCands(int row, int column){
        return getCell(row, column).getCands();
    }

    /**
     * Returns the value of the cell at the given row and column
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @return the value of the cell at this position
     */
    public int getVal(int row, int column){
        return getCell(row, column).getVal();
    }

    /**
     * Returns whether the cell is solved at the given row and column
     * @param row the row of the cell to be solved
     * @param column the column of the cell to be solved
     * @return the value of the cell at this position
     */
    public boolean isSolved(int row, int column){
        return getCell(row, column).isSolved();
    }

    /**
     * Returns whether this grid is fully solved in a valid solution
     * @return true iff all cells are solved and there are no duplicates
     */
    public boolean isSolved(){
        return numSolved() == 81 && !hasDuplicate();
    }

    /**
     * Returns the number of solved cells in this grid
     * @return the number of solved cells in this grid
     */
    public int numSolved(){
        int numSolved = 0;
        for(Cell cell: this){
            if(cell.isSolved()){
                numSolved++;
            }
        }
        return numSolved;
    }


    /**
     * Removes value from this cell's candidates
     * @param row the row of the cell to be deleted
     * @param column the column of the cell to be deleted
     * @param value the value to be removed
     * @return true iff this cell's candidates contained value
     */
    public boolean removeCand(int row, int column, int value){
        return getCell(row, column).remove(value);
    }

    /**
     * Returns the set of all solved values in a given row
     * @param row the row to be looked at
     * @return the set of all solved values in a given row
     */
    public Set<Integer> getRow(int row){
        return getVals(getRowCells(row));
    }

    /**
     * Returns the set of all solved values in a given column
     * @param column the column to be looked at
     * @return the set of all solved values in a given column
     */
    public Set<Integer> getColumn(int column){
        return getVals(getColumnCells(column));
    }

    /**
     * Returns the set of all solved values in a given box
     * @param row a row in the box to be looked at
     * @param column a column in the box to be looked at
     * @return the set of all solved values in a given box
     */
    public Set<Integer> getBox(int row, int column){
        return getVals(getBoxCells(row, column));
    }

    /**
     * Returns all of the non -1 values of getVal for each cell in cells
     * @param cells the list of cells to return the values of
     * @return a set of getVal for each cell in cells (-1 excluded)
     */
    private Set<Integer> getVals(List<Cell> cells){
        Set<Integer> vals = new HashSet<>();
        for(Cell cell: cells){
            vals.add(cell.getVal());
        }
        vals.remove(0);
        return vals;
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given row
     * @param row the row to be looked at whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in the row
     */
    public Map<Integer,Integer> getRowCands(int row){
        return getCandsMapping(getRowCells(row));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given column
     * @param column the column to be looked at whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in the column
     */
    public Map<Integer, Integer> getColumnCands(int column){
        return getCandsMapping(getColumnCells(column));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given box
     * @param row a column in the box to be looked at
     * @param column a column in the box to be looked at
     * @return a map of 1-9 to the number of times they are candidates in the box
     */
    public Map<Integer, Integer> getBoxCands(int row, int column){
        return getCandsMapping(getBoxCells(row, column));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in all of the given cells. If a cell only has one candidate,
     * that candidate is not included in the sums
     * @param cells the list of cells whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in cells
     */
    public Map<Integer, Integer> getCandsMapping(List<Cell> cells){
        Map<Integer, Integer> cands = new HashMap<>(9);
        for(int i = 1; i <= 9; i++){
            cands.put(i, 0);
        }
        for(Cell cell: cells){
            if(!cell.isSolved()) {
                for (int cand : cell.getCands()) {
                    cands.put(cand, cands.get(cand) + 1);
                }
            }
        }
        return cands;
    }

    /**
     * Returns the list of all cells in a given row
     * @param row the row of the cells to be returned
     * @return the list of all cells in a given row
     */
    public List<Cell> getRowCells(int row){
        List<Cell> rCells = new ArrayList<>();
        for(int c = 0; c < 9; c++){
            rCells.add(getCell(row, c));
        }
        return rCells;
    }

    /**
     * Returns the list of all cells in a given column
     * @param column the column of the cells to be returned
     * @return the list of all cells in a given column
     */
    public List<Cell> getColumnCells(int column){
        List<Cell> cCells = new ArrayList<>();
        for(int r = 0; r < 9; r++){
            cCells.add(getCell(r, column));
        }
        return cCells;
    }

    /**
     * Returns the list of all cells in a given box
     * @return the list of all cells in a given box
     */
    public List<Cell> getCells(){
        List<Cell> bCells = new ArrayList<>(81);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                bCells.add(getCell(r, c));
            }
        }
        return bCells;
    }

    /**
     * Returns the list of all cells
     * @return the list of all cells
     */
    public List<Cell> getBoxCells(int row, int column){
        List<Cell> bCells = new ArrayList<>();
        int rX = row / 3;
        int cX = column / 3;
        for (int r = rX * 3; r < rX * 3 + 3; r++) {
            for (int c = cX * 3; c < cX * 3 + 3; c++) {
                bCells.add(getCell(r, c));
            }
        }
        return bCells;
    }

    /**
     * Returns a list of all cells, including the cell at the given row, column,
     * that are in the local column, row, or box
     * @param row the row of the cells to be looked at
     * @param column the column of the cells to be looked at
     * @return a list of all cells "seen" by the cell at position (row, column)
     */
    public List<Cell> getSeenCells(int row, int column){
        List<Cell> cells = new ArrayList<>();
        for(int c = 0; c < 9; c++){
            cells.add(getCell(row, c));
        }
        for(int r = 0; r < 9; r++){
            if(r == row){
                continue;
            }
            cells.add(getCell(r, column));
        }
        int rX = row / 3;
        int cX = column / 3;
        for (int r = rX * 3; r < rX * 3 + 3; r++) {
            if(r == row){
                continue;
            }
            for (int c = cX * 3; c < cX * 3 + 3; c++) {
                if(c == column){
                    continue;
                }
                cells.add(getCell(r, c));
            }
        }
        return cells;
    }

    /**
     * Returns the Cell at the given position in this
     * @param row the row of the Cell to be returned
     * @param column the column of the Cell to be returned
     * @return the Cell at the given position in this
     */
    public Cell getCell(int row, int column){
        return cells[row * 9 + column];
    }

    /**
     * Returns a string representation of this grid
     * @return a string representation of this grid
     */
    @Override
    public String toString(){
        StringBuilder stringRepresentation = new StringBuilder();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                stringRepresentation.append(getCell(r, c).toStringNoCands());
            }
            stringRepresentation.append("\n");
        }
        stringRepresentation.append("\n\n\n");

        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                Set<Integer> cands = getCands(r, c);
                for(int i = 1; i <= 9; i++){
                    if(cands.contains(i)){
                        stringRepresentation.append(i);
                    } else {
                        stringRepresentation.append(" ");
                    }
                }
                stringRepresentation.append("|");
            }
            stringRepresentation.append("\n");
        }
        return stringRepresentation.toString();
    }



    //Todo: order methods in terms of accessors then mutators




    /**
     * Check to see if there is an invalid duplicate anywhere in this
     * @return true iff there are two cells in the same row, column, or box
     * with the same solved value
     */
    public boolean hasDuplicate(){
        //check rows and column
        for(int rc = 0; rc < 9; rc++){
            if(hasDuplicate(rowItr(rc))){
                return true;
            }
            if(hasDuplicate(columnItr(rc))){
                return true;
            }
        }
        //check boxes
        for(int box = 0; box < 9; box++){
            if(hasDuplicate(boxItr(box / 3, box % 3))){
                return true;
            }
        }
        return false;
    }

    /**
     * Check to see if this itr iterates over multiple cells that represent the same val
     * does not check for properties of unsolved cells
     * @param itr the iterator over a given set of cells
     * @return true iff this itr contains two cells with the same value
     */
    private boolean hasDuplicate(Iterator<Cell> itr){
        HashSet<Integer> rowVals = new HashSet<>(9);
        while(itr.hasNext()) {
            Cell cell = itr.next();
            if(cell.isSolved() && !rowVals.add(cell.getVal())){
                return true;
            }
        }
        return false;
    }

    /**
     * Check for whether a this.solve(row, column, val) will result in a duplicate
     * and val was a candidate of this cell
     * @param row the row of the cell to be checked
     * @param column the column of the cell to be checked
     * @param val the val to be checked if this.solve(row, column, val) results in
     * a simple invalid state
     * @return true iff val can be solved at this cell resulting in a simple valid state
     */
    public boolean canSolveSimple(int row, int column, int val){
        return(!getRow(row).contains(val) && !getColumn(column).contains(val)
                && !getBox(row, column).contains(val))
                && getCell(row, column).contains(val);
    }

    /**
     * Returns an iterator that will iterate over all cells in the given row
     * @param row the row to be iterated over
     * @return an iterator over the given row
     */
    public Iterator<Cell> rowItr(int row){
        return getRowCells(row).iterator();
    }

    /**
     * Returns an iterator that will iterate over all cells in the given column
     * @param column the column to be iterated over
     * @return an iterator over the given column
     */
    public Iterator<Cell> columnItr(int column){
        return getColumnCells(column).iterator();
    }

    /**
     * Returns an iterator that will iterate over all cells in the given box
     * @param row the row of the box to be iterated over
     * @param column the column of the box to be iterated over
     * @return an iterator over the given box
     */
    public Iterator<Cell> boxItr(int row, int column){
        return getBoxCells(row, column).iterator();
    }

    /**
     * Returns an iterator that will iterate over all cells in this grid
     * iterates from top left to bottom right, left to right first
     * @return an iterator over all cells
     */
    public Iterator<Cell> iterator(){
        return Arrays.asList(cells).iterator();
    }

    /**
     * Returns a deep clone of this grid
     * @return a deep clone
     */
    public Grid clone(){
        Grid copy = new Grid();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                Cell cell = getCell(r, c);
                for(int cand = 1; cand <= 9; cand++){
                    if(!cell.contains(cand)){
                        copy.removeCand(r, c, cand);
                    }
                }
            }
        }
        return copy;
    }

    /**
     * checks if this grid has the same candidates for all cells as the other grid
     * @param other the other grid to be checked for equality
     * @return true iff this grid and other have the same cells
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Grid)) {
            return false;
        }
        Grid otherGrid = (Grid) other;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if(!this.getCell(r, c).equals(otherGrid.getCell(r, c))){
                    return false;
                }
            }
        }
        return true;
    }
}