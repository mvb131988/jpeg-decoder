package util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This class is intended to store mcu[i1][i2][i3][i4], where i1 - mcu serial number,
 * i2 - du serial number(into i1 mcu), i3 - row serial number(into i2 du),
 * i4 - column serial number(into i3 row)  
 */
public class FileSystemMCUWriter implements AutoCloseable {

	private OutputStream os;
	
	public FileSystemMCUWriter() throws IOException {
		Files.createDirectories(Paths.get("tmp"));
		
		//TODO: external property
		Path p = Paths.get("C:\\endava\\workspace\\jpeg-decoder\\tmp\\mcus");
		Files.deleteIfExists(p);
		Files.createFile(p);
		os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE));
	}
	
	/**
	 * mcu[i1][i2][i3], i1 - serial number of du, i2 - serial number of a row,
	 * i3 - serial number of a column 
	 * @throws IOException 
	 */
	public void write(int[][][] mcu) throws IOException {
		for(int[][] du: mcu) 
			for(int[] row: du)
				for(int column: row) 
					os.write(column);
	}
	
	@Override
	public void close() throws IOException {
		os.close();
	}
	
}
