package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import main.AppProperties;

public class FileSystemMCUReader implements AutoCloseable {
	
	//number of DUs in a single MCU
	private int duN;
	
	private InputStream is;
	
	private Path p;
	
	public FileSystemMCUReader(int duN) throws IOException {
		this.duN = duN; 
		
		p = Paths.get(AppProperties.getTmpPath() + "mcus");
		is = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ), 262_144);
	}
	
	/**
	 * 
	 * @return fully populated MCU or null if during read EOF occurs
	 * @throws IOException 
	 */
	public int[][][] read() throws IOException {
		int[][][] mcu = new int[duN][8][8];
		for(int i=0; i<mcu.length; i++)
			for(int j=0; j<8; j++)
				for(int k=0; k<8; k++) {
					mcu[i][j][k] = is.read();
					if(mcu[i][j][k] == -1) return null;
				}
		return mcu;
	}
	
	@Override
	public void close() throws IOException {
		is.close();
		Files.deleteIfExists(p);
	}
	
}
