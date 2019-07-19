package decoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import util.BufferedReader;

/**
 * Entry point for the decoding process
 */
public class DecoderControlProcedure {

    //source(jpeg) image reader
    private BufferedReader br;
    
    private FrameDecoderProcedure fdp = new FrameDecoderProcedure();
    
    /**
     * 
     * @param sPath - path to source(jpeg) image
     * @throws IOException 
     */
    public DecoderControlProcedure(String sPath) throws IOException {
        Path path = Paths.get(sPath);
        InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
        br = new BufferedReader(is);
    }
    
    public void decodeImage() throws Exception {
        decodeImageInternally();
        br.close();
    }
    
    private void decodeImageInternally() throws Exception {
        //read SOI(start of image marker)
        int[] soi = new int[2]; soi[0] = br.next(); soi[1] = br.next();
        if(soi[0] != 0xff && soi[1] != 0xd8) throw new Exception("SOI not found");
        
        //lookup for start of frame SOF0 - Baseline DCT. Only this format is supported
        int[] marker = new int[2];  marker[0] = br.next(); marker[1] = br.next();
        while(!(marker[0] == 0xff && marker[1] == 0xc0) || endOfFile(marker[0], marker[1])) {
            marker[0] = marker[1]; marker[1] = br.next();
            //TODO: interpret markers
        }
        
        fdp.decodeFrame(br);
    }
    
    //TODO: compare with Integer.MIN_VALUE, see BufferedReader for more details
    private boolean endOfFile(int m0, int m1) {
        return false;
    }
    
}