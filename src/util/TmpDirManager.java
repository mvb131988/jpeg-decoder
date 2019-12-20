package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import main.AppProperties;

public class TmpDirManager {

	public void init() throws IOException {
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()));
		Files.createDirectories(Paths.get(AppProperties.getTmpPath()).resolve("rows"));
	}
	
	public void clean() {
		for(File file: new File(Paths.get(AppProperties.getTmpPath()).toString()).listFiles()) 
		    if (!file.isDirectory()) 
		        file.delete();
		
		for(File file: new File(Paths.get(AppProperties.getTmpPath())
									 .resolve("rows").toString())
								 	 .listFiles()
		   ) 
		{ 
		    if (!file.isDirectory()) 
		        file.delete();
		}
	}
	
}
