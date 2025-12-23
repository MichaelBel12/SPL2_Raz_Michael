package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;
import scheduling.TiredExecutor;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      if(args.length != 3){
        throw new IOException("[Usage error] Invalid use! use command- java -jar target/lga-1.0.jar <number of threads>" + 
        " <path/to/input/file> <path/to/output/file>");
      }
      int numOfThreads = Integer.parseInt(args[0]);
      String inputPath = args[1];
      String outputPath = args[2];
      InputParser parser = new InputParser();
      try{
          ComputationNode root = parser.parse(inputPath);
          LinearAlgebraEngine lae = new LinearAlgebraEngine(numOfThreads);
          ComputationNode nodeResult = lae.run(root);
          double[][] result = nodeResult.getMatrix();
          OutputWriter.write(result, outputPath);
      }
      catch(ParseException e){
          OutputWriter.write(e.getMessage(), outputPath);
      }
      catch(Exception e){
          OutputWriter.write("ERROR: " + e.getMessage(), outputPath);
      }
      
      
      
    }
}