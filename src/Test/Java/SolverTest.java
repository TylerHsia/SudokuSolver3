package Test.Java;
import Main.Cell;
import Main.Grid;
import Main.QueueSet;
import Main.Solver;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class SolverTest {
    Scanner sudokScanner;
    Grid grid;
    Set<Integer> cands;
    String testCasesFile;
    String[] difFiles = new String[6];
    Solver solver;

    @Before
    public void setUpVariables(){
        grid = new Grid();
        cands = allCands();
        testCasesFile = "testCases.txt";
        difFiles = new String[]{null, "diff1.txt", "diff2.txt", "diff3.txt", "diff4.txt", "diff5.txt"};
        solver = new Solver(grid);
    }
    private Set<Integer> allCands(){
        Set<Integer> allCands = new HashSet<>(9);
        for(int i = 1; i <= 9; i++){
            allCands.add(i);
        }
        return allCands;
    }

    @Test
    public void test_remove_rook_box(){
        Map<Integer, Integer> frequency = new HashMap<>();
        frequency.put(1, 0);
        for(int i = 2; i <= 9; i++){
            frequency.put(i, 8);
        }
        frequency.put(1, 0);

        grid.solveCell(0, 0, 1);
        Queue<Integer> changed = new QueueSet<Integer>();
        solver.removeRookBox(0, 0, changed);
        assertEquals(frequency, grid.getRowCands(0));
        assertEquals(frequency, grid.getColumnCands(0));
        assertEquals(frequency, grid.getBoxCands(0, 0));

        Set<Integer> coordSet = new HashSet<>(changed);
        Integer[] coordExpected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 45, 54, 63, 72, 10, 11, 19, 20};
        Set<Integer> expected = new HashSet<>(Arrays.asList(coordExpected));
        assertEquals(expected, coordSet);
    }

    @Test
    public void test_dif_1(){
        sudokScanner = initializeScanner(difFiles[1]);
        while(sudokScanner.hasNext()){
            grid = new Grid(sudokScanner.next());
            Solver solver = new Solver(grid);
            assertTrue(solver.solve());
        }
    }

    @Test
    public void test_dif_2(){
        sudokScanner = initializeScanner(difFiles[2]);
        while(sudokScanner.hasNext()){
            grid = new Grid(sudokScanner.next());
            Solver solver = new Solver(grid);
            assertTrue(solver.solve());
        }
    }
    //Todo: test for removeRookBox

    private Scanner initializeScanner(String fileName){
        try{
            return new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}

