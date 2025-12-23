package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList; //imported for code structure
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);  //checks for positive numThreads in constructor
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try{
            while(computationRoot.getNodeType() != ComputationNodeType.MATRIX){
                ComputationNode compNode = computationRoot.findResolvable(); 
                compNode.associativeNesting();
                compNode = compNode.findResolvable();
                loadAndCompute(compNode);
                compNode.resolve(leftMatrix.readRowMajor());
            }
        }  //solved computation tree
        finally{
            try{
                System.out.println(executor.getWorkerReport());
                executor.shutdown();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        ComputationNodeType nodeType = node.getNodeType();
        List<ComputationNode> listNode = node.getChildren();
        List<Runnable> toSubmit = new ArrayList<>();
        if(nodeType.equals(ComputationNodeType.ADD)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            rightMatrix.loadRowMajor(listNode.getLast().getMatrix());
            toSubmit = createAddTasks();
        }
        if(nodeType.equals(ComputationNodeType.MULTIPLY)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            rightMatrix.loadColumnMajor(listNode.getLast().getMatrix());
            toSubmit = createMultiplyTasks();
        }
        if(nodeType.equals(ComputationNodeType.TRANSPOSE)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            toSubmit = createTransposeTasks();
        }
        if(nodeType.equals(ComputationNodeType.NEGATE)){
            leftMatrix.loadRowMajor(listNode.getFirst().getMatrix());
            toSubmit = createNegateTasks();
        }
        executor.submitAll(toSubmit);
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> addOutput = new ArrayList<>();
        if(leftMatrix.length() != rightMatrix.length()){
            throw new IllegalArgumentException("[createAddTasks]: Matrices lengths are not equal");
        }
        for(int i = 0; i < leftMatrix.length(); i++){
            SharedVector left = leftMatrix.get(i);
            SharedVector right = rightMatrix.get(i);
            Runnable addRun = () -> {left.add(right);};
            addOutput.add(addRun);
        }
        return addOutput;
    }
 
    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> output = new ArrayList<>();
        for(int i = 0; i < leftMatrix.length(); i++){
            SharedVector left = leftMatrix.get(i);
            Runnable r = () -> {left.vecMatMul(rightMatrix);};
            output.add(r);
        }
        return output;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> output = new ArrayList<>();
        for(int i = 0; i < leftMatrix.length(); i++){
            SharedVector negateVector = leftMatrix.get(i);
            Runnable r = () -> {negateVector.negate();};
            output.add(r);
        }
        return output;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> output = new ArrayList<>();
        for(int i = 0; i < leftMatrix.length(); i++){
            SharedVector transVector = leftMatrix.get(i);
            Runnable r = () -> {transVector.transpose();};
            output.add(r);
        }
        return output;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }


}
