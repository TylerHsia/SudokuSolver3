package Test.Java;

import Main.*;
import org.junit.Before;

import java.io.File;
import java.util.Queue;
import java.util.Scanner;

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
            sudokScanner = initializeScanner(difFiles[3]);
            while (sudokScanner.hasNext()) {
                grid = new Grid(sudokScanner.next());
                System.out.println(grid);
                assertTrue(Generator.isValid(grid));
            }
        }
    }
    @Test
    public void test_brute_force_solves(){
        for(int i = 1; i <= 6; i++){
            sudokScanner = initializeScanner(difFiles[3]);
            while (sudokScanner.hasNext()) {
                grid = new Grid(sudokScanner.next());
                assertTrue(Generator.isValid(grid));
            }
        }
    }

    private Scanner initializeScanner(String fileName){
        try{
            return new Scanner(new File("src/Test/text/" + fileName));
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}

