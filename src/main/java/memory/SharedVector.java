package memory;

import java.util.concurrent.locks.ReadWriteLock;

import javax.management.RuntimeErrorException;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if(vector==null){
            throw new IllegalArgumentException("[SharedVector]: Vector cannot be null");
        }
        this.orientation=orientation;
        this.vector=vector;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        readLock();
        try {
            if(index<0 || index>=vector.length)
                throw new IndexOutOfBoundsException("[Sharedvector: Get]: out of bounds index");
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
            VectorOrientation o=orientation;
            return o;
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
            VectorOrientation opposite = (this.orientation == VectorOrientation.ROW_MAJOR) ?
             VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;      
            this.orientation=opposite;
        }
        
        finally{
            writeUnlock();
        }
        
        
    }

    public void add(SharedVector other) {
        if (this==other){
            writeLock();
            try{  
            for(int i=0;i<vector.length;i++){
                    vector[i] = vector[i]+ vector[i];
                }
                return;
            }
            finally{
                writeUnlock();
            }                       
        }
        int a = System.identityHashCode(this);
        int b = System.identityHashCode(other);
        if(a > b){                             
            other.readLock();
            writeLock();
        }
        else{
            writeLock();
            other.readLock();
        }
        try{
            if(vector.length!=other.vector.length)
                throw new IllegalArgumentException("[Add]: Cannot add vectors with different sizes");
            if(orientation!=other.orientation)
                throw new IllegalArgumentException("[Add]: Cannot add vectors with different orientations");
            for(int i=0;i<vector.length;i++){
                vector[i] = vector[i]+ other.vector[i];
            }
        }
        finally{
            writeUnlock();
            other.readUnlock();
        }
    }
        

    public void negate() {
        writeLock();
        try{
            for(int i=0;i<vector.length;i++){
                vector[i]= -1*vector[i];
            }
        }
        finally{
            writeUnlock();  
        }
    }
    public double dot(SharedVector other) { 
        if(other==null){
            throw new IllegalArgumentException("[dot]: other vector is null");
        }                        
        if (this==other){                            
          throw new IllegalArgumentException("[dot]: Cannot multiply vectors with same Orientation");                             
        }  
        double result=0;
        int a= System.identityHashCode(this);
        int b= System.identityHashCode(other);
        if(a>b){                             
            readLock();
            other.readLock();
        }
        else{
            other.readLock();
            readLock();
        }
        try{
            if(vector.length!=other.vector.length){
                throw new IllegalArgumentException("[dot]: Vectors have different lengths");
            }
            if(orientation==other.orientation){
                throw new IllegalArgumentException("[dot]: Vectors have same orientations");
            }
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
        if(matrix==null || matrix.length()==0){
            throw new IllegalArgumentException("[VecMatMul]: matrix is null");
        }
        if(vector.length==0 && matrix.length()==0){
            return;
        }
        if(matrix.length()==0){
            throw new IllegalArgumentException("[VecMatMul]: matrix has zero rows, cannot multiply");
        }
        try{
            if(orientation==VectorOrientation.COLUMN_MAJOR){           
                throw new IllegalArgumentException("[VecMatMul]: vector is not row major");
        }
            if(matrix.get(0).orientation==VectorOrientation.ROW_MAJOR){
                if(matrix.length()!=vector.length){
                    throw new IllegalArgumentException("[VecMatMul]: matrix rows are not equal to vector's length");
                }
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
                    res[i]=this.dot(matrix.get(i));
                }
                vector=res;
            }
        }
        finally{
            writeUnlock();
        }
    }

}
