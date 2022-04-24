package Main;

import java.util.*;

// A class which represents the candidates of one cell in a sudoku grid
public class Cell {
    private Set<Integer> candidates;
    private final int ONE_CAND_CAP = 1; // the initial capacity of the set for a cell with one candidate
    private int row;
    private int column;
    private int coord;

    // checks that this cell represents a valid cell of a sudoku grid
    private void checkRep(){
        if(candidates.size() > 9 || candidates.size() < 1){
            throw new IllegalStateException("Size of candidates violated");
        }
    }

    /**
     * Constructs a cell with the candidates 1-9
     */
    public Cell(){
        candidates = new HashSet<>(9);
        fill(candidates);
    }


    public Cell(int row, int column){
        this();
        this.row = row;
        this.column = column;
        this.coord = row * 9 + column;
    }

    public Cell(int row, int column, int value){
        this(value);
        this.row = row;
        this.column = column;
        this.coord = row * 9 + column;
    }

    /**
     * fills the given set with 1 through 9
     * @param candidates the set to be filled
     */
    private void fill(Set<Integer> candidates){
        Collections.addAll(candidates, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * constructs a cell which is solved to value
     * @param value the value of the cell
     * @throws IllegalArgumentException if value < 1 or value > 9
     */
    public Cell(int value){
        checkVal(value);
        candidates = new HashSet<>(ONE_CAND_CAP);
        candidates.add(value);
    }

    /**
     * Solves this cell to the value given. if value is outside range 1-9, does nothing
     * @param value the value to be solved for
     * @throws IllegalStateException if value is in range 1-9 and is not a candidate
     */
    public void solve(int value){
        if(1 <= value && value <= 9){
            if (!contains(value)){
                throw new IllegalStateException();
            }
            candidates = new HashSet<>(ONE_CAND_CAP);
            candidates.add(value);
        }
    }

    /**
     * Removes value from this cell's candidates
     * @param value the value to be removed
     * @return true iff candidates contained value
     */
    public boolean remove(int value){
        boolean removed = candidates.remove(value);
        checkRep();
        return removed;
    }

    /**
     * Removes all values in cands from this cell's candidates
     * @param cands the candidates to be removed
     * @return true iff at least one candidate was removed
     */
    public boolean removeAll(Set<Integer> cands) {
        boolean didChange = false;
        for(int cand: cands){
            didChange |= remove(cand);
        }
        return didChange;
    }


    /**
     *
     * @return whether the current cell has one candidate (it is solved)
     */
    public boolean isSolved(){
        return candidates.size() == 1;
    }

    /**
     * returns the number of candidates in this cell
     * @return the number of candidates in this cell
     */
    public int size(){
        return candidates.size();
    }

    /**
     * retruns whether value is a candidate of this cell
     * @param value the value to be checked for containment
     * @return true iff contains value as a candidate
     */
    public boolean contains(int value){
        return candidates.contains(value);
    }

    /**
     * Returns the solved value of this cell
     * @return returns the only candidate in this cell if it is solved, otherwise returns 0
     */
    public int getVal(){
        if (!isSolved()){
            return 0;
        }
        return candidates.iterator().next();
    }

    /**
     * Returns the list of candidates as an unmodifiable set
     * warning: this list will change as the candidates of this cell change
     * @return an unmodifiable set of the candidates of this list
     */
    public Set<Integer> getCands(){
        return Collections.unmodifiableSet(candidates);
    }

    /**
     * checks if this cell has the same candidates as another cell
     * @param other the other cell to be checked for equality
     * @return true iff this cell and other have the same candidates
     */
    public boolean equals(Object other){
        if(!(other instanceof Cell)){
            return false;
        }
        Cell otherCell = (Cell) other;
        return this.candidates.equals(otherCell.candidates);
    }

    /**
     * checks if a value is in valid range
     * @param value the value to be checked
     * @throws IllegalArgumentException if value is outside of range
     */
    private void checkVal(int value){
        if (value < 1 || value > 9){
            throw new IllegalArgumentException("Value is outside range 1-9");
        }
    }

    /**
     * @return the row of this cell
     */
    public int getRow(){
        return row;
    }

    /**
     * @return the column of this cell
     */
    public int getColumn(){
        return column;
    }

    /**
     * @return the single number coord of this cell, equals to getRow() * 9 + getColumn();
     */
    public int getCoord(){
        return coord;
    }

    @Override
    public String toString(){
        return candidates.toString();
    }

    public String toStringNoCands(){
        if(isSolved()){
            return "" + getVal();
        }
        return " ";
    }
}
