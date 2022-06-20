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
        if(grid.numSolved() < 16){
            //there is no known sudoku with fewer than 16 givens
            return false;
        }
        grid = grid.clone();
        Solver solver = new Solver(grid);
        try{
            if(solver.solve()){
                return true;
            }
        } catch(Throwable e){
            return false;
        }

        Grid inOrder = grid.clone();
        Grid revOrder = grid.clone();
        List<Integer> candidates = new ArrayList<>();
        for(int i = 9; i >= 1; i--){
            candidates.add(i);
        }
        boolean solved = bruteForceSolver(inOrder);
        bruteForceSolver(revOrder, candidates);
        return solved && inOrder.equals(revOrder);
    }

    /**
     * Returns whether the given grid has exactly one solution
     * @param grid the grid to be checked
     * @return true iff the grid has exactly one solution
     */
    public static boolean isValidSlow(Grid grid){
        Solver solver = new Solver(grid);
        solver.runNakedSingle(new LinkedList<>());
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
        return solved != null;
    }

    /**
     * Solver for the given grid with the list of solver methods.
     * Naked singles is always run first, regardless of methods
     * @param grid the grid to be solved
     * @param methods the list of solving methods to be used on the grid
     * @return true iff grid was solved
     */
    public static boolean solveWithMethods(Grid grid, List<SolverFunction> methods){
        Queue<Integer> changedCoords = new QueueSet<Integer>();
        for(int i = 0; i < 81; i++){
            changedCoords.add(i);
        }
        Solver solver = new Solver(grid);
        solver.runNakedSingle(changedCoords);
        while(!grid.isSolved() && !changedCoords.isEmpty()){
            int changed = changedCoords.remove();
            int row = changed / 9;
            int column = changed % 9;

            if(!grid.isSolved(row, column)){
                for(SolverFunction func: methods){
                    func.solveMethod(row, column, changedCoords);
                }
            }
        }
        return grid.isSolved();
    }

    /**
     * Solves the given grid by brute force
     * @param grid the grid to be solved
     * @return true if the grid was solved
     */
    public static boolean bruteForceSolver(Grid grid){
        List<Integer> candidates = new ArrayList<>();
        for(int i = 1; i <= 9; i++){
            candidates.add(i);
        }
        return bruteForceSolver(grid, candidates);
    }

    /**
     * Solves the given grid by brute force, using the given list as the order to test candidates
     * @param grid the grid to be solved
     * @param candidates an ordered list of candidates [1,9] that determines the order to be tested.
     * @return true if the grid was solved
     */
    private static boolean bruteForceSolver(Grid grid, List<Integer> candidates){
        int[][] intGrid = new int[9][9];
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                intGrid[r][c] = grid.getVal(r, c);
            }
        }
        bruteForceSolver(intGrid, candidates);
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
        List<Integer> candidates = new ArrayList<>();
        for(int i = 1; i <= 9; i++){
            candidates.add(i);
        }
        return bruteForceSolver(grid, candidates);
    }

    /**
     * Solves the given grid by brute force, using the given list as the order to test candidates
     * @param grid the grid to be solved
     * @param candidates an ordered list of candidates [1,9] that determines the order to be tested.
     * @return true if the grid was solved
     */
    private static boolean bruteForceSolver(int[][] grid, List<Integer> candidates){
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                if(grid[r][c] == 0){
                    for(int cand: candidates){
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
