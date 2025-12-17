package memory;

import java.util.concurrent.locks.ReadWriteLock;

import javax.management.RuntimeErrorException;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.orientation=orientation;
        this.vector=vector;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if(index<0 || index>=vector.length)
            throw new IndexOutOfBoundsException("[Sharedvector:Get]: Index out of bounds");
        readLock();
        try {
            double output = vector[index];
            return output;
        }
        finally {
            readUnlock();
        }
    }

    public int length() {
        readLock();
        try{
            int len=vector.length;
            return len;
        }
        finally{
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        readLock();
        try{
            VectorOrientation orient=orientation;
            return orient;
        }
        finally{readUnlock();}
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        try{
            VectorOrientation opposite = (this.orientation == VectorOrientation.ROW_MAJOR) ? VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;      
            this.orientation=opposite;
        }
        
        finally{
            writeUnlock();
        }
        
        
    }

    public void add(SharedVector other) {              //Note: Might cause deadlock if a+b and b+a are executed at the same time, yet-
        if (this==other){                             //This situation wont happen in LAE implementation
            throw new IllegalArgumentException("[Add]: Cannot add a vector to itself");                              
        }  
        other.readLock();
        writeLock();
        if(vector.length!=other.vector.length)
            throw new IllegalArgumentException("[Add]: Cannot add vectors with diferent sizes"); 
        
        try{
            if(orientation!=other.orientation)
                throw new IllegalArgumentException("[Add]: Cannot add vectors with different orientations");
            for(int i=0;i<vector.length;i++){
                vector[i] = vector[i]+ other.vector[i];
            }
        }
        finally{
            other.readUnlock();
            writeUnlock();
        }
    } 

    public void negate() {
        writeLock();
        for(int i=0;i<vector.length;i++){
            vector[i]= -1*vector[i];
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {  //Note: Might cause deadlock if a*b and b*a are executed at the same time, yet-
        double result=0;                        //This situation wont happen in LAE implementation
        if (this==other){                            
          throw new IllegalArgumentException("[dot]: Cannot multiply vectors with same Orientation");                             
        }  
        other.readLock();
        readLock();
        if(vector.length!=other.vector.length)
            throw new IllegalArgumentException("[dot]: Cannot multiply vectors with diferent sizes"); 
        try{
            if(orientation==other.orientation)
                throw new IllegalArgumentException("[dot]: Cannot multiply vectors with same orientations");
            for(int i=0;i<vector.length;i++){
                result += vector[i]*other.vector[i];
            }
            return result;
        }
        finally{
            other.readUnlock();
            readUnlock();
        }
        
    } 

    

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        writeLock();
        try{
            if(orientation==VectorOrientation.COLUMN_MAJOR){           
                throw new IllegalArgumentException("[VecMatMul]: vector orientation must be row");
        }
            if(matrix.get(0).orientation==VectorOrientation.ROW_MAJOR){
                if(matrix.length()!=vector.length)
                    throw new IllegalArgumentException("[VecMatMul: Matrix length doesnt fit vector length");
                double[] res=new double[matrix.get(0).length()];
                for(int i=0;i<matrix.length();i++){
                    matrix.get(i).readLock();               
                    try{
                        for(int j=0; j<matrix.get(i).length();j++){
                            res[j]+=vector[i]*matrix.get(i).get(j);
                        } 
                    }
                    finally{
                        matrix.get(i).readUnlock();
                    }
                }
                vector=res;
            }
            else{                                                //other matrix is column major
                if(matrix.get(0).length()!=vector.length){
                    throw new IllegalArgumentException("[VecMatMul]: matrix columns are not equal to vector's length");
                }
                double[] res=new double[matrix.length()];
                for(int i=0; i<matrix.length();i++){
                    res[i]=dot(matrix.get(i));
                }
                vector=res;
            }
        }
        finally{
            writeUnlock();
        }
    }

}
