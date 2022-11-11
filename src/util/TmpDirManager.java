package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TmpDirManager {

	private static Logger logger = LogManager.getRootLogger();
	
	public void init(String tmpPath) throws IOException {
		Files.createDirectories(Paths.get(tmpPath));
		Files.createDirectories(Paths.get(tmpPath).resolve("rows"));
	}
	
	public void clean(String tmpPath) {
		File[] files = new File(tmpPath).listFiles();
		for(File f: files) {
			Path current = Paths.get(tmpPath).resolve(f.getName());
			if(f.isDirectory()) 
				cleanUp(current.toString());
			try {
				if(!current.getFileName().toString().equals("rows"))
					Files.delete(current);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
	private void cleanUp(String path) {
		File[] files = new File(path).listFiles();
		for(File f: files) {
			Path current = Paths.get(path).resolve(f.getName());
			if(f.isDirectory()) cleanUp(current.toString());
			try {
				Files.delete(current);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
