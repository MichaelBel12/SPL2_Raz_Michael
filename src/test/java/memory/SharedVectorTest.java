package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {
    @Test
    public void testVectorConstructor() {
        //test 1: Null array
        assertThrows(IllegalArgumentException.class, () -> {new SharedVector(null, VectorOrientation.ROW_MAJOR);});
        //test 2: Null orientation
        assertThrows(IllegalArgumentException.class, () -> {new SharedVector(new double[]{1.0,2.0}, null);});
    }

    @Test
    public void testVectorAddition() {
        //test 1: Successful addition
        SharedVector vec1 = new SharedVector(new double[]{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0}, VectorOrientation.ROW_MAJOR);
        SharedVector vec2 = new SharedVector(new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, VectorOrientation.ROW_MAJOR);
        vec1.add(vec2);
        double[] expctOutput = {2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0};
        double[] actOutput = vec1.getVector();

        assertArrayEquals(expctOutput, actOutput);
        
        //test 2: Non-matching dimensions
        SharedVector vec3 = new SharedVector(new double[]{1.0,2.0,3.0,4.0}, VectorOrientation.ROW_MAJOR);
        SharedVector vec4= new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> {vec3.add(vec4);});

        //test 3: vector + {}
        SharedVector vec5 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> {vec3.add(vec5);});

        //test 4: Non-matching orientations
        SharedVector vec6 = new SharedVector(new double[]{3.0,4.0,5.0}, VectorOrientation.COLUMN_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> {vec4.add(vec6);});

    }

    @Test
    public void testVectorVecMatMul() {
        //test 1: Successful Row Matrix Multiplication
        SharedVector vec1 = new SharedVector(new double[]{1.0,1.0,1.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix mat1 = new SharedMatrix(new double[][]{{2.0,2.0},{1.0,1.0},{5.0,5.0}});
        vec1.vecMatMul(mat1);
        double[] expctOutput1 = {8.0,8.0};

        assertArrayEquals(expctOutput1, vec1.getVector());

        //test 2: Successful Column Matrix Multiplication
        SharedVector vec2 = new SharedVector(new double[]{1.0,1.0,1.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix mat2 = new SharedMatrix();
        mat2.loadColumnMajor(new double[][]{{2.0,2.0},{1.0,1.0},{5.0,5.0}});
        vec2.vecMatMul(mat2);

        assertArrayEquals(expctOutput1, vec2.getVector());

        //test 3: Non-matching dimensions
        SharedVector vec3 = new SharedVector(new double[]{1.0,1.0,1.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix mat3 = new SharedMatrix(new double[][]{{2.0,2.0},{1.0,1.0}});

        assertThrows(IllegalArgumentException.class, () -> {vec3.vecMatMul(mat3);});

        //test 4: Zero vector * Zero matrix
        SharedVector vec4 = new SharedVector(new double[]{}, VectorOrientation.ROW_MAJOR);
        SharedMatrix mat4 = new SharedMatrix();
        vec4.vecMatMul(mat4);

        assertArrayEquals(new double[]{}, vec4.getVector());

        //test 5: Vector orientation is Column
        SharedVector vec5 = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix mat5 = new SharedMatrix(new double[][]{{7.0}});

        assertThrows(IllegalArgumentException.class, () -> {vec5.vecMatMul(mat5);});
    }

    @Test
    public void testVectorTranspose() {
        //test 1: Successful Row -> Column
        SharedVector vec1 = new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.ROW_MAJOR);
        vec1.transpose();

        assertEquals(VectorOrientation.COLUMN_MAJOR, vec1.getOrientation());

        //test 2: Succesful Column -> Row
        SharedVector vec2 = new SharedVector(new double[]{4.0,2.0,4.0}, VectorOrientation.COLUMN_MAJOR);
        vec2.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, vec2.getOrientation());

        //null vector is handled in Constructor.
    }

    @Test
    public void testVectorNegate(){
        //test 1: Successful negate
        SharedVector vec1 = new SharedVector(new double[]{10.0,5.0,6.0,-9.0}, VectorOrientation.ROW_MAJOR);
        vec1.negate();
        double[] expctOutput = {-10.0,-5.0,-6.0,9.0};
        
        assertArrayEquals(expctOutput, vec1.getVector());

        //null vector is handled in Constructor.
    }

    @Test
    public void testVectorGet() {
        SharedVector vec1 = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        //test 1: Index out of bounds (Too large)
        assertThrows(IndexOutOfBoundsException.class, () -> {vec1.get(10);});
        //test 2: Index out of bounds (Negative)
        assertThrows(IndexOutOfBoundsException.class, () -> {vec1.get(-1);});
    }
}