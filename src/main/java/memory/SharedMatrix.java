package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        vectors=new SharedVector[matrix.length];
        for(int i=0;i<matrix.length;i++){
            vectors[i]=new SharedVector(matrix[i],VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        SharedVector[] copy=new SharedVector[matrix.length];
        for(int i=0;i<matrix.length;i++){
            copy[i]=new SharedVector(matrix[i],VectorOrientation.ROW_MAJOR);
        }
        vectors=copy;
    }

    public void loadColumnMajor(double[][] matrix) {
        int other_columns=matrix[0].length;
        SharedVector[] copy=new SharedVector[other_columns]; 
        for (int c=0;c<other_columns;c++){
            double[] normalVector=new double[matrix.length];
            for(int r=0;r<matrix.length;r++){
                normalVector[r]=matrix[r][c];
            }
            copy[c]=new SharedVector(normalVector,VectorOrientation.COLUMN_MAJOR);
        }  
        vectors=copy;
    }

    public double[][] readRowMajor() {
        if (vectors.length == 0) {
            return new double[0][0];
        }
        double[][] output=new double[vectors.length][vectors[0].length()];
        for(int i=0;i<output.length;i++){
            for(int j=0;j<output[0].length;j++){
                output[i][j]=vectors[i].get(j);
            }
        }
        return output;
    }

    public SharedVector get(int index) {
        if(index<0 || index>=vectors.length)
            throw new IndexOutOfBoundsException("[SharedVector get]: Index given is invalid");
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
        
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if(vectors.length==0)
            throw new IndexOutOfBoundsException("[getOrientation]: Given matrix is empty");
       return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(int i=0;i<vecs.length;i++){
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(int i=0;i<vecs.length;i++){
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(int i=0;i<vecs.length;i++){
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write lock for each vector
        for(int i=0;i<vecs.length;i++){
            vecs[i].writeUnlock();
        }
    }
}
