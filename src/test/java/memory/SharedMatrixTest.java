package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedMatrixTest {

    @Test
    public void testMatrixConstructor() {
        //test 1: Null array
        assertThrows(IllegalArgumentException.class, () -> {new SharedMatrix(null);});
    }

    @Test
    public void testLoadRowMajor() {
        //test 1: Successful load
        SharedMatrix mat1 = new SharedMatrix();
        double[][] input = {{1.0, 2.0}, {3.0, 4.0}};
        mat1.loadRowMajor(input);
        
        double[][] actOutput = mat1.readRowMajor();
        assertArrayEquals(input, actOutput);
        assertEquals(VectorOrientation.ROW_MAJOR, mat1.getOrientation());

        //test 2: Null matrix
        assertThrows(IllegalArgumentException.class, () -> {mat1.loadRowMajor(null);});
    }

    @Test
    public void testLoadColumnMajor() {
        //test 1: Successful load
        SharedMatrix mat1 = new SharedMatrix();
        double[][] input = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        mat1.loadColumnMajor(input);
        
        assertEquals(3, mat1.length()); 
        assertEquals(VectorOrientation.COLUMN_MAJOR, mat1.getOrientation());
        assertArrayEquals(input, mat1.readRowMajor());

        //test 2: Null matrix
        assertThrows(IllegalArgumentException.class, () -> {mat1.loadColumnMajor(null);});

        //test 3: Empty matrix
        double[][] emptyInput = {};
        SharedMatrix mat2 = new SharedMatrix();
        mat2.loadColumnMajor(emptyInput);

        double[][] actOutput = mat2.readRowMajor();
        assertArrayEquals(emptyInput, actOutput);
    }

    @Test
    public void testReadRowMajor() {
        //test 1: Read empty matrix
        SharedMatrix mat1 = new SharedMatrix();
        double[][] emptyRes = mat1.readRowMajor();
        assertEquals(0, emptyRes.length);

        //test 2: Read Row Major Data
        double[][] dataRow = {{1.0, 2.0}, {3.0, 4.0}};
        mat1.loadRowMajor(dataRow);
        assertArrayEquals(dataRow, mat1.readRowMajor());

        //test 3: Read Column Major Data
        mat1.loadColumnMajor(dataRow);
        assertArrayEquals(dataRow, mat1.readRowMajor());
    }

    @Test
    public void testGet() {
        //test 1: Successful Get
        SharedMatrix mat1 = new SharedMatrix(new double[][]{{1.0, 2.0, 3.0},{6.0,1.0,1.0}});
        SharedVector v = mat1.get(1);
        assertEquals(6.0, v.get(0));

        //test 2: Index out of bounds (Negative)
        assertThrows(IndexOutOfBoundsException.class, () -> {mat1.get(-1);});

        //test 3: Index out of bounds (Too large)
        assertThrows(IndexOutOfBoundsException.class, () -> {mat1.get(4);});
    }

    @Test
    public void testGetOrientation() {
        //test 1: Row Major
        SharedMatrix mat1 = new SharedMatrix(new double[][]{{1.0}});
        assertEquals(VectorOrientation.ROW_MAJOR, mat1.getOrientation());

        //test 2: Column Major
        mat1.loadColumnMajor(new double[][]{{1.0}});
        assertEquals(VectorOrientation.COLUMN_MAJOR, mat1.getOrientation());
    }
}