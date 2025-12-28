package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiredExecutorTest {
    @Test
    public void testExecutorConstructor() {
        //test 1: Input 0 threads
        assertThrows(IllegalArgumentException.class, () -> {new TiredExecutor(0);});

        //test 2: Input negative threads
        assertThrows(IllegalArgumentException.class, () -> {new TiredExecutor(-3);});
    }

    @Test
    public void testSubmit() {
        //test 1: Successful submit, one of these will happen: inFlight = 1 or idleMinHeap !empty
        TiredExecutor te = new TiredExecutor(1);
        Runnable task = () -> {};
        te.submit(task);
        if(te.getInFlight() != 0){
            assertTrue(true);
        }
        else{
            assertFalse(te.minHeapIsEmpty());
        }
        try{ te.shutdown(); }
        catch(InterruptedException e) {}
    }

    @Test
    public void testSubmitAll() {
        //test 1: Submitting multiple tests results in inFlight = 0
        TiredExecutor te = new TiredExecutor(5);
        int numTasks = 30;
        java.util.ArrayList<Runnable> tasks = new java.util.ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                int num = 0;
                for (int j = 0; j < 100000000; j++) {
                    num++;
                }
            });
        }
        te.submitAll(tasks);

        assertEquals(0, te.getInFlight());

        try { te.shutdown(); } 
        catch (InterruptedException e) {}
    }

    @Test
    public void testShutdown() {
        //test 1: All threads = TERMINATED at end
        TiredExecutor te = new TiredExecutor(8);
        te.submit(() -> {});
        
        try {te.shutdown(); } 
        catch (InterruptedException e) {
            fail("Shutdown was interrupted");
        }
        for (TiredThread worker : te.getWorkers()) {
            assertFalse(worker.isAlive());
        }
    }
}
