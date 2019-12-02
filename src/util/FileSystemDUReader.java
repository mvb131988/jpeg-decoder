package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemDUReader implements AutoCloseable {

	private InputStream is;
	
	//du serial number(value from {0,1,2})
	private int componentId;
		
	//serial number of DU row within MCU (starts with 0)
	private int rowNumber;
	
	private Path p;
	
	public FileSystemDUReader(int componentId, int rowNumber) throws IOException {
		this.componentId = componentId;
		this.rowNumber = rowNumber;
		
		p = Paths.get(AppProperties.getTmpPath() + "component_" + componentId + "_row_" + rowNumber);
		is = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ), 262_144);
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public int[][] read() throws IOException {
		int[][] du = new int[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				du[i][j] = is.read();
		return du;
	}
	
	@Override
	public void close() throws Exception {
		is.close();
		Files.deleteIfExists(p);
	}

}
