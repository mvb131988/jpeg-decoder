package processor;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import processor.JpegProcessDecision.JpegProcessDecisionContext;

public class JpegProcessDecisionTest {
  
  /**
   *  There is a min bmp img for the input jpeg, however it's corrupted,
   *  meaning its size is 0.
   *  
   * @throws IOException
   */
  @Test
  public void test1() throws IOException {
    Path inputRoot = Path.of(System.getProperty("user.dir"), 
                             "test-resources", 
                             "jpeg-process-decision", 
                             "input");
    Path outputRoot = Path.of(System.getProperty("user.dir"), 
                              "test-resources", 
                              "jpeg-process-decision", 
                              "output"); 
    Path file = Path.of(inputRoot.toString(), "img1.jpg");
    
    JpegProcessDecision jpd = new JpegProcessDecision(inputRoot, outputRoot);
    JpegProcessDecisionContext result = jpd.decide(file);
    
    Path expectedBmpName = Path.of("img1.bmp");
    Path expectedBmpPath = Path.of(outputRoot.toString(), expectedBmpName.toString());
    assertTrue(result.isJpeg);
    assertTrue(result.isProcessable);
    assertEquals(expectedBmpName, result.bmpName);
    assertEquals(expectedBmpPath, result.bmpPath);
  }
  
  /**
   *  The required min bmp img is missing.
   *  
   * @throws IOException
   */
  @Test
  public void test2() throws IOException {
    Path inputRoot = Path.of(System.getProperty("user.dir"), 
                             "test-resources", 
                             "jpeg-process-decision", 
                             "input");
    Path outputRoot = Path.of(System.getProperty("user.dir"), 
                              "test-resources", 
                              "jpeg-process-decision", 
                              "output"); 
    Path file = Path.of(inputRoot.toString(), "img2.jpg");
    
    JpegProcessDecision jpd = new JpegProcessDecision(inputRoot, outputRoot);
    JpegProcessDecisionContext result = jpd.decide(file);
    
    Path expectedBmpName = Path.of("img2.bmp");
    Path expectedBmpPath = Path.of(outputRoot.toString(), expectedBmpName.toString());
    assertTrue(result.isJpeg);
    assertTrue(result.isProcessable);
    assertEquals(expectedBmpName, result.bmpName);
    assertEquals(expectedBmpPath, result.bmpPath);
  }
  
  /**
   *  Valid min bmp img already exists.
   *  
   * @throws IOException
   */
  @Test
  public void test3() throws IOException {
    Path inputRoot = Path.of(System.getProperty("user.dir"), 
                             "test-resources", 
                             "jpeg-process-decision", 
                             "input");
    Path outputRoot = Path.of(System.getProperty("user.dir"), 
                              "test-resources", 
                              "jpeg-process-decision", 
                              "output"); 
    Path file = Path.of(inputRoot.toString(), "img3.jpg");
    
    JpegProcessDecision jpd = new JpegProcessDecision(inputRoot, outputRoot);
    JpegProcessDecisionContext result = jpd.decide(file);
    
    Path expectedBmpName = Path.of("img3.bmp");
    Path expectedBmpPath = Path.of(outputRoot.toString(), expectedBmpName.toString());
    assertTrue(result.isJpeg);
    assertFalse(result.isProcessable);
    assertEquals(result.bmpName, expectedBmpName);
    assertEquals(result.bmpPath, expectedBmpPath);
  }
  
}
