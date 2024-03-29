package decoder;

import markers.ScanHeader;
import util.BufferedReader;

public class ScanDecoderProcedure {

    private RestartIntervalDecoderProcedure ridp = new RestartIntervalDecoderProcedure();
    
    public void decodeScan(BufferedReader br, DecoderContext dc) throws Exception {
        int[] ss = new int[2]; ss[0] = br.next(); ss[1] = br.next();
        int scanSize = (ss[0] << 8) + ss[1];
        
        //2 bytes of frame header marker are not counted in frameSize, but 2 bytes of frame header size are
        int[] scanHeader = new int[scanSize];
        scanHeader[0] = ss[0];
        scanHeader[1] = ss[1];        
        for(int i=2; i<scanSize; i++) scanHeader[i] = br.next();
        
        ScanHeader sh = new ScanHeader(scanHeader);
        dc.scanHeader = sh;
        
        ridp.decodeRestartInterval(br, dc);
    }

    public RestartIntervalDecoderProcedure getRidp() {
        return ridp;
    }

    public void setRidp(RestartIntervalDecoderProcedure ridp) {
        this.ridp = ridp;
    }
    
}
