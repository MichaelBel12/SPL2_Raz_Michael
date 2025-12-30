package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedMatrixTest {

    @Test
    public void testInstanceCreation() {
        // Validation 1: Check rejection of null array inputs
        assertThrows(IllegalArgumentException.class, () -> { 
            new SharedMatrix(null); 
        });
    }

    @Test
    public void testHorizontalStorage() {
        // Validation 1: Verify successful horizontal (Row-Major) data ingestion
        SharedMatrix matrixA = new SharedMatrix();
        double[][] sampleGrid = {{10.5, 11.5}, {12.5, 13.5}};
        matrixA.loadRowMajor(sampleGrid);
        
        double[][] retrievedData = matrixA.readRowMajor();
        assertArrayEquals(sampleGrid, retrievedData);
        assertEquals(VectorOrientation.ROW_MAJOR, matrixA.getOrientation());

        // Validation 2: Ensure null check is active
        assertThrows(IllegalArgumentException.class, () -> { 
            matrixA.loadRowMajor(null); 
        });
    }

    @Test
    public void testVerticalStorage() {
        // Validation 1: Verify successful vertical (Column-Major) data ingestion
        SharedMatrix matrixB = new SharedMatrix();
        double[][] rawMatrix = {{1.1, 2.2, 3.3}, {4.4, 5.5, 6.6}};
        matrixB.loadColumnMajor(rawMatrix);
        
        assertEquals(3, matrixB.length()); 
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrixB.getOrientation());
        assertArrayEquals(rawMatrix, matrixB.readRowMajor());

        // Validation 2: Ensure null check is active
        assertThrows(IllegalArgumentException.class, () -> { 
            matrixB.loadColumnMajor(null); 
        });

        // Validation 3: Edge case - handling an empty dataset
        double[][] blankInput = {};
        SharedMatrix matrixC = new SharedMatrix();
        matrixC.loadColumnMajor(blankInput);

        double[][] outputGrid = matrixC.readRowMajor();
        assertArrayEquals(blankInput, outputGrid);
    }

    @Test
    public void testDataRetrieval() {
        // Validation 1: Reading an uninitialized/empty matrix
        SharedMatrix matrixD = new SharedMatrix();
        double[][] emptyResult = matrixD.readRowMajor();
        assertEquals(0, emptyResult.length);

        // Validation 2: Confirming Row Major persistence
        double[][] rowSet = {{5.0, 6.0}, {7.0, 8.0}};
        matrixD.loadRowMajor(rowSet);
        assertArrayEquals(rowSet, matrixD.readRowMajor());

        // Validation 3: Confirming Column Major persistence
        matrixD.loadColumnMajor(rowSet);
        assertArrayEquals(rowSet, matrixD.readRowMajor());
    }

    @Test
    public void testVectorAccess() {
        // Validation 1: Successful retrieval of a vector at a specific index
        SharedMatrix matrixE = new SharedMatrix(new double[][]{{100.0, 200.0, 300.0}, {400.0, 500.0, 600.0}});
        SharedVector segment = matrixE.get(1); // Accessing second row/column
        assertEquals(400.0, segment.get(0));

        // Validation 2: Bound check - Negative index
        assertThrows(IndexOutOfBoundsException.class, () -> { 
            matrixE.get(-1); 
        });

        // Validation 3: Bound check - Index exceeding size
        assertThrows(IndexOutOfBoundsException.class, () -> { 
            matrixE.get(10); 
        });
    }

    @Test
    public void testCurrentStateOrientation() {
        // Validation 1: Initial state or standard row-major load
        SharedMatrix matrixF = new SharedMatrix(new double[][]{{9.9}});
        assertEquals(VectorOrientation.ROW_MAJOR, matrixF.getOrientation());

        // Validation 2: After switching to column-major
        matrixF.loadColumnMajor(new double[][]{{9.9}});
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrixF.getOrientation());
    }
}