package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemReverseOrderRowReader implements AutoCloseable {

	private InputStream is;
	
	private Path p;
	
	public FileSystemReverseOrderRowReader(int componentId, int rowNumber) throws IOException {
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()).resolve("rows"));
		
		p = Paths.get(AppProperties.getTmpPath()) 
				 .resolve("rows")
				 .resolve("component_" + componentId + 
						  "_row_" + rowNumber);
		
		is = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ), 262_144);
	}
	
	public int read() throws IOException {
		int sample = is.read();
		return sample;
	}

	@Override
	public void close() throws Exception {
		is.close();
		Files.deleteIfExists(p);
	}
	
}
