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
 * Writes one single row(row of samples) to the output file. Each invocation of the write method
 * makes an append of 1 sample to the already existed part of the row of samples.
 * 
 * Used only for component extension 
 */
public class FileSystemExtendedRowWriter implements AutoCloseable {

	private OutputStream os;
	
	//component serial number(value from {0,1,2})
	private int componentId;
		
	//serial number of a row(row of samples or line of samples) during extension (starts with 0)
	//total number of rows is defined by the ratio maxVs/Vs[i](actually it's equal to maxVs/Vs[i])
	private int rowNumber;
	
	public FileSystemExtendedRowWriter(int componentId, int rowNumber) throws IOException {
		this.componentId = componentId;
		this.rowNumber = rowNumber;
		
		Path p = Paths.get(AppProperties.getTmpPath() + 
						   "ext_component_" + componentId + 
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
