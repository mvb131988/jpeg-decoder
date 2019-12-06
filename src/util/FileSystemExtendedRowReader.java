package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemExtendedRowReader implements AutoCloseable {

	private InputStream is;

	// component serial number(value from {0,1,2})
	private int componentId;

	// serial number of DU row within MCU (starts with 0)
	private int rowNumber;

	private Path p;

	public FileSystemExtendedRowReader(int componentId, int rowNumber) throws IOException {
		this.componentId = componentId;
		this.rowNumber = rowNumber;
		
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()));
		
		p = Paths.get(AppProperties.getTmpPath() + 
									"ext_component_" + componentId + 
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
