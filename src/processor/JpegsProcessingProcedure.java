package processor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import decoder.DecoderContext;
import decoder.DecoderControlProcedure;
import util.TmpDirManager;

public class JpegsProcessingProcedure {
    
	private static Logger logger = LogManager.getRootLogger();
	
	private ComponentExpander ce = new ComponentExpander();
	
	private ComponentSplitter csp = new ComponentSplitter();
	
	private ComponentSquasher csq = new ComponentSquasher();
	
	private ComponentsAssembler ca= new ComponentsAssembler();
	
	private TmpDirManager tmpDirManager = new TmpDirManager();
	
	public JpegsProcessingProcedure() throws IOException {
		tmpDirManager.init();
	}
    
    /**
     * Finds all jpeg files in all sub directories relative to the root path,
     * transforms them and writes in output bmp files.  
     *
     * @param inputRoot - root of the file system with jpeg files
     * @param outputRoot - root of the file system with bmp files
     * @throws IOException 
     */
    public void writeAll(Path inputRoot, Path outputRoot) throws IOException {
    	Files.walkFileTree(inputRoot, new JpegFilesVisitor(this, inputRoot, outputRoot));
    }	
    
    /**
     * Visits any jpeg file that lies into file system defined by input root path,
     * tries to create bmp file and store it into file system defined by output
     * root path. 
     * 
     * IMPORTANT: relative file path(relative from the root) in output file system 
     * 			  is preserved the same as relative file path in input file system. 
     */
    private static class JpegFilesVisitor implements FileVisitor<Path> {

    	private JpegsProcessingProcedure jpp;
    	
    	private Path inputRoot;
    	
    	private Path outputRoot;
    	
    	public JpegFilesVisitor(JpegsProcessingProcedure jpp, Path inputRoot, Path outputRoot) {
    		this.jpp = jpp;
    		this.inputRoot = inputRoot;
    		this.outputRoot = outputRoot;
    	}
    	
  		@Override
  		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
  			return FileVisitResult.CONTINUE;
  		}
  
  		@Override
  		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
  			jpp.tmpDirManager.clean();
  			
  			String fileName = file.getFileName().toString();
  			String extension = null; 
  			if(fileName.lastIndexOf(".") != -1) {
  				extension = fileName.substring(fileName.lastIndexOf(".")+1);
  				if(extension.equals("jpg")) {
  					String bmpName = bmpFileName(file).toString();
  					
  					Path bmpOutputPath = bmpOutputPath(bmpFileName(file));
  					//if min img exists don't recreate it
  					if(!Files.exists(bmpOutputPath)) {
    					try {
    						logger.info(file + " start processing");
    						
    						DecoderContext dc = new DecoderContext();
    						DecoderControlProcedure dcp = new DecoderControlProcedure(file.toString());
    						dcp.decodeImage(dc);
    						
    						logger.info("Extension MCUs start (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						jpp.ce.extend(dc);
    						
    						logger.info("Extension MCUs finished (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						logger.info("Rotation MCUs start (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						jpp.csp.rotate(dc);
    						
    						logger.info("Rotation MCUs finished (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						logger.info("Squashing MCUs start (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						jpp.csq.squash(dc);
    						
    						logger.info("Squashing MCUs finished (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						logger.info("Bmp assembling start (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						jpp.ca.convert(dc, bmpName);
    						
    						logger.info("Bmp assembling finished (used space) " + 
    				        		(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
    						
    						logger.info(file + " is processed");
    					} catch (Throwable th) {
    						logger.error(file + " fails with " + th.toString());
    						th.printStackTrace();
    					}
  					} else {
  					  logger.info("Skipping already existed file: " + bmpOutputPath);
  					}
  				}
  			}
  			return FileVisitResult.CONTINUE;
  		}
  
  		@Override
  		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
  			return FileVisitResult.CONTINUE;
  		}
  
  		@Override
  		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
  			return FileVisitResult.CONTINUE;
  		}
      	
  		private Path bmpFileName(Path jpegFileName) {
  			//jpg file relative path including file name
  			Path rpJpeg = inputRoot.relativize(jpegFileName);
  			
  			//file extension substitution
  			String srp0 = rpJpeg.toString();
  			srp0 = srp0.substring(0, srp0.lastIndexOf(".")).concat(".bmp");
  			//bmp file relative path including file name
  			Path rpBmp = Paths.get(srp0);
  			
  			return rpBmp;
  		}
  		
  		/**
  		 * 
  		 * @param bmpFileRelativePath - part of the bmp file path relative to the output root
  		 * @return
  		 */
  		private Path bmpOutputPath(Path bmpFileRelativePath) {
  		  return Paths.get(outputRoot.toString(), bmpFileRelativePath.toString());
      }
  		
    }
    
}
