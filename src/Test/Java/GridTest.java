package Test.Java;
import Main.Cell;
import Main.Grid;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class GridTest {
    Scanner sudokScanner;
    Grid grid;
    Set<Integer> cands;
    String testCasesFile;
    String[] difFiles = new String[6];

    @Before
    public void setUpVariables(){
        grid = new Grid();
        cands = allCands();
        testCasesFile = "testCases.txt";
        difFiles = new String[]{null, "diff1.txt", "diff2.txt", "diff3.txt", "diff4.txt", "diff5.txt"};
    }
    @Test
    public void test_simple_construction_and_accessors(){
        int count = 0;
        for(Cell cell: grid){
            count++;
            assertEquals(cands, cell.getCands());
            assertEquals(0, cell.getVal());
            assertFalse(cell.isSolved());
        }
        assertEquals(81, count);
    }

    @Test
    public void test_solve_cell(){
        cands = new HashSet<>();
        //solve first column 1-9
        for(int r = 0; r < 9; r++){
            grid.solveCell(r, 0, r + 1);
        }
        //check first column cells
        for(int r = 0; r < 9; r++){
            cands.add(r + 1);
            assertEquals(r + 1, grid.getVal(r, 0));
            assertEquals(cands, grid.getCands(r, 0));
            cands.remove(r + 1);
            assertTrue(grid.isSolved(r, 0));
        }
        //assert rest of cells unchanged
        assertAllButFirstColumnDefault(grid);
    }

    @Test
    public void test_remove_once(){
        //remove first column 1-9
        for(int r = 0; r < 9; r++){
            assertTrue(grid.removeCand(r, 0, r + 1));
        }
        //check first column cells
        for(int r = 0; r < 9; r++){
            cands.remove(r + 1);
            assertEquals(0, grid.getVal(r, 0));
            assertEquals(cands, grid.getCands(r, 0));
            cands.add(r + 1);
            assertFalse(grid.isSolved(r, 0));
        }
        //assert rest of cells unchanged
        assertAllButFirstColumnDefault(grid);
    }

    @Test
    public void test_remove_return(){
        //remove first column 1-9
        for(int r = 0; r < 9; r++){
            assertTrue(grid.removeCand(r, 0, r + 1));
        }
        //remove first column 1-9 again
        for(int r = 0; r < 9; r++){
            assertFalse(grid.removeCand(r, 0, r + 1));
        }
        //remove first cell non 1-9
        for(int i = 10; i < 1000; i++){
            assertFalse(grid.removeCand(0, 0, i));
        }
    }

    @Test
    public void test_remove_all_but_one_val(){
        cands = new HashSet<>();
        //remove first column 1-9
        for(int r = 0; r < 9; r++){
            for(int cand = 1; cand <= 9; cand++){
                if(cand != r + 1){
                    assertTrue(grid.removeCand(r, 0, cand));
                }
            }
        }
        //check first column cells
        for(int r = 0; r < 9; r++){
            cands.add(r + 1);
            assertEquals(r + 1, grid.getVal(r, 0));
            assertEquals(cands, grid.getCands(r, 0));
            cands.remove(r + 1);
            assertTrue(grid.isSolved(r, 0));
        }
        //assert rest of cells unchanged
        assertAllButFirstColumnDefault(grid);
    }

    @Test
    public void test_get_row(){
        for(int r = 0; r < 9; r++){
            assertTrue(grid.getRow(r).isEmpty());
        }
        Set<Integer> getRow = new HashSet<>();
        //assign first row to 1 - 9, checking getRow
        for(int c = 0; c < 9; c++){
            getRow.add(c + 1);
            grid.solveCell(0, c, c + 1);
            assertEquals(getRow, grid.getRow(0));
        }
    }

    @Test
    public void test_get_column(){
        for(int c = 0; c < 9; c++){
            assertTrue(grid.getColumn(c).isEmpty());
        }

        Set<Integer> getColumn = new HashSet<>();
        //assign first row to 1 - 9, checking getRow
        for(int r = 0; r < 9; r++){
            getColumn.add(r + 1);
            grid.solveCell(r, 0, r + 1);
            assertEquals(getColumn, grid.getColumn(0));
        }
    }

    @Test
    public void test_get_box(){
        for(int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assertTrue(grid.getBox(r, c).isEmpty());
            }
        }
        int val = 1;
        Set<Integer> getBox = new HashSet<>();

        for (Iterator<Cell> it = grid.boxItr(0, 0); it.hasNext(); ) {
            Cell cell = it.next();
            getBox.add(val);
            cell.solve(val);
            val++;
            assertEquals(getBox, grid.getBox(0, 0));
        }
    }


    @Test
    public void test_numCandsRow(){
        Map<Integer, Integer> rowMap;

        //Test starting state
        for(int r = 0; r < 9; r++){
            rowMap = grid.getRowCands(r);
            assertEquals(9, rowMap.size());
            for(int key = 1; key <= 9; key++){
                assertEquals(9, (int) rowMap.get(key));
            }
        }
        //removed one cand
        removeOneThroughNine(grid.rowItr(0));
        rowMap = grid.getRowCands(0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) rowMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.rowItr(1));
        rowMap = grid.getRowCands(1);
        for(int val = 1; val <= 9; val++){
            assertEquals(0, (int) rowMap.get(val));
        }
    }

    @Test
    public void test_numCandsColumn(){
        Map<Integer, Integer> columnMap;
        //test starting state
        for(int c = 0; c < 9; c++){
            columnMap = grid.getColumnCands(c);
            assertEquals(9, columnMap.size());
            for(int key = 1; key <= 9; key++){
                assertEquals(9, (int) columnMap.get(key));
            }
        }
        //removed one cand
        removeOneThroughNine(grid.columnItr(0));
        columnMap = grid.getColumnCands(0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) columnMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.columnItr(1));
        columnMap = grid.getColumnCands(1);
        for(int val = 1; val <= 9; val++){
            assertEquals(0, (int) columnMap.get(val));
        }
    }

    @Test
    public void test_numCandsBox(){
        Map<Integer, Integer> boxMap;
        //test starting state
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                boxMap = grid.getBoxCands(r, c);
                assertEquals(9, boxMap.size());
                for(int key = 1; key <= 9; key++){
                    assertEquals(9, (int) boxMap.get(key));
                }
            }
        }

        //removed one cand
        removeOneThroughNine(grid.boxItr(0, 0));
        boxMap = grid.getBoxCands(0, 0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) boxMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.boxItr(3, 3));
        boxMap = grid.getBoxCands(3, 3);
        for(int val = 1; val <= 9; val++){
            assertEquals(0, (int) boxMap.get(val));
        }
    }

    @Test
    public void test_is_solved(){
        initializeScanner(testCasesFile);
        grid = new Grid(sudokScanner.next());
        assertTrue(grid.isSolved());
        for(int dif = 1; dif <= 5; dif++){
            initializeScanner(difFiles[dif]);
            while(sudokScanner.hasNext()){
                grid = new Grid(sudokScanner.next());
                assertFalse(grid.isSolved());
            }
        }
    }

    @Test
    public void test_num_solved(){
        initializeScanner(testCasesFile);
        grid = new Grid(sudokScanner.next());
        assertEquals(81, grid.numSolved());
        for(int dif = 1; dif <= 5; dif++){
            initializeScanner(difFiles[dif]);
            while(sudokScanner.hasNext()){
                String next = sudokScanner.next();
                grid = new Grid(next);
                assertEquals(81 - numZeroes(next), grid.numSolved());
            }
        }
        grid = new Grid();
        assertEquals(0, grid.numSolved());
        for(int r = 0; r < 9; r++){
            grid.solveCell(r, 0, r + 1);
            assertTrue(grid.removeCand(r, 1, r + 1));
            assertEquals(r + 1, grid.numSolved());
        }
    }

    //returns the number of spaces in a string from index 0 to 80
    private int numZeroes(String sudoku){
        int numZero = 0;
        for(int i = 0; i < 81; i++){
            if(sudoku.charAt(i) == '0'){
                numZero++;
            }
        }
        return numZero;
    }

    @Test
    public void test_has_duplicate(){
        assertFalse(grid.hasDuplicate());

        //rowdup
        grid.solveCell(0, 0, 1);
        assertFalse(grid.hasDuplicate());
        grid.solveCell(8, 0, 1);
        assertTrue(grid.hasDuplicate());
        grid.solveCell(2, 0, 1);
        assertTrue(grid.hasDuplicate());
        grid.solveCell(3, 0, 2);
        assertTrue(grid.hasDuplicate());

        //column dup
        grid = new Grid();
        grid.solveCell(0, 0, 1);
        assertFalse(grid.hasDuplicate());
        grid.solveCell(0, 8, 1);
        assertTrue(grid.hasDuplicate());

        //box dup
        grid = new Grid();
        grid.solveCell(0, 0, 1);
        assertFalse(grid.hasDuplicate());
        grid.solveCell(2, 2, 1);
        assertTrue(grid.hasDuplicate());

        //all dup
        grid = new Grid();
        for(Cell cell: grid){
            cell.solve(1);
        }
        assertTrue(grid.hasDuplicate());



        initializeScanner(testCasesFile);
        grid = new Grid(sudokScanner.next());
        assertFalse(grid.hasDuplicate());
        for(int dif = 1; dif <= 5; dif++){
            initializeScanner(difFiles[dif]);
            while(sudokScanner.hasNext()){
                grid = new Grid(sudokScanner.next());
                assertFalse(grid.hasDuplicate());
            }
        }
    }

    @Test
    public void test_can_solve_simple(){
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                for(int val = 1; val <= 9; val++){
                    assertTrue(grid.canSolveSimple(r, c, val));
                }
            }
        }

        //rowdup
        grid.solveCell(0, 0, 1);
        for(int rc = 1; rc < 9; rc++){
            assertFalse(grid.canSolveSimple(rc,0, 1));
            assertFalse(grid.canSolveSimple(0, rc, 1));
        }

        assertFalse(grid.canSolveSimple(1,1, 1));
        assertFalse(grid.canSolveSimple(2,1, 1));
        assertFalse(grid.canSolveSimple(1,2, 1));
        assertFalse(grid.canSolveSimple(2,2, 1));


        assertTrue(grid.removeCand(8, 8, 1));
        assertFalse(grid.canSolveSimple(8, 8, 1));
    }

    private void removeOneThroughNine(Iterator<Cell> itr){
        int val = 1;
        while (itr.hasNext()) {
            Cell cell = itr.next();
            cell.remove(val);
            val++;
        }
    }

    private void solveOneThroughNine(Iterator<Cell> itr){
        int val = 1;
        while (itr.hasNext()) {
            Cell cell = itr.next();
            cell.solve(val);
            val++;
        }
    }

    //fails if any cell with row != 0 is not a default cell with all cands
    private void assertAllButFirstColumnDefault(Grid grid){
        Set<Integer> allCands = allCands();
        for(int c = 1; c < 9; c++){
            for (Iterator<Cell> it = grid.columnItr(c); it.hasNext(); ) {
                Cell cell = it.next();
                assertEquals(allCands, cell.getCands());
                assertEquals(0, cell.getVal());
                assertFalse(cell.isSolved());
            }
        }
    }

    private Set<Integer> allCands(){
        Set<Integer> allCands = new HashSet<>(9);
        for(int i = 1; i <= 9; i++){
            allCands.add(i);
        }
        return allCands;
    }

    private void initializeScanner(String fileName){
        try{
            sudokScanner = new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

}
