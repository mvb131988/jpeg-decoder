package util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import bmp.BmpFileHeader;
import bmp.BmpFileHeaderRaw;
import bmp.Pixel;
import main.AppProperties;

public class FileSystemBmpWriter implements AutoCloseable {

	private OutputStream os;

	/**
	 * 
	 * @param fileName - file name with extension without path
	 * @throws IOException
	 */
	public FileSystemBmpWriter(String fileName) throws IOException {
		Path p = Paths.get(AppProperties.getOutputPath() + fileName);
		Files.deleteIfExists(p);
		Files.createDirectories(p.getParent());
		Files.createFile(p);
		
		os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.WRITE), 262_144);
	}

	public void writeHeader(BmpFileHeader h) throws IOException {
		os.write(new BmpFileHeaderRaw(h).toByteArray());
	}

	public void writePixel(Pixel p) throws IOException {
		os.write(p.b);
		os.write(p.g);
		os.write(p.r);
	}

	@Override
	public void close() throws IOException {
		os.close();
	}
	
}
