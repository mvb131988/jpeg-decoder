package processor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
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
   * @param cooldownFile - time two wait between two consequent file processing
   * @throws IOException 
   */
  public void writeAll(Path inputRoot, Path outputRoot, long cooldownFile) throws IOException {
  	Files.walkFileTree(inputRoot, new JpegFilesVisitor(this, inputRoot, outputRoot, cooldownFile));
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
  	
  	private long cooldownFile;
  	
  	private JpegProcessDecision jpd;
  	
  	public JpegFilesVisitor(JpegsProcessingProcedure jpp,
  	                        Path inputRoot,
  	                        Path outputRoot,
  	                        long cooldownFile) {
  		this.jpp = jpp;
  		this.jpd = new JpegProcessDecision(inputRoot, outputRoot);
  		this.cooldownFile = cooldownFile;
  	}
  	
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      jpp.tmpDirManager.clean();

      JpegProcessDecision.JpegProcessDecisionContext result = jpd.decide(file);
      
      if (result.isJpeg && result.isProcessable) {
        try {
          logger.info(file + " start processing");

          DecoderContext dc = new DecoderContext();
          DecoderControlProcedure dcp = new DecoderControlProcedure(file.toString());
          dcp.decodeImage(dc);

          logger.debug("Extension MCUs start (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          jpp.ce.extend(dc);

          logger.debug("Extension MCUs finished (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          logger.debug("Rotation MCUs start (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          jpp.csp.rotate(dc);

          logger.debug("Rotation MCUs finished (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          logger.debug("Squashing MCUs start (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          jpp.csq.squash(dc);

          logger.debug("Squashing MCUs finished (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          logger.debug("Bmp assembling start (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          jpp.ca.convert(dc, result.bmpName.toString());

          logger.debug("Bmp assembling finished (used space) "
              + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000 + " MB");

          logger.info(file + " is processed");

          // cooldown to not overload the machine
          Thread.sleep(cooldownFile);
        } catch (Throwable th) {
          logger.error(file + " fails with " + th.toString());
          th.printStackTrace();
        }
      } else {
        if(result.isJpeg) {
          logger.info("Skipping already existed file: " + result.bmpPath);
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
    	
  }
    
}
