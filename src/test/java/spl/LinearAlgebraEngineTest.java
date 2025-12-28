package spl;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import parser.ComputationNode;
import parser.InputParser;


public class LinearAlgebraEngineTest {
    // @Test
    // public void testLinearAlgebraEngine() {
    //     int numOfThreads = 6;
    //     String inputPath = "myexample.json";
    //     InputParser parser = new InputParser();
    //     try{
    //         ComputationNode compRoot = parser.parse(inputPath);
    //         LinearAlgebraEngine lae = new LinearAlgebraEngine(numOfThreads); // Purposfully created here to 
    //         ComputationNode nodeResult = lae.run(compRoot); // avoid executor shutdown upon parser failure
    //         double[][] result = nodeResult.getMatrix();
    //     }
    //     catch(Exception e){
    //         //Won't happen while testing
    //     }
    // }
}
