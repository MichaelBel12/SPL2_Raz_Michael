package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {
    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        if(numThreads <= 0){
            throw new IllegalArgumentException("[TiredExecutor]: Number of threads must be positive!");
        }
        workers = new TiredThread[numThreads];
        for(int i = 0; i < workers.length; i++){
            TiredThread thread = new TiredThread(i, Math.random()+0.5); //Fatigue factor between 0.5 and 1.5
            workers[i] = thread;
            idleMinHeap.put(thread);
            thread.start();      
        }
    }

    public void submit(Runnable task) {
        // TODO
        terminationLookup();                                  //perhaps other threads have crashed
        try{
            TiredThread worker = idleMinHeap.take();
            Runnable ScopedTask = () -> {                   //allowes the worker to return to the heap when done
                try{
                    long startTime = System.nanoTime();
                    task.run();
                    worker.setStopTimes(startTime);              //update time used and idle start time before returning to heap
                    worker.setBusy(false);
                    idleMinHeap.put(worker);
                }
                catch(Exception e){ 
                    System.err.print(e.getMessage());
                    inFlight.set(-1*workers.length-5);            //indicate crash to submitAll (will never reach 0 again)
                    throw e;      
                }
                finally{
                    inFlight.decrementAndGet();              //in submitAll, we wait until inFlight is 0;
                                                               //return to heap only if finished successfully
                    synchronized(this){
                        this.notifyAll();                     //notify submitAll that a task has finished
                    }
                }
            };
            inFlight.incrementAndGet();
            worker.newTask(ScopedTask);                      //FINALLY hands over the task to the worker
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks){
        // TODO: submit tasks one by one and wait until all finish
        Iterator<Runnable> iter = tasks.iterator();
        while(iter.hasNext()){
            terminationLookup();                     //first check if any thread has crashed
            this.submit(iter.next());
        }
        synchronized(this){                        //wait until all tasks are done
            while(inFlight.get() > 0){
                terminationLookup();
                try{
                    wait();   
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }    
            }
            terminationLookup();
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(int i = 0; i <workers.length;i++){ //Shuts down all workers one after the other
            TiredThread cur=workers[i];
            cur.shutdown();                  
        }
        for(int i = 0; i < workers.length;i++){
            workers[i].join(); //Waits for all workers to terminate
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String output = "";
        for(int i = 0; i < workers.length; i++){ //All readable statistics for each worker
            TiredThread cur = workers[i];
            output += "Worker " + i + ":\n";
            output += "\tFatigue: " + cur.getFatigue() + "\n";
            output += "\tTime Used (ns): " + cur.getTimeUsed() + "\n";
            output += "\tTime Idle (ns): " + cur.getTimeIdle() + "\n";
            output += "\tIs Busy: " + cur.isBusy() + "\n";
           
        }
         output += "\tFairness: " + this.fairnessCalculation() + "\n";
        return output;
    }

    private void terminationLookup(){
        if(inFlight.get() < 0){
            throw new IllegalThreadStateException("[terminationLookup]: Thread has crashed and been terminated."); 
        }
    }
    public double fairnessCalculation(){
        double totalFatigue = 0.0;
        for(TiredThread worker : workers){
            totalFatigue += worker.getFatigue();
        }
        double averageFatigue = totalFatigue / workers.length;
        double sumSquaredDiffs = 0.0;
        for(TiredThread worker : workers){
            double diff = worker.getFatigue() - averageFatigue;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs;
    }
     int getInFlight(){  //for testing purposes
        return inFlight.get();
    }

    boolean minHeapIsEmpty(){
        return idleMinHeap.isEmpty();
    }

    TiredThread[] getWorkers(){
        return workers;
    }
}