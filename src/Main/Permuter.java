package Main;

import java.util.*;

public class Permuter {
    /**
     * Randomly permutes a given sudoku grid and returns a permutation of it 
     * @param grid the grid to be permuted, this grid is not changed 
     * @return a new grid that is perumuted
     */
    public static Grid permute(Grid grid) {
        Random random = new Random();
        Grid perturb = grid.clone();

        perturb = changeNumbers(grid, random);
        //50% chance
        if(random.nextBoolean()){
            perturb = reflectTopBottomDiagonal(grid);
        }
        //50% chance
        if(random.nextBoolean()){
            perturb = reflectBottomTopDiagonal(grid);
        }
        //50% chance
        if(random.nextBoolean()){
            perturb = reflectOrigin(grid);
        }
        //50% chance
        if(random.nextBoolean()){
            perturb = flipVertical(grid);
        }
        //50% chance
        if(random.nextBoolean()){
            perturb = flipHorizontal(grid);
        }
        //random rotation
        int numRotations = random.nextInt(4);
        for(int i = 0; i < numRotations; i++){
            perturb = rotateClockwise(grid);
        }

        //random boxrow swaps a random number of times
        int numBoxRowSwaps = random.nextInt(4);
        for(int i = 0; i < numBoxRowSwaps; i++){
            perturb = swapBoxRows(grid, random.nextInt(3), random.nextInt(3));
        }

        //random boxColumn swaps a random number of times
        int numBoxColumnSwaps = random.nextInt(4);
        for(int i = 0; i < numBoxColumnSwaps; i++){
            perturb = swapBoxColumns(grid, random.nextInt(3), random.nextInt(3));
        }

        //radnom number of swap columns in box column
        int numSwapColumnInBoxColumn = random.nextInt(9);
        for(int i = 0; i < numSwapColumnInBoxColumn; i++){
            perturb = swapColumnInBoxColumn(grid, random.nextInt(3), random.nextInt(3), random.nextInt(3));
        }

        //random number of swap row in box row
        int numSwapRowInBowRow = random.nextInt(9);
        for(int i = 0; i < numSwapRowInBowRow; i++){
            perturb = swapRowsInBoxRow(grid, random.nextInt(3), random.nextInt(3), random.nextInt(3));
        }
        return perturb;
    }

    /**
     * Returns a rotated grid 90 degrees clockwise
     * @param grid the grid to be rotated, this grid is not changed
     * @return a new grid that is rotated
     */
    public static Grid rotateClockwise(Grid grid) {
        Grid rotated = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                copyCell(grid.getCell(8 - c, r), rotated.getCell(r, c));
            }
        }
        return rotated;
    }

    /**
     * Returns a rotated grid 90 degrees counter-clockwise
     * @param grid the grid to be rotated, this grid is not changed
     * @return a new grid that is rotated
     */
    public static Grid rotateCounterClockwise(Grid grid){
        return rotateClockwise(rotateClockwise(rotateClockwise(grid)));
    }

    /**
     * Returns a grid reflected across the horizontal 
     * @param grid the grid to be reflected, this grid is not changed
     * @return a new grid that is reflected
     */
    public static Grid flipHorizontal(Grid grid) {
        Grid flipped = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                copyCell(grid.getCell(r, c), flipped.getCell(8 - r, c));
            }
        }
        return flipped;
    }

    /**
     * Returns a grid reflected across the horizontal 
     * @param grid the grid to be reflected, this grid is not changed
     * @return a new grid that is reflected
     */
    public static Grid flipVertical(Grid grid) {
        Grid flipped = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                copyCell(grid.getCell(r, c), flipped.getCell(r, 8 - c));
            }
        }
        return flipped;
    }

    /**
     * Swaps two box columns where a box column is a three wide column contained in 3 
     * boxes aligned vertically
     * @param grid the grid to be swapped, this grid is not changed
     * @param boxColumnOne the index of the first box column to be swapped 
     * @param boxColumnTwo the index of the second box column to be swapped 
     * @requires boxColumnOne is in range [0,2]
     * @requires boxColumnTwo is in range [0,2]
     * @return a new grid that is swapped
     */
    public static Grid swapBoxColumns(Grid grid, int boxColumnOne, int boxColumnTwo) {
        Grid swapped = grid.clone();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (c / 3 == boxColumnOne) {
                    copyCell(grid.getCell(r, boxColumnTwo * 3 + c % 3), swapped.getCell(r, c));
                }
                if (c / 3 == boxColumnTwo) {
                    copyCell(grid.getCell(r, boxColumnOne * 3 + c % 3), swapped.getCell(r, c));
                }
            }
        }
        return swapped;
    }

    /**
     * Swaps two box rows where a box rows is a three wide row contained in 3 
     * boxes aligned horizontally
     * @param grid the grid to be swapped, this grid is not changed
     * @param boxRowOne the index of the first box row to be swapped 
     * @param boxRowTwo the index of the second box row to be swapped 
     * @requires boxRowOne is in range [0,2]
     * @requires boxRowTwo is in range [0,2]
     * @return a new grid that is swapped
     */
    public static Grid swapBoxRows(Grid grid, int boxRowOne, int boxRowTwo) {
        Grid swapped = grid.clone();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (r / 3 == boxRowOne) {
                    copyCell(grid.getCell(boxRowTwo * 3 + r % 3, c), swapped.getCell(r, c));
                }
                if (r / 3 == boxRowTwo) {
                    copyCell(grid.getCell( boxRowOne * 3 + r % 3, c), swapped.getCell(r, c));
                }
            }
        }
        return swapped;
    }

    /**
     * Swaps two rows inside a box row
     * @param grid the grid to be swapped, this grid is not changed
     * @param boxRow the index of the row of boxes
     * @param rowAddOne the offset index of the first row to be swapped
     * @param rowAddTwo the offset index of the second row to be swapped
     * @requires boxRow, rowAddOne, rowAddTwo be in range [0,2]
     * @return a new grid that is swapped
     */
    public static Grid swapRowsInBoxRow(Grid grid, int boxRow, int rowAddOne, int rowAddTwo){
        Grid swapped = grid.clone();
        int r1 = boxRow * 3 + rowAddOne;
        int r2 = boxRow * 3 + rowAddTwo;
        for(int c = 0; c < 9; c++){
            copyCell(grid.getCell(r2, c), swapped.getCell(r1, c));
            copyCell(grid.getCell(r1, c), swapped.getCell(r2, c));
        }
        return swapped;
    }

    /**
     * Swaps two columns inside a box column
     * @param grid the grid to be swapped, this grid is not changed
     * @param boxColumn the index of the column of boxes
     * @param columnAddOne the offset index of the first column to be swapped
     * @param columnAddTwo the offset index of the second column to be swapped
     * @requires boxColumn, columnAddOne, columnAddTwo be in range [0,2]
     * @return a new grid that is swapped
     */
    public static Grid swapColumnInBoxColumn(Grid grid, int boxColumn, int columnAddOne, int columnAddTwo){
        Grid swapped = grid.clone();
        int c1 = boxColumn * 3 + columnAddOne;
        int c2 = boxColumn * 3 + columnAddTwo;
        for(int r = 0; r < 9; r++){
            copyCell(grid.getCell(r, c2), swapped.getCell(r, c1));
            copyCell(grid.getCell(r, c1), swapped.getCell(r, c2));
        }
        return swapped;
    }

    /**
     * Returns a grid reflected across the origin 
     * @param grid the grid to be reflected, this grid is not changed
     * @return a new grid that is reflected
     */
    public static Grid reflectOrigin(Grid grid){
        Grid reflected = grid.clone();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                copyCell(grid.getCell(r, c), reflected.getCell(8 - r, 8 - c));
            }
        }
        return reflected;
    }

    /**
     * Returns a grid reflected across the major diagonal from bottom to top 
     * @param grid the grid to be reflected, this grid is not changed
     * @return a new grid that is reflected
     */
    public static Grid reflectBottomTopDiagonal(Grid grid){
        Grid reflected = grid.clone();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                copyCell(grid.getCell(r, c), reflected.getCell(8 - c, 8 - r));
            }
        }
        return reflected;
    }

    /**
     * Returns a grid reflected across the major diagonal from top to bottom 
     * @param grid the grid to be reflected, this grid is not changed
     * @return a new grid that is reflected
     */
    public static Grid reflectTopBottomDiagonal(Grid grid){
        Grid reflected = grid.clone();
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                copyCell(grid.getCell(r, c), reflected.getCell(c, r));
            }
        }
        return reflected;
    }

    /**
     * Returns a grid whose numbers are randomly swapped based off the given Random object
     * For example, replaces all 1s with 5s and all 5s with 4s and so on
     * @param grid the grid to be changed, this grid is not changed
     * @param random the random number generator used for the randomness
     * @return a grid whose numbers have been swapped
     */
    public static Grid changeNumbers(Grid grid, Random random){
        Grid changed = grid.clone();
        ArrayList<Integer> numList = new ArrayList<>();
        Collections.addAll(numList, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(numList, random);
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                Set<Integer> cands = grid.getCell(r, c).getCands();
                Set<Integer> newCands = new HashSet<>();
                for(int cand: cands){
                    newCands.add(numList.get(cand - 1));
                }
                changed.getCell(r, c).setCandidates(newCands);
            }
        }
        return changed;
    }

    /**
     * Sets the candidate set of @parameter to to the same set as from
     * @param from the cell to be copied from
     * @param to the cell to be made a copy of from
     */
    private static void copyCell(Cell from, Cell to){
        to.setCandidates(from.getCands());
    }
}
