package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemComponentReader implements AutoCloseable {

	private InputStream is;
	
	//component serial number(value from {0,1,2})
	private int componentId;
	
	private Path p;
	
	public FileSystemComponentReader(int componentId) throws IOException {
		this.componentId = componentId;
		
		p = Paths.get(AppProperties.getTmpPath() + "component_" + componentId);
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
