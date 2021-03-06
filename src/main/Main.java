package main;

import java.nio.file.Paths;

import persister.BmpFileWriter;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
    public static void main(String[] args) throws Exception {
    	//sets the root of the file system, that will be scanned for jpeg images
    	String inputPath = AppProperties.getInputPath();
    	//sets the root of the file system, that will persist bmp images(the result
    	//of jpeg transformation, found in inputPath)
    	String outputPath = AppProperties.getOutputPath();
    	
    	new BmpFileWriter().writeAll(Paths.get(inputPath), Paths.get(outputPath));
    }
}
