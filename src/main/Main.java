package main;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import persister.BmpFileWriter;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
	private static Logger logger = LogManager.getRootLogger();
	
    public static void main(String[] args) throws Exception {
    	
    	System.out.println("Total space " + Runtime.getRuntime().totalMemory() + " bytes");
    	System.out.println("Free space " + Runtime.getRuntime().freeMemory() + " bytes");
    	System.out.println("Used space " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " bytes");
    	
    	int[][] twoDim = new int[20][];
    	for(int i=0; i<20; i++) twoDim[i]=new int[10];
    	for(int i=0; i<20; i++)
    		for(int j=0; j<10; j++)
    			twoDim[i][j] = i+j;
    	
    	System.out.println("Total space " + Runtime.getRuntime().totalMemory() + " bytes");
    	System.out.println("Free space " + Runtime.getRuntime().freeMemory() + " bytes");
    	System.out.println("Used space " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " bytes");
    	
    	long maxMemory = Runtime.getRuntime().maxMemory()/1_000_000;
    	logger.info("Max memory: " + maxMemory + " Mb");
    	
    	AppProperties appProperties = new AppProperties();
    	
    	//sets the root of the file system, that will be scanned for jpeg images
    	String inputPath = appProperties.inputPath;
    	//sets the root of the file system, that will persist bmp images(the result
    	//of jpeg transformation, found in inputPath)
    	String outputPath = appProperties.outputPath;
    	
    	new BmpFileWriter().writeAll(Paths.get(inputPath), Paths.get(outputPath));
    	
    	logger.info("PROCESS HAS BEEN FINISHED");
    }
}
