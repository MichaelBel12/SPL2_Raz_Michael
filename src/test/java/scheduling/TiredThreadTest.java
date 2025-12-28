package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {
    @Test
    public void testThreadConstructor() {
        //test 1: Invalid fatigue factor <0.5
        assertThrows(IllegalArgumentException.class, () -> {new TiredThread(1, 0.3);});
        //test 2: Invalid fatigure factor >=1.5
        assertThrows(IllegalArgumentException.class, () -> {new TiredThread(1, 1.5);});
    }


    @Test
    public void testThreadNewTask(){
        //test 1: Thread receives runnable inside handoff queue
        TiredThread thread = new TiredThread(1, 0.7);
        Runnable task = () -> {return;};
        thread.newTask(task);
        assertTrue(thread.hasTaskInQueue());

        //test 2: Trying to hand a new task when handoff is full
        assertThrows(IllegalStateException.class, () -> {thread.newTask(() -> {});});
    }
    @Test
    public void testCompareTo() {
        //test 1: Both fatigues are 0
        TiredThread t1 = new TiredThread(1, 0.5);
        TiredThread t2 = new TiredThread(2, 1.4);
        assertEquals(0, t1.compareTo(t2));

        //test 2: One thread's fatigue is higher
        t1.setTimeUsed();
        
        assertEquals(1, t1.compareTo(t2));
        assertEquals(-1, t2.compareTo(t1));
    }
    @Test
    public void testShutdown() {
        //test 1: Successful shutdown terminates thread
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        thread.shutdown();

        assertDoesNotThrow(() -> thread.join(1000)); 
        assertFalse(thread.isAlive());

        //test 2: Assigning test to terminated thread
        assertThrows(IllegalStateException.class, () -> {
            thread.newTask(() -> {});});
    }
}
