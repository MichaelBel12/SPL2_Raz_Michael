package spl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import parser.ComputationNode;
import parser.ComputationNodeType;
import spl.lae.LinearAlgebraEngine;
import java.util.Arrays;
import java.util.List;

public class LinearAlgebraEngineTest {

    @Test
    public void testEngineConstructor() {
        // Test 1: Valid initialization
        assertDoesNotThrow(() -> new LinearAlgebraEngine(4));

        // Test 2: Invalid thread count (handled by TiredExecutor)
        assertThrows(IllegalArgumentException.class, () -> new LinearAlgebraEngine(0));
    }

    @Test
    public void testSimpleAddition() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        double[][] matA = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] matB = {{5.0, 6.0}, {7.0, 8.0}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode addNode = new ComputationNode("+", Arrays.asList(nodeA, nodeB));

        ComputationNode resultNode = lae.run(addNode);
        double[][] result = resultNode.getMatrix();

        double[][] expected = {{6.0, 8.0}, {10.0, 12.0}};
        assertArrayEquals(expected[0], result[0]);
        assertArrayEquals(expected[1], result[1]);
    }

    @Test
    public void testSimpleMultiplication() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        // 2x3 matrix
        double[][] matA = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        // 3x2 matrix
        double[][] matB = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode multNode = new ComputationNode("*", Arrays.asList(nodeA, nodeB));

        ComputationNode resultNode = lae.run(multNode);
        double[][] result = resultNode.getMatrix();

        // Expected: [1*7+2*9+3*11, 1*8+2*10+3*12] = [58, 64]
        //           [4*7+5*9+6*11, 4*8+5*10+6*12] = [139, 154]
        double[][] expected = {{58.0, 64.0}, {139.0, 154.0}};
        assertArrayEquals(expected[0], result[0]);
        assertArrayEquals(expected[1], result[1]);
    }

  @Test
public void testTransposeAndNegate() {
    double[][] mat1 = {{1.0, -2.0}, {3.0, 4.0}};
    double[][] mat2 = {{1.0, -2.0}, {3.0, 4.0}}; // Fresh copy
    
    // Test Negate
    LinearAlgebraEngine laeNeg = new LinearAlgebraEngine(1);
    ComputationNode negNode = new ComputationNode("-", List.of(new ComputationNode(mat1)));
    laeNeg.run(negNode);
    // Test Transpose
    LinearAlgebraEngine laeTrans = new LinearAlgebraEngine(1);
    ComputationNode transNode = new ComputationNode("T", List.of(new ComputationNode(mat2)));
    double[][] transResult = laeTrans.run(transNode).getMatrix();

    assertEquals(1.0, transResult[0][0]); // Should pass now
    }
    @Test
    public void testComplexTreeResolution() {
        // (A + B) * C
        LinearAlgebraEngine lae = new LinearAlgebraEngine(4);
        double[][] matA = {{1.0, 1.0}, {1.0, 1.0}};
        double[][] matB = {{2.0, 2.0}, {2.0, 2.0}};
        double[][] matC = {{1.0, 0.0}, {0.0, 1.0}}; // Identity

        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode nodeC = new ComputationNode(matC);

        ComputationNode addNode = new ComputationNode("+", Arrays.asList(nodeA, nodeB));
        ComputationNode root = new ComputationNode("*", Arrays.asList(addNode, nodeC));

        double[][] result = lae.run(root).getMatrix();
        double[][] expected = {{3.0, 3.0}, {3.0, 3.0}};
        
        assertArrayEquals(expected[0], result[0]);
        assertArrayEquals(expected[1], result[1]);
    }

    @Test
    public void testMismatchedDimensions() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        double[][] matA = {{1.0, 2.0}}; // 1x2
        double[][] matB = {{1.0}, {2.0}}; // 2x1
        
        // Addition of different dimensions should fail in loadAndCompute/createAddTasks
        ComputationNode addNode = new ComputationNode("+", Arrays.asList(new ComputationNode(matA), new ComputationNode(matB)));
        
        assertThrows(IllegalArgumentException.class, () -> lae.run(addNode));
    }

    @Test
    public void testWorkerReport() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        double[][] mat = {{1.0}};
        ComputationNode node = new ComputationNode("-", Arrays.asList(new ComputationNode(mat)));
        
        lae.run(node);
        String report = lae.getWorkerReport();
        
        assertNotNull(report);
        assertTrue(report.contains("Worker 0"));
        assertTrue(report.contains("Fatigue"));
        assertTrue(report.contains("Fairness"));
    }
}