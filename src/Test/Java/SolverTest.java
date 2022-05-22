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
    Queue<Integer> changed;

    @Before
    public void setUpVariables(){
        grid = new Grid();
        cands = allCands();
        testCasesFile = "testCases.txt";
        difFiles = new String[]{null, "diff1.txt", "diff2.txt", "diff3.txt", "diff4.txt", "diff5.txt"};
        solver = new Solver(grid);
        changed = new QueueSet<Integer>();
    }
    private Set<Integer> allCands(){
        Set<Integer> allCands = new HashSet<>(9);
        for(int i = 1; i <= 9; i++){
            allCands.add(i);
        }
        return allCands;
    }

    @Test
    public void test_naked_single(){
        Map<Integer, Integer> frequency = new HashMap<>();
        frequency.put(1, 0);
        for(int i = 2; i <= 9; i++){
            frequency.put(i, 8);
        }
        frequency.put(1, 0);

        grid.solveCell(0, 0, 1);
        solver.nakedSingle(0, 0, changed);
        assertEquals(frequency, grid.getRowCands(0));
        assertEquals(frequency, grid.getColumnCands(0));
        assertEquals(frequency, grid.getBoxCands(0, 0));

        Set<Integer> coordSet = new HashSet<>(changed);
        Integer[] coordExpected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 45, 54, 63, 72, 10, 11, 19, 20};
        Set<Integer> expected = new HashSet<>(Arrays.asList(coordExpected));
        assertEquals(expected, coordSet);
    }

    @Test
    public void test_hidden_single(){
        Iterator<Cell> itr = grid.rowItr(0);
        itr.next();
        while(itr.hasNext()){
            itr.next().remove(1);
        }
        solver.hiddenSingle(0, 8, changed);
        assertTrue(grid.isSolved(0, 0));
        assertEquals(1, grid.getVal(0, 0));
        Set<Integer> allCands = allCands();
        allCands.remove(1);
        for(Cell cell: locals(grid, 0, 0)){
            if(cell.getCoord() != 0){
                assertEquals(allCands, cell.getCands());
            }
        }
    }

    @Test
    public void test_naked_n(){
        //Test convenient 2 - 8 n
        for(int j = 1; j < 8; j++){
            for(int i = 0; i < 9 - j; i++){
                grid.removeCand(0, i, j);
            }
            solver.nakedCandidateN(0, 0, 9 - j, changed);
            assertEquals(j, grid.getVal(0, 9 - j));
        }

        //test naked pair non adjacent
        setUpVariables();
        for(int i = 3; i <= 9; i++){
            grid.removeCand(0, 0, i);
            grid.removeCand(0, 2, i);
        }
        solver.nakedCandidateN(0, 0, 2, changed);
        for(int c = 0; c < 9; c++){
            if(c != 0 && c != 2){
                assertFalse(grid.getCands(0, c).contains(2));
                assertFalse(grid.getCands(0, c).contains(1));
            }
        }

        //test failure case of dif2
        setUpVariables();
        keepCands(grid.getCell(0, 0), 2, 6, 9);
        keepCands(grid.getCell(0, 2), 6, 9);
        keepCands(grid.getCell(0, 3), 4, 6);
        keepCands(grid.getCell(0, 6), 2, 4, 6);
        solver.nakedCandidateN(0, 0, 2, changed);
        assertTrue(changed.isEmpty());

        //Test that it continues past first permutation
        setUpVariables();
        keepCands(grid.getCell(0, 0), 2, 6);
        keepCands(grid.getCell(0, 2), 5, 7);
        keepCands(grid.getCell(0, 3), 5, 7);
        solver.nakedCandidateN(0, 0, 2, changed);
        assertFalse(changed.isEmpty());
    }

    private void keepCands(Cell cell, Integer... cands){
        Set<Integer> keep = new HashSet<>(Arrays.asList(cands));
        for(int i = 1; i <= 9; i++){
            if(!keep.contains(i)){
                cell.remove(i);
            }
        }
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
    public void test_dif_2() {
        sudokScanner = initializeScanner(difFiles[2]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.next());
            Solver solver = new Solver(grid);
            assertTrue(solver.solve());
        }
    }

    @Test
    public void test_dif_3() {
        sudokScanner = initializeScanner(difFiles[3]);
        while (sudokScanner.hasNext()) {
            grid = new Grid(sudokScanner.next());
            Solver solver = new Solver(grid);
            assertTrue(solver.solve());
        }
    }

    private Scanner initializeScanner(String fileName){
        try{
            return new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    private List<Cell> locals(Grid grid, int row, int column){
        List<Cell> cells = new ArrayList<>();
        cells.addAll(grid.getRowCells(row));
        cells.addAll(grid.getColumnCells(column));
        cells.addAll(grid.getBoxCells(row, column));
        return cells;
    }
}

