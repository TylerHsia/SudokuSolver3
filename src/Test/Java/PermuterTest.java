package Test.Java;
import Main.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class PermuterTest {
    Grid simple;
    Queue<Integer> changed;
    List<Grid> cases;

    @Before
    public void setUpVariables(){
        changed = new QueueSet<Integer>();
        Scanner scanner = initializeScanner("testCases.txt");
        cases = new ArrayList<>();
        while (scanner.hasNext()) {
            cases.add(new Grid(scanner.nextLine()));
        }
        simple = cases.get(0);
    }

    private Scanner initializeScanner(String fileName){
        try{
            return new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Test
    public void testPerturbations(){
        assertEquals(cases.get(1), Permuter.rotateClockwise(simple));
        assertEquals(cases.get(2), Permuter.rotateCounterClockwise(simple));
        assertEquals(cases.get(3), Permuter.flipHorizontal(simple));
        assertEquals(cases.get(4), Permuter.flipVertical(simple));
        assertEquals(cases.get(5), Permuter.swapBoxColumns(simple, 0, 0));
        assertEquals(cases.get(6), Permuter.swapBoxColumns(simple, 0, 1));
        assertEquals(cases.get(7), Permuter.swapBoxColumns(simple, 0, 2));
        assertEquals(cases.get(8), Permuter.swapBoxRows(simple, 0, 0));
        assertEquals(cases.get(9), Permuter.swapBoxRows(simple, 0, 1));
        assertEquals(cases.get(10), Permuter.swapBoxRows(simple, 0, 2));
        assertEquals(cases.get(11), Permuter.swapRowsInBoxRow(simple,0, 0, 0));
        assertEquals(cases.get(12), Permuter.swapRowsInBoxRow(simple,0, 0, 1));
        assertEquals(cases.get(13), Permuter.swapRowsInBoxRow(simple,0, 0, 2));
        assertEquals(cases.get(14), Permuter.swapRowsInBoxRow(simple,2, 0, 1));
        assertEquals(cases.get(15), Permuter.swapColumnInBoxColumn(simple, 0, 0, 0));
        assertEquals(cases.get(16), Permuter.swapColumnInBoxColumn(simple, 0, 0, 1));
        assertEquals(cases.get(17), Permuter.swapColumnInBoxColumn(simple, 0, 0, 2));
        assertEquals(cases.get(18), Permuter.swapColumnInBoxColumn(simple, 2, 0, 1));
        assertEquals(cases.get(19), Permuter.reflectOrigin(simple));
        assertEquals(cases.get(20), Permuter.reflectBottomTopDiagonal(simple));
        assertEquals(cases.get(21), Permuter.reflectTopBottomDiagonal(simple));
        assertEquals(cases.get(22), Permuter.changeNumbers(simple, new Random(0)));
    }

    private void print(Grid grid){
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                System.out.print(grid.getVal(r, c));
            }
        }
        System.out.println();
    }
}
