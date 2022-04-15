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
        vals.remove(-1);
        return vals;
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given row
     * @param row the row to be looked at whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in the row
     */
    public Map<Integer,Integer> numCandsRow(int row){
        return getCands(getRowCells(row));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given column
     * @param column the column to be looked at whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in the column
     */
    public Map<Integer, Integer> numCandsColumn(int column){
        return getCands(getColumnCells(column));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in the given box
     * @param row a column in the box to be looked at
     * @param column a column in the box to be looked at
     * @return a map of 1-9 to the number of times they are candidates in the box
     */
    public Map<Integer, Integer> numCandsBox(int row, int column){
        return getCands(getBoxCells(row, column));
    }

    /**
     * Returns a map of each value 1-9 mapped to the number of times it appears
     * as a candidate in all of the given cells. If a cell only has one candidate,
     * that candidate is includes in the sums
     * @param cells the list of cells whose candidates will be summed
     * @return a map of 1-9 to the number of times they are candidates in cells
     */
    private Map<Integer, Integer> getCands(List<Cell> cells){
        Map<Integer, Integer> cands = new HashMap<>(9);
        for(int i = 1; i <= 9; i++){
            cands.put(i, 0);
        }
        for(Cell cell: cells){
            for(int cand: cell.getCands()){
                cands.put(cand, cands.get(cand) + 1);
            }
        }
        return cands;
    }

    /**
     * Returns the list of all cells in a given row
     * @param row the row of the cells to be returned
     * @return the list of all cells in a given row
     */
    private List<Cell> getRowCells(int row){
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
    private List<Cell> getColumnCells(int column){
        List<Cell> cCells = new ArrayList<>();
        for(int r = 0; r < 9; r++){
            cCells.add(getCell(r, column));
        }
        return cCells;
    }

    /**
     * Returns the list of all cells in a given box
     * @param row a row in the box to be looked at
     * @param column a column in the box to be looked at
     * @return the list of all cells values in a given box
     */
    private List<Cell> getBoxCells(int row, int column){
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
     * Returns the Cell at the given position in this
     * @param row the row of the Cell to be returned
     * @param column the column of the Cell to be returned
     * @return the Cell at the given position in this
     */
    private Cell getCell(int row, int column){
        return cells[row * 9 + column];
    }

    @Override
    public String toString(){
        StringBuilder stringRepresentation = new StringBuilder();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                stringRepresentation.append(getCell(r, c).toString());
            }
            stringRepresentation.append("\n");
        }
        return stringRepresentation.toString();
    }

    public Iterator<Cell> rowItr(int row){
        return getRowCells(row).iterator();
    }

    public Iterator<Cell> columnItr(int column){
        return getColumnCells(column).iterator();
    }

    public Iterator<Cell> boxItr(int row, int column){
        return getBoxCells(row, column).iterator();
    }

    public Iterator<Cell> iterator(){
        return Arrays.asList(cells).iterator();
    }

    //Todo: grid isSolved
    //Todo: grid numSolved
    //Todo: grid has duplicate
    //Todo: can solve/remove without making duplicate
}