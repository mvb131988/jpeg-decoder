package util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

/**
 * To invert image, for each row would be created separate file.
 * Writes single row (appends sample by sample) to the row file.
 */
public class FileSystemReverseOrderRowWriter implements AutoCloseable {

	private OutputStream os;
	
	public FileSystemReverseOrderRowWriter(int componentId, int rowNumber) throws IOException {
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()).resolve("rows"));
		
		Path p = Paths.get(AppProperties.getTmpPath()) 
					  .resolve("rows")
					  .resolve("component_" + componentId + 
							   "_row_" + rowNumber);
		
		Files.deleteIfExists(p);
		Files.createFile(p);
		os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE), 262_144);
	}
	
	public void write(int sample) throws IOException {
		os.write(sample);
	}
	
	@Override
	public void close() throws IOException {
		os.close();
	}
	
}
