package Main;
import javax.print.attribute.standard.MediaSize;
import java.util.*;

public class Generator {
    /**
     * Returns whether the given grid has exactly one solution
     * @param grid the grid to be checked
     * @return true iff the grid has exactly one solution
     */
    public static boolean isValid(Grid grid){
        Solver solver = new Solver(grid);
        solver.solve();
        if(grid.hasDuplicate()){
            return false;
        } else if(grid.numSolved() == 81){
            return true;
        }
        return isValid(grid, grid.getCells(), 0);
    }

    /**
     * Returns whether the given grid has exactly one solution
     * @param grid the grid to be checked
     * @param cells the list of all cells in the grid
     * @param
     * @return true iff the grid has exactly one solution
     */
    private static boolean isValid(Grid grid, List<Cell> cells, int index){
        for(int i = index; i < 81; i++){
            Cell cell = cells.get(i);
            if(!cell.isSolved()){
                boolean solvedOne = false;
                Set<Integer> cands = cell.getCands();
                for(int cand: cands){
                    Grid copy = grid.clone();
                    copy.solveCell(cell.getRow(), cell.getColumn(), cand);
                    try{
                        Solver solver = new Solver(copy);
                        solver.solve();
                    } catch(Throwable e){
                        continue;
                    }
                    if(isValid(copy, copy.getCells(), i + 1)){
                        if(solvedOne){
                            return false;
                        } else {
                            solvedOne = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns whether the given grid has exactly one solution
     * @param grid the grid to be checked
     * @return true iff the grid has exactly one solution
     */
    public static boolean isValidSlow(Grid grid){
        Solver solver = new Solver(grid);
        solver.runNakedSingle();
        if(grid.hasDuplicate()){
            return false;
        } else if(grid.numSolved() == 81){
            return true;
        }
        Grid solved = null;
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                if(!grid.isSolved(r, c)){
                    Cell cell = grid.getCell(r, c);
                    for(int cand: cell.getCands()){
                        Grid copy = grid.clone();
                        copy.solveCell(r, c, cand);
                        if(bruteForceSolver(copy)){
                            if(!copy.equals(solved)){
                                if(solved == null){
                                    solved = copy;
                                } else{
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Solves the given grid by brute force
     * @param grid the grid to be solved
     * @return true if the grid was solved
     */
    public static boolean bruteForceSolver(Grid grid){
        int[][] intGrid = new int[9][9];
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                intGrid[r][c] = grid.getVal(r, c);
            }
        }
        bruteForceSolver(intGrid);
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                grid.solveCell(r, c, intGrid[r][c]);
            }
        }
        return grid.isSolved();
    }

    /**
     * Solves the given grid by brute force
     * @param grid the grid to be solved
     * @return true if the grid was solved
     */
    private static boolean bruteForceSolver(int[][] grid){
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                if(grid[r][c] == 0){
                    for(int cand = 1; cand <= 9; cand++){
                        if(isValidPlacement(grid, cand, r, c)){
                            grid[r][c] = cand;
                            if(bruteForceSolver(grid)){
                                return true;
                            } else {
                                grid[r][c] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checker for if a candidate can be placed in a certain position in a grid
     * @param grid the grid to be checked
     * @param cand the cand to be checked
     * @param row the row to be checked
     * @param column the column to be checked
     * @return true iff the cand can be placed in the given grid's row and column
     *         without being a duplicate
     */
    private static boolean isValidPlacement(int[][] grid, int cand, int row, int column){
        for(int r = 0; r < 9; r++){
            if(grid[r][column] == cand){
                return false;
            }
        }
        for(int c = 0; c < 9; c++){
            if(grid[row][c] == cand){
                return false;
            }
        }
        int br = row - row % 3;
        int bc = column - column % 3;
        for(int r = br; r < br + 3; r++){
            for(int c = bc; c < bc + 3; c++){
                if(grid[r][c] == cand){
                    return false;
                }
            }
        }
        return true;
    }
}
