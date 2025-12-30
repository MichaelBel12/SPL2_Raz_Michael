package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiredExecutorTest {

    @Test
    public void testPoolConstruction() {
        // Validation 1: Ensure zero-capacity pools are disallowed
        assertThrows(IllegalArgumentException.class, () -> { 
            new TiredExecutor(0); 
        });

        // Validation 2: Ensure negative capacity triggers an exception
        assertThrows(IllegalArgumentException.class, () -> { 
            new TiredExecutor(-5); 
        });
    }

    @Test
    public void testTaskHandover() {
        // Validation 1: Verify task state changes upon submission
        TiredExecutor scheduler = new TiredExecutor(1);
        Runnable dummyTask = () -> { /* simple no-op */ };
        
        scheduler.submit(dummyTask);
        
        // Logical check: Task must either be currently processing (in-flight) 
        // or stored in the prioritization structure (min-heap).
        if (scheduler.getInFlight() != 0) {
            assertTrue(true);
        } else {
            assertFalse(scheduler.minHeapIsEmpty(), "Task should be in the idle heap if not in-flight");
        }
        
        try { 
            scheduler.shutdown(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testBulkProcessing() {
        // Validation 1: Verify that bulk submission blocks until all tasks finish (inFlight reaches 0)
        TiredExecutor core = new TiredExecutor(4);
        int totalWorkload = 25;
        java.util.ArrayList<Runnable> workloadList = new java.util.ArrayList<>();
        
        for (int k = 0; k < totalWorkload; k++) {
            workloadList.add(() -> {
                double result = 0.1;
                // Simulating a CPU-heavy calculation to ensure concurrency logic is tested
                for (int m = 0; m < 50_000_000; m++) {
                    result = Math.sqrt(result + m);
                }
            });
        }
        
        // The logic requires submitAll to be a blocking/synchronous call until completion
        core.submitAll(workloadList);

        assertEquals(0, core.getInFlight(), "All tasks should have completed before return");

        try { 
            core.shutdown(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testLifecycleTermination() {
        // Validation 1: Ensure all internal worker threads are stopped post-shutdown
        TiredExecutor pool = new TiredExecutor(10);
        pool.submit(() -> {
            try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        });
        
        try { 
            pool.shutdown(); 
        } catch (InterruptedException e) {
            fail("The shutdown process was prematurely interrupted");
        }
        
        // Verify the status of every individual worker thread
        for (TiredThread unit : pool.getWorkers()) {
            assertFalse(unit.isAlive(), "Worker thread should be terminated after shutdown call");
        }
    }
}