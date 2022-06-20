package Test.Java;

import Main.*;
import org.junit.Before;

import java.io.File;
import java.util.*;

import Main.Grid;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneratorTest {
    Scanner sudokScanner;
    Grid grid;
    String testCasesFile;
    String[] difFiles = new String[6];
    Solver solver;
    Queue<Integer> changed;

    @Before
    public void setUpVariables(){
        grid = new Grid();
        testCasesFile = "testCases.txt";
        difFiles = new String[]{null, "diff1.txt", "diff2.txt", "diff3.txt", "diff4.txt", "diff5.txt", "oldTests.txt"};
        solver = new Solver(grid);
        changed = new QueueSet<Integer>();
    }

    @Test
    public void test_all_stored_valid(){
        for(int i = 1; i <= 6; i++){
            sudokScanner = initializeScanner(difFiles[i]);
            int j = 1;
            while (sudokScanner.hasNext()) {
                System.out.println(i + " " + j);
                grid = new Grid(sudokScanner.nextLine());

                assertTrue(Generator.isValid(grid));
                j++;
            }
        }
    }

    @Test
    public void test_is_valid(){
        assertFalse(Generator.isValid(grid));
        List<Grid> testCases = getGrids(testCasesFile);
        for(int i = 29; i < 49; i++){
            assertFalse(Generator.isValid(testCases.get(i)));
        }
    }

    @Test
    public void test_brute_force_solves(){
        for(int i = 1; i <= 6; i++){
            sudokScanner = initializeScanner(difFiles[i]);
            while (sudokScanner.hasNext()) {
                grid = new Grid(sudokScanner.nextLine());
                assertTrue(Generator.bruteForceSolver(grid));
            }
        }
    }

    @Test
    public void test_solve_with_methods() {
        List<SolverFunction> methods = new ArrayList<>();
        sudokScanner = initializeScanner(difFiles[1]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.nextLine());
            assertTrue(Generator.solveWithMethods(grid, methods));
        }

        sudokScanner = initializeScanner(difFiles[2]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.nextLine());
            Solver solver = new Solver(grid);
            methods.add(solver::hiddenSingle);
            assertTrue(Generator.solveWithMethods(grid, methods));
        }

        sudokScanner = initializeScanner(difFiles[3]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.nextLine());
            Solver solver = new Solver(grid);
            methods.add(solver::hiddenSingle);
            methods.add(solver::nakedCandidates);
            assertTrue(Generator.solveWithMethods(grid, methods));
        }

        sudokScanner = initializeScanner(difFiles[4]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.nextLine());
            Solver solver = new Solver(grid);
            methods.add(solver::hiddenSingle);
            methods.add(solver::nakedCandidates);
            methods.add(solver::claimingCandidates);
            methods.add(solver::pointingCandidates);
            assertTrue(Generator.solveWithMethods(grid, methods));
        }
    }

    private Scanner initializeScanner(String fileName){
        try{
            return new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private List<Grid> getGrids(String fileName){
        sudokScanner = initializeScanner(fileName);
        List<Grid> grids = new ArrayList<>();
        while (sudokScanner.hasNext()) {
            grids.add(new Grid(sudokScanner.nextLine()));
        }
        return grids;
    }
}

