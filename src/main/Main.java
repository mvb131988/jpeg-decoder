package main;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import processor.JpegsProcessingProcedure;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
	private static Logger logger = LogManager.getRootLogger();
	
    public static void main(String[] args) {
    	
    	try {
  	      Thread.sleep(30_000);
  	    } catch (InterruptedException e) {
  	      // TODO Auto-generated catch block
  	      e.printStackTrace();
  	    }
    	
    	//sets the root of the file system, that will be scanned for jpeg images
    	String inputPath = AppProperties.getInputPath();
    	//sets the root of the file system, that will persist bmp images(the result
    	//of jpeg transformation, found in inputPath)
    	String outputPath = AppProperties.getOutputPath();
    	
    	long cooldownRepo = AppProperties.getCooldownRepo();
    	long cooldownFile = AppProperties.getCooldownFile();

		for (;;) {
			logger.info("Jpegs processing has started");
			try {
				new JpegsProcessingProcedure().writeAll(Paths.get(inputPath), 
														Paths.get(outputPath), 
														cooldownFile);
			} catch (IOException e) {
				logger.error("Main unexpectedly crashing: ", e);
			}
			
			try {
				Thread.sleep(cooldownRepo);
			} catch (InterruptedException e) {
				logger.error("Main unexpectedly crashing during sleep pause: ", e);
			}
			logger.info("Jpegs processing has finished");
		}
    }
}
