package persister;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
    
}
