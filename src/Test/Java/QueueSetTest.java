package Test.Java;
import Main.Cell;
import Main.Grid;
import Main.QueueSet;
import Main.Solver;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class QueueSetTest {
    Queue<Integer> intQueue;
    @Before
    public void setUpVariables(){
        intQueue= new QueueSet<Integer>();
    }
    @Test
    public void construction_test(){
        assertTrue(intQueue.isEmpty());
        assertEquals(0, intQueue.size());
    }

    @Test
    public void add_no_duplicate_test(){
        for(int i = 0; i < 100; i++){
            assertEquals(i, intQueue.size());
            assertTrue(intQueue.add(i));
            assertFalse(intQueue.isEmpty());
        }
    }

    @Test
    public void add_duplicate_test(){
        for(int i = 0; i < 10; i++){
            intQueue.add(i);
        }
        for(int i = 0; i < 100; i++){
            assertEquals(10, intQueue.size());
            assertFalse(intQueue.add(i % 10));
            assertFalse(intQueue.isEmpty());
        }
    }

    @Test
    public void remove_peek_test(){
        //no duplicates added
        for(int i = 0; i < 100; i++){
            assertEquals(i, intQueue.size());
            intQueue.add(i);
            assertFalse(intQueue.isEmpty());
        }
        for(int i = 0; i < 100; i++){
            assertEquals(100 - i, intQueue.size());
            assertEquals(i, (int) intQueue.peek());
            assertEquals(i, (int) intQueue.remove());
        }
        //duplicates added
        for(int i = 0; i < 10; i++){
            intQueue.add(i);
        }
        for(int i = 0; i < 100; i++){
            assertEquals(10, intQueue.size());
            intQueue.add(i % 10);
            assertFalse(intQueue.isEmpty());
        }
        for(int i = 0; i < 10; i++){
            assertEquals(10 - i, intQueue.size());
            assertEquals(i, (int) intQueue.peek());
            assertEquals(i, (int) intQueue.remove());
        }
    }

    @Test
    public void iterator_test(){
        assertFalse(intQueue.iterator().hasNext());
        addOneToOneHunred(intQueue);
        Iterator<Integer> itr = intQueue.iterator();
        assertTrue(itr.hasNext());
        for(int i = 1; i <= 100; i++){
            assertEquals(i, (int) itr.next());
        }
        assertFalse(itr.hasNext());
        assertThrows(UnsupportedOperationException.class, itr::remove);
    }

    private void addOneToOneHunred(Queue<Integer> intQueue) {
        for (int i = 1; i <= 100; i++) {
            intQueue.add(i);
        }
    }

    @Test
    public void testAddAll(){
        Integer[] arr = new Integer[100];
        for(int i = 0; i < 100; i++){
            arr[i] = i;
        }
        Collections.addAll(intQueue, arr);
        assertEquals(100, intQueue.size());
        assertFalse(intQueue.isEmpty());
        for(int i = 0; i < 100; i++){
            assertEquals(100 - i, intQueue.size());
            assertEquals(i, (int) intQueue.peek());
            assertEquals(i, (int) intQueue.remove());
        }
        assertTrue(intQueue.isEmpty());
        //add duplicates
        for(int i = 0; i < 100; i++){
            arr[i] = i % 10;
        }
        Collections.addAll(intQueue, arr);
        assertEquals(10, intQueue.size());
        assertFalse(intQueue.isEmpty());
        for(int i = 0; i < 10; i++){
            assertEquals(10 - i, intQueue.size());
            assertEquals(i, (int) intQueue.peek());
            assertEquals(i, (int) intQueue.remove());
        }
    }
}
