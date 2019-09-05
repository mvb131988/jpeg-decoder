package decoder;

import java.io.IOException;

import markers.RestartInterval;
import util.BufferedReader;

/**
 * Detects:
 * - application specific marker and skips it
 * - restart interval marker and decodes it   
 */
public class HeadersDecoder {

    public boolean isAppMarker(int[] marker) {
        boolean res = marker[0] == 0xff && (marker[1] == 0xe0 || 
                                            marker[1] == 0xe1 ||
                                            marker[1] == 0xe2 ||
                                            marker[1] == 0xe3 || 
                                            marker[1] == 0xe4 ||
                                            marker[1] == 0xe5 ||
                                            marker[1] == 0xe6 || 
                                            marker[1] == 0xe7 ||
                                            marker[1] == 0xe8 ||
                                            marker[1] == 0xe9 || 
                                            marker[1] == 0xea ||
                                            marker[1] == 0xeb ||
                                            marker[1] == 0xec || 
                                            marker[1] == 0xed ||
                                            marker[1] == 0xee ||
                                            marker[1] == 0xef);
        return res;
    }
    
    /**
     * Moves buffered reader pointer to the position followed by the end of app marker
     * @throws IOException 
     */
    public void skipAppMarker(BufferedReader br) throws IOException {
        int[] appSize0 = new int[2]; appSize0[0] = br.next(); appSize0[1] = br.next();
        int appSize = (appSize0[0] << 8) + appSize0[1];
        
        //2 bytes of appSize
        appSize -= 2;
        
        //skip app marker
        for(int i=0; i<appSize; i++) br.next();
    }
    
    public boolean isRestartIntervalMarker(int[] marker) {
        boolean res = marker[0] == 0xff && marker[1] == 0xdd;
        return res;
    }
    
    public RestartInterval restartInterval(BufferedReader br) throws IOException {
        int[] riHeader = new int[4];
        for(int i=0; i<4; i++) riHeader[i] = br.next();
        return new RestartInterval(riHeader);
    }
    
}
