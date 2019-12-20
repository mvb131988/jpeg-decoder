package persister;

import java.io.IOException;
import java.io.OutputStream;
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
import markers.Image;
import util.ComponentExpander;
import util.ComponentRotator;
import util.ComponentSquasher;
import util.FileSystemBmpAssembler;
import util.TmpDirManager;

public class JpegsProcessingProcedure {
    
	private static Logger logger = LogManager.getRootLogger();
	
	private ComponentExpander ce = new ComponentExpander();
	
	private ComponentRotator cr = new ComponentRotator();
	
	private ComponentSquasher cs = new ComponentSquasher();
	
	private TmpDirManager tmpDirManager = new TmpDirManager();
	
	public JpegsProcessingProcedure() throws IOException {
		tmpDirManager.init();
	}
	
    /**
     * 
     * @param root - root of the file system, that will persist bmp images(the result
     *    			 of jpeg transformation, found in inputPath)
     * @param relativePath - path, relative to the root without fileName
     * @param fileName - bmp file name
     * @param pixels
     * @throws IOException
     */
    public void write(Path root, Path relativePath, Path fileName, Pixel[][] pixels) throws IOException {
        if (!Files.exists(root.resolve(relativePath))) {
    		Files.createDirectories(root.resolve(relativePath));
    	}
    	
        BmpFile f = new BmpFile(pixels);
        //final absolute path of the bmp file
        Path path = root.resolve(relativePath).resolve(fileName);
        try(OutputStream os = Files.newOutputStream(path)) {
            os.write(f.rawHeader());
            os.write(f.rawPixels());
        }
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

    	private JpegsProcessingProcedure writer;
    	
    	private Path inputRoot;
    	
    	private Path outputRoot;
    	
    	public JpegFilesVisitor(JpegsProcessingProcedure writer, Path inputRoot, Path outputRoot) {
    		this.writer = writer;
    		this.inputRoot = inputRoot;
    		this.outputRoot = outputRoot;
    	}
    	
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			writer.tmpDirManager.clean();
			
			String fileName = file.getFileName().toString();
			String extension = null; 
			if(fileName.lastIndexOf(".") != -1) {
				extension = fileName.substring(fileName.lastIndexOf(".")+1);
				if(extension.equals("jpg")) {
					//bmp file relative path including file name
					Path rpBmp = bmpRelativePath(file);
					try {
						DecoderContext dc = new DecoderContext();
						DecoderControlProcedure dcp = new DecoderControlProcedure(file.toString());
						Image img = dcp.decodeImage(dc);
						
						writer.ce.extend(dc);
						writer.cr.rotate(dc);
						writer.cs.squash(dc);
						
						FileSystemBmpAssembler fsba= new FileSystemBmpAssembler();
						Pixel[][] pixels = fsba.convert(dc, rpBmp.getFileName().toString());
						
//						pixels = new PixelConverter().scale(pixels);
//						Pixel[][] pixels = pc.scale(pc.convert(img));
						
//						writer.write(outputRoot, 
//									 rpBmp.getParent() == null ? Paths.get("") : rpBmp.getParent(), 
//									 rpBmp.getFileName(), 
//									 pixels);

						logger.info(file + " is processed");
					} catch (Throwable th) {
						logger.error(file + " fails with " + th.toString());
						th.printStackTrace();
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
    	
		/**
		 * 
		 * @param apJpeg - jpeg absolute path
		 * @param rpJpeg - jpeg realative path
		 * @return
		 */
		private Path bmpRelativePath(Path apJpeg) {
			//jpg file relative path including file name
			Path rpJpeg = inputRoot.relativize(apJpeg);
			
			//file extension substitution
			String srp0 = rpJpeg.toString();
			srp0 = srp0.substring(0, srp0.lastIndexOf(".")).concat(".bmp");
			//bmp file relative path including file name
			Path rpBmp = Paths.get(srp0);
			
			return rpBmp;
		}
    
    }
    
}
