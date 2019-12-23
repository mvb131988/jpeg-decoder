package util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemSquashedComponentWriter implements AutoCloseable {
	
	private OutputStream os;

	// component serial number(value from {0,1,2})
	private int componentId;

	public FileSystemSquashedComponentWriter(int componentId) throws IOException {
		this.componentId = componentId;
		
		Path p = Paths.get(AppProperties.getTmpPath() + 
						   "squashed_component_" + componentId);
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
