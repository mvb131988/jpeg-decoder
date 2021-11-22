package processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JpegProcessDecision {
  
  private Path inputRoot;
  
  private Path outputRoot;
  
  public JpegProcessDecision(Path inputRoot, Path outputRoot) {
    this.inputRoot = inputRoot;
    this.outputRoot = outputRoot;
  }
  
  /**
   * Takes a decision if the file defined by the input path is 
   * eligible for jpeg processing (conversion to bmp). Returns context with 
   * decision result. If decision is positive, context includes info about 
   * bmp output file.
   * 
   * @param file - file full path
   * @return
   * @throws IOException 
   */
  public JpegProcessDecisionContext decide(Path file) throws IOException {
    String fileName = file.getFileName().toString();
    String extension = null; 
    
    // no extension
    if(fileName.lastIndexOf(".") == -1) {
      return new JpegProcessDecisionContext(false, false, null, null);
    }
    
    // not jpeg file
    extension = fileName.substring(fileName.lastIndexOf(".")+1);
    if(!extension.toLowerCase().equals("jpg")) {
      return new JpegProcessDecisionContext(false, false, null, null);
    }
      
    Path bmpName = bmpName(file, inputRoot);
    Path bmpPath = bmpPath(bmpName, outputRoot);
    
    // min img already exist and its size greater than zero
    // if min img size is zero (file system recovery after disruptive event
    // might erase some of min img, that leads to size zero) regenerate min img
    if(Files.exists(bmpPath) && Files.size(bmpPath) > 0) {
      return new JpegProcessDecisionContext(false, true, bmpName, bmpPath);
    }
    
    return new JpegProcessDecisionContext(true, true, bmpName, bmpPath);
  }
  
  private Path bmpName(Path jpegFileName, Path inputRoot) {
    //jpg file relative path including file name
    Path rpJpeg = inputRoot.relativize(jpegFileName);
    
    //file extension substitution
    String srp0 = rpJpeg.toString();
    srp0 = srp0.substring(0, srp0.lastIndexOf(".")).concat(".bmp");
    //bmp file relative path including file name
    Path rpBmp = Paths.get(srp0);
    
    return rpBmp;
  }    
  
  /**
   * 
   * @param bmpFileRelativePath - part of the bmp file path relative to the output root
   * @return
   */
  private Path bmpPath(Path bmpFileRelativePath, Path outputRoot) {
    return Paths.get(outputRoot.toString(), bmpFileRelativePath.toString());
  }
  
  public static class JpegProcessDecisionContext {
    public boolean isProcessable;
    public boolean isJpeg;
    public Path bmpName;
    public Path bmpPath;

    public JpegProcessDecisionContext(boolean isProcessable, 
                                      boolean isJpeg, 
                                      Path bmpName, 
                                      Path bmpPath) 
    {
      this.isProcessable = isProcessable;
      this.isJpeg = isJpeg;
      this.bmpName = bmpName;
      this.bmpPath = bmpPath;
    }
  }
      
}
