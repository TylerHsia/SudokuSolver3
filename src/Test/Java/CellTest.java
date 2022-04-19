package Test.Java;
import Main.Cell;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    @Test
    public void test_construction_no_param_is_correct() {
        Cell cell1 = new Cell();
        assertEquals(cell1.size(), 9);
        assertFalse(cell1.isSolved());
        assertEquals(cell1.getVal(), 0);
        for(int i = 1; i <= 9; i++){
            assertTrue(cell1.contains(i));
        }
    }

    @Test
    public void test_construction_value_is_correct(){
        for(int i = 1; i <= 9; i++){
            Cell cell = new Cell(i);
            assertEquals(cell.getVal(), i);
            assertTrue(cell.isSolved());
            assertTrue(cell.contains(i));
            for(int j = 1; j <= 9; j++){
                if(j != i){
                    assertFalse(cell.contains(j));
                }
            }
            assertEquals(cell.size(), 1);
        }
    }

    @Test
    public void test_remove_is_correct(){
        Cell cell = new Cell();
        for(int i = 1; i <= 8; i++){
            cell.remove(i);
            assertEquals(cell.size(), 9 - i);
        }
        assertEquals(cell.getVal(), 9);
    }

    @Test
    public void test_solve_is_correct(){
        for(int i = 1; i <= 9; i++){
            Cell cell = new Cell();
            cell.solve(i);
            assertEquals(cell.getVal(), i);
            assertEquals(cell.size(), 1);
            assertTrue(cell.isSolved());
        }
    }

    @Test
    public void test_equality_correct(){
        //equality of removing cands 1-8
        Cell cell1 = new Cell();
        Cell cell2 = new Cell();
        assertEquals(cell1, cell2);
        for(int i = 1; i <= 8; i++){
            cell1.remove(i);
            assertNotEquals(cell1, cell2);
            cell2.remove(i);
            assertEquals(cell1, cell2);
        }
        //equality of new solved cells
        cell1 = new Cell(3);
        cell2 = new Cell(3);
        assertEquals(cell1, cell2);
        cell2 = new Cell(2);
        assertNotEquals(cell1, cell2);

        //equality of solving after construction
        cell1 = new Cell();
        cell2 = new Cell();
        cell1.solve(9);
        cell2.solve(9);
        assertEquals(cell1, cell2);
    }

    @Test
    public void test_solve_throws(){
        //throws for non 2
        Cell two = new Cell(2);
        for(int i = 1; i <= 9; i++){
            if(i != 2){
                int finalI = i;
                assertThrows(IllegalStateException.class, ()->
                        two.solve(finalI));
            }
        }
        //throws for removed val
        Cell cell = new Cell();
        cell.remove(1);
        assertThrows(IllegalStateException.class, ()->
                two.solve(1));
    }

    @Test
    public void test_construction_throws(){
        // too low
        for(int i = -10; i < 1; i++){
            int finalI = i;
            assertThrows(IllegalArgumentException.class, ()->
                    new Cell(finalI));
        }
        // too high
        for(int i = 10; i < 100; i++){
            int finalI = i;
            assertThrows(IllegalArgumentException.class, ()->
                    new Cell(finalI));
        }
    }


    //Todo: test throws with invalid parameters

    //Todo: test cell coord, row, column
}
