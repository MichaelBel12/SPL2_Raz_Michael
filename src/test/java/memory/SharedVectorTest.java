package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {

    @Test
    public void testVectorInitialization() {
        // Validation 1: Rejection of null data arrays
        assertThrows(IllegalArgumentException.class, () -> {
            new SharedVector(null, VectorOrientation.ROW_MAJOR);
        });
        
        // Validation 2: Rejection of missing orientation metadata
        assertThrows(IllegalArgumentException.class, () -> {
            new SharedVector(new double[]{5.5, 6.5}, null);
        });
    }

    @Test
    public void testElementWiseAddition() {
        // Validation 1: Successful vector sum
        SharedVector alpha = new SharedVector(new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0}, VectorOrientation.ROW_MAJOR);
        SharedVector beta = new SharedVector(new double[]{2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0}, VectorOrientation.ROW_MAJOR);
        alpha.add(beta);
        
        double[] expectedResult = {2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0};
        assertArrayEquals(expectedResult, alpha.getVector());

        // Validation 2: Size mismatch check
        SharedVector gamma = new SharedVector(new double[]{10.0, 20.0}, VectorOrientation.ROW_MAJOR);
        SharedVector delta = new SharedVector(new double[]{10.0, 20.0, 30.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> { gamma.add(delta); });

        // Validation 3: Handling empty vector addition
        SharedVector emptyVec = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> { gamma.add(emptyVec); });

        // Validation 4: Orientation mismatch check
        SharedVector verticalVec = new SharedVector(new double[]{10.0, 20.0}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> { gamma.add(verticalVec); });
    }

    @Test
    public void testLinearTransformation() {
        // Validation 1: Row Vector by Matrix multiplication
        SharedVector baseVector = new SharedVector(new double[]{2.0, 2.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix transformMat = new SharedMatrix(new double[][]{{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0}});
        baseVector.vecMatMul(transformMat);
        
        // Calculation: [2*1 + 2*2 + 2*3, 2*1 + 2*2 + 2*3] = [12, 12]
        double[] targetOutput = {12.0, 12.0};
        assertArrayEquals(targetOutput, baseVector.getVector());

        // Validation 2: Vector multiplication with Column-Major Matrix
        SharedVector secondaryVector = new SharedVector(new double[]{2.0, 2.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix colMatrix = new SharedMatrix();
        colMatrix.loadColumnMajor(new double[][]{{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0}});
        secondaryVector.vecMatMul(colMatrix);

        assertArrayEquals(targetOutput, secondaryVector.getVector());

        // Validation 3: Dimensionality constraint check
        SharedVector mismatchVec = new SharedVector(new double[]{1.0, 1.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix tooLargeMat = new SharedMatrix(new double[][]{{1.0}, {1.0}, {1.0}});
        assertThrows(IllegalArgumentException.class, () -> { mismatchVec.vecMatMul(tooLargeMat); });

        // Validation 4: Identity case - Empty structures
        SharedVector nullVec = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedMatrix nullMat = new SharedMatrix();
        nullVec.vecMatMul(nullMat);
        assertArrayEquals(new double[]{}, nullVec.getVector());

        // Validation 5: Verify only Row Vectors can initiate multiplication
        SharedVector invalidOrientVec = new SharedVector(new double[]{1.0, 1.0}, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix simpleMat = new SharedMatrix(new double[][]{{5.0}});
        assertThrows(IllegalArgumentException.class, () -> { invalidOrientVec.vecMatMul(simpleMat); });
    }

    @Test
    public void testOrientationFlip() {
        // Validation 1: Change Row to Column
        SharedVector pivotVec = new SharedVector(new double[]{10.0, 20.0, 30.0}, VectorOrientation.ROW_MAJOR);
        pivotVec.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, pivotVec.getOrientation());

        // Validation 2: Change Column to Row
        SharedVector resetVec = new SharedVector(new double[]{0.1, 0.2}, VectorOrientation.COLUMN_MAJOR);
        resetVec.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, resetVec.getOrientation());
    }

    @Test
    public void testValueInversion() {
        // Validation 1: Sign flipping for all elements
        SharedVector source = new SharedVector(new double[]{-1.5, 3.0, 0.0, -10.5}, VectorOrientation.ROW_MAJOR);
        source.negate();
        double[] expectedValues = {1.5, -3.0, -0.0, 10.5};
        
        assertArrayEquals(expectedValues, source.getVector());
    }

    @Test
    public void testBoundaryChecks() {
        SharedVector probe = new SharedVector(new double[]{7.0, 8.0, 9.0}, VectorOrientation.ROW_MAJOR);
        
        // Validation 1: High-end bounds overflow
        assertThrows(IndexOutOfBoundsException.class, () -> { probe.get(100); });
        
        // Validation 2: Low-end bounds underflow
        assertThrows(IndexOutOfBoundsException.class, () -> { probe.get(-5); });
    }
}