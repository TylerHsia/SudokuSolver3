package Test.Java;
import Main.Cell;
import Main.Grid;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.*;

public class GridTest {
    Grid grid;
    Set<Integer> cands;
    @Before
    public void setUpVariables(){
        grid = new Grid();
        cands = allCands();
    }
    @Test
    public void test_simple_construction_and_accessors(){
        int count = 0;
        for(Cell cell: grid){
            count++;
            assertEquals(cands, cell.getCands());
            assertEquals(-1, cell.getVal());
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
            grid.removeCand(r, 0, r + 1);
        }
        //check first column cells
        for(int r = 0; r < 9; r++){
            cands.remove(r + 1);
            assertEquals(-1, grid.getVal(r, 0));
            assertEquals(cands, grid.getCands(r, 0));
            cands.add(r + 1);
            assertFalse(grid.isSolved(r, 0));
        }
        //assert rest of cells unchanged
        assertAllButFirstColumnDefault(grid);
    }


    @Test
    public void test_remove_all_but_one_val(){
        cands = new HashSet<>();
        //remove first column 1-9
        for(int r = 0; r < 9; r++){
            for(int cand = 1; cand <= 9; cand++){
                if(cand != r + 1){
                    grid.removeCand(r, 0, cand);
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
            rowMap = grid.numCandsRow(r);
            assertEquals(9, rowMap.size());
            for(int key = 1; key <= 9; key++){
                assertEquals(9, (int) rowMap.get(key));
            }
        }
        //removed one cand
        removeOneThroughNine(grid.rowItr(0));
        rowMap = grid.numCandsRow(0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) rowMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.rowItr(1));
        rowMap = grid.numCandsRow(1);
        for(int val = 1; val <= 9; val++){
            assertEquals(1, (int) rowMap.get(val));
        }
    }

    @Test
    public void test_numCandsColumn(){
        Map<Integer, Integer> columnMap;
        //test starting state
        for(int c = 0; c < 9; c++){
            columnMap = grid.numCandsColumn(c);
            assertEquals(9, columnMap.size());
            for(int key = 1; key <= 9; key++){
                assertEquals(9, (int) columnMap.get(key));
            }
        }
        //removed one cand
        removeOneThroughNine(grid.columnItr(0));
        columnMap = grid.numCandsColumn(0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) columnMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.columnItr(1));
        columnMap = grid.numCandsColumn(1);
        for(int val = 1; val <= 9; val++){
            assertEquals(1, (int) columnMap.get(val));
        }
    }

    @Test
    public void test_numCandsBox(){
        Map<Integer, Integer> boxMap;
        //test starting state
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                boxMap = grid.numCandsBox(r, c);
                assertEquals(9, boxMap.size());
                for(int key = 1; key <= 9; key++){
                    assertEquals(9, (int) boxMap.get(key));
                }
            }
        }

        //removed one cand
        removeOneThroughNine(grid.boxItr(0, 0));
        boxMap = grid.numCandsBox(0, 0);
        for(int val = 1; val <= 9; val++){
            assertEquals(8, (int) boxMap.get(val));
        }
        //solved all cands
        solveOneThroughNine(grid.boxItr(3, 3));
        boxMap = grid.numCandsBox(3, 3);
        for(int val = 1; val <= 9; val++){
            assertEquals(1, (int) boxMap.get(val));
        }
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
                assertEquals(-1, cell.getVal());
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
}
