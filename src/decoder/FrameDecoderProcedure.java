package decoder;

import java.io.IOException;

import main.FrameHeader;
import util.BufferedReader;

public class FrameDecoderProcedure {
    
    private ScanDecoderProcedure sdp = new ScanDecoderProcedure();
    
    /**
     * 
     * @param br - is set to the first frame header bit(frame header marker already processed) 
     * @throws IOException 
     */
    public void decodeFrame(BufferedReader br) throws IOException {
        int[] fs = new int[2]; fs[0] = br.next(); fs[1] = br.next();
        int frameSize = (fs[0] << 8) + fs[1];
        
        //2 bytes of frame header marker are not counted in frameSize, but 2 bytes of frame header size are
        int[] frameHeader = new int[frameSize];
        frameHeader[0] = fs[0];
        frameHeader[1] = fs[1];        
        for(int i=2; i<frameSize; i++) frameHeader[i] = br.next();
        
        FrameHeader fh = new FrameHeader(frameHeader);
        
        //lookup for start of scan SOS. Expected only image that consists of one scan. 
        //TODO - check number of scans for baseline DCT
        int[] marker = new int[2];  marker[0] = br.next(); marker[1] = br.next();
        while(!(marker[0] == 0xff && marker[1] == 0xda) || endOfFile(marker[0], marker[1])) {
            marker[0] = marker[1]; marker[1] = br.next();
            //TODO: interpret markers
        }

        sdp.decodeScan(br);
        
        //TODO: check for EOI(end of image marker)
    }
    
    //TODO: compare with Integer.MIN_VALUE, see BufferedReader for more details
    private boolean endOfFile(int m0, int m1) {
        return false;
    }
    
}
