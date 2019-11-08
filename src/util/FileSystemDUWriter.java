package util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemDUWriter implements AutoCloseable {
	
	private OutputStream os;
	
	//component serial number(value from {0,1,2})
	private int componentId;
	
	//serial number of DU row within MCU (starts with 0)
	private int rowNumber;
	
	public FileSystemDUWriter(int componentId, int rowNumber) throws IOException {
		this.componentId = componentId;
		
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()));
		
		Path p = Paths.get(AppProperties.getTmpPath() + "component_" + componentId + "row_" + rowNumber);
		Files.deleteIfExists(p);
		Files.createFile(p);
		os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE));
	}
	
	public void write(int[][] du) throws IOException {
		for(int[] row: du)
			for(int column: row) 
				os.write(column);	
	}
	
	@Override
	public void close() throws IOException {
		os.close();
	}
	
}
