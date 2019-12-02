package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemRowReader implements AutoCloseable {
	
	private InputStream is;
	
	//component serial number(value from {0,1,2})
	private int componentId;
		
	//serial number of DU row within MCU (starts with 0)
	private int rowNumber;
	
	//line number within a DU(0value from {0-7})
	private int lineNumber;
	
	//number of samples in a line(within the whole component, not just DU or MCU) excluding padding
	private int samplesCount;
	
	private Path p;

	public FileSystemRowReader(int componentId, int rowNumber, int lineNumber, int samplesCount) throws IOException {
		this.componentId = componentId;
		this.rowNumber = rowNumber;
		this.lineNumber = lineNumber;
		this.samplesCount = samplesCount;
		
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()));
		
		p = Paths.get(AppProperties.getTmpPath() + 
									"component_" + componentId + 
									"_row_" + rowNumber + 
									"_line_" + lineNumber);
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
