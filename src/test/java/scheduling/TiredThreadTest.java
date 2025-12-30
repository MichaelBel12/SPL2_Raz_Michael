package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {

    @Test
    public void testWorkerConfiguration() {
        // Validation 1: Rejection of fatigue factors below the 0.5 threshold
        assertThrows(IllegalArgumentException.class, () -> { 
            new TiredThread(101, 0.45); 
        });

        // Validation 2: Rejection of fatigue factors at or above the 1.5 limit
        assertThrows(IllegalArgumentException.class, () -> { 
            new TiredThread(102, 1.5); 
        });
    }

    @Test
    public void testTaskAssignmentLogic() {
        // Validation 1: Verify that a task successfully enters the internal handoff mechanism
        TiredThread unit = new TiredThread(5, 0.85);
        Runnable workItem = () -> { /* logic */ };
        
        unit.newTask(workItem);
        assertTrue(unit.hasTaskInQueue(), "The worker's handoff queue should report as occupied");

        // Validation 2: Ensure an exception is raised when attempting to overfill the handoff queue
        assertThrows(IllegalStateException.class, () -> { 
            unit.newTask(() -> { /* second task */ }); 
        });
    }

    @Test
    public void testPrioritizationOrdering() {
        // Validation 1: Equal priority when neither thread has accumulated "exhaustion"
        TiredThread workerA = new TiredThread(1, 0.6);
        TiredThread workerB = new TiredThread(2, 1.2);
        assertEquals(0, workerA.compareTo(workerB), "Threads with zero work time should be equivalent in priority");

        // Validation 2: Priority shift after one thread records work duration
        workerA.setTimeUsed(); // Simulating fatigue accumulation
        
        // workerA is now "more tired" than workerB
        assertEquals(1, workerA.compareTo(workerB), "More tired thread should have higher comparison value");
        assertEquals(-1, workerB.compareTo(workerA), "Fresher thread should have lower comparison value");
    }

    @Test
    public void testThreadDeactivation() {
        // Validation 1: Confirm that shutdown effectively kills the execution context
        TiredThread probe = new TiredThread(99, 1.1);
        probe.start();
        probe.shutdown();

        // Ensure the thread wraps up and exits within a reasonable timeout
        assertDoesNotThrow(() -> probe.join(2000)); 
        assertFalse(probe.isAlive(), "Thread must not be active after shutdown and join");

        // Validation 2: Reject new task assignments for a dead thread
        assertThrows(IllegalStateException.class, () -> {
            probe.newTask(() -> {}); 
        });
    }
}