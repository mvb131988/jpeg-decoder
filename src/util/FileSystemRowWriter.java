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
 * makes an append of 8 samples to the already existed part of the row of samples.
 */
public class FileSystemRowWriter implements AutoCloseable {

	private OutputStream os;
	
	//component serial number(value from {0,1,2})
	private int componentId;
		
	//serial number of DU row within MCU (starts with 0)
	private int rowNumber;
	
	//line number within a DU(0value from {0-7})
	private int lineNumber;
	
	//number of samples in a line(within the whole component, not just DU or MCU) including padding
	private int extSamplesCount;
	
	public FileSystemRowWriter(int componentId, int rowNumber, int lineNumber, int extSamplesCount) throws IOException {
		this.componentId = componentId;
		this.rowNumber = rowNumber;
		this.lineNumber = lineNumber;
		this.extSamplesCount = extSamplesCount;
		
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()));
		
		Path p = Paths.get(AppProperties.getTmpPath() + 
						   "component_" + componentId + 
						   "_row_" + rowNumber + 
						   "_line_" + lineNumber);
		Files.deleteIfExists(p);
		Files.createFile(p);
		os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE), 262_144);
	}
	
	/**
	 * Writes part(specific line of the DU row) of the component row(samples row).
	 * Each write invocation makes an append of part of the component row(8 samples)
	 * to the already added samples.
	 *  
	 * @param duRow
	 * @throws IOException 
	 */
	public void write(int[] duRow) throws IOException {
		for(int sample: duRow)
			os.write(sample);
	}
	
	@Override
	public void close() throws IOException {
		os.close();
	}
	
}
