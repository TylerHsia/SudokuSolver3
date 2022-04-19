package Test.Java;
import Main.Cell;
import Main.Grid;
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

    @Before
    public void setUpVariables(){
        grid = new Grid();
        cands = allCands();
        testCasesFile = "testCases.txt";
        difFiles = new String[]{null, "diff1.txt", "diff2.txt", "diff3.txt", "diff4.txt", "diff5.txt"};
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
        sudokScanner = initializeScanner(difFiles[1]);
        while(sudokScanner.hasNext()){
            System.out.println("he");
            grid = new Grid(sudokScanner.next());
            Solver solver = new Solver(grid);
            assertTrue(solver.solve());

        }
    }

    private Scanner initializeScanner(String fileName){
        try{
            Scanner scanner = new Scanner(new File("src/Test/text/" + fileName));
            return scanner;
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}

