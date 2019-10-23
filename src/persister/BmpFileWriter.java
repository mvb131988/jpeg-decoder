package persister;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import decoder.DecoderControlProcedure;
import markers.Image;

public class BmpFileWriter {
    
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
    
    private static class JpegFilesVisitor implements FileVisitor<Path> {

    	private BmpFileWriter writer;
    	
    	private Path inputRoot;
    	
    	private Path outputRoot;
    	
    	public JpegFilesVisitor(BmpFileWriter writer, Path inputRoot, Path outputRoot) {
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
			String fileName = file.getFileName().toString();
			String extension = null; 
			if(fileName.lastIndexOf(".") != -1) {
				extension = fileName.substring(fileName.lastIndexOf(".")+1);
				if(extension.equals("jpg")) {
					//bmp file relative path including file name
					Path rp = inputRoot.relativize(file);
					String rp0 = rp.toString();
					rp0 = rp0.substring(0, rp0.lastIndexOf(".")).concat(".bmp");
					
					Pixel[][] pixels = null;
					try {
						DecoderControlProcedure dcp = new DecoderControlProcedure(file.toString());
						Image img = dcp.decodeImage();
						PixelConverter pc = new PixelConverter(); 
						pixels = pc.scale(pc.convert(img));
					} catch (Exception e) {
						System.out.println("log here");
					}
					
					//relative path(relative to the root) without file name
					Path prp = Paths.get(rp0).getParent();
					prp = prp == null ? Paths.get("") : prp;
					writer.write(outputRoot, prp, Paths.get(rp0).getFileName(), pixels);
					
					System.out.println(file + " is processed");
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
