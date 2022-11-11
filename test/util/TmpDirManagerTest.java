package util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class TmpDirManagerTest {

	@Before
	public void setUp() throws IOException {
		Path root = Paths.get("").resolve("test-dir").toAbsolutePath();

		Files.createDirectories(root);
		
		cleanUp(root.toString());
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
	
	/**
	 * Creates files structure like:
	 * test-dir -> .tmp -> rows -> file3
	 * 							-> file4
	 * 					-> file1
	 * 					-> dir1 -> file2
	 * 
	 * Assert that at the end remains: 
	 * test-dir -> .tmp -> rows
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		Path tmpPath = Paths.get("").resolve("test-dir")
									.resolve(".tmp")
									.toAbsolutePath();
		TmpDirManager tdm = new TmpDirManager();
		tdm.init(tmpPath.toString());
		
		Path file1Path = tmpPath.resolve("file1.txt");
	    createFile(file1Path);
	    Path file2Path = tmpPath.resolve("dir1").resolve("file1.txt");
	    createFile(file2Path);
	    Path file3Path = tmpPath.resolve("rows").resolve("file3.txt");
	    createFile(file3Path);
	    Path file4Path = tmpPath.resolve("rows").resolve("file4.txt");
	    createFile(file4Path);
	    
	    tdm.clean(tmpPath.toString());
	    
	    assertTrue(Files.exists(tmpPath.resolve("rows")));
	    assertFalse(Files.exists(file1Path));
	    assertFalse(Files.exists(file2Path.getParent()));
	    assertFalse(Files.exists(file2Path));
	    assertFalse(Files.exists(file3Path));
	    assertFalse(Files.exists(file4Path));
	}
	
	private void createFile(Path path) {
		 try {
			 //create missing dirs
			 File file = new File(path.getParent().toString());
			 file.mkdir();
			 
	         file = new File(path.toString());
	         file.createNewFile();
	      } catch(Exception e) {
	    	  System.out.println(e);
	      }
	}
	
}
