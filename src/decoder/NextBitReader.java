package decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.BufferedReader;

public class NextBitReader {

    private BufferedReader br;
    
    protected int b;
    
    protected int b2;
    
    protected int cnt;
    
    protected int untransformedByte;
    
    private static List<Integer> byteLog = new ArrayList<>();
    
    public NextBitReader(BufferedReader br) {
        this.br = br;
        this.cnt = 0;
    }
    
    /**
     * Byte sequence ff00 is discarded. 
     * 
     * @return
     * @throws IOException
     */
    public int nextBit() throws IOException {
        if(cnt == 0) {
            b = br.next();
            
            untransformedByte = b;
            byteLog.add(b);
            
            cnt = 8;
            if(b == 0xff) {
                b2 = br.next();
                
                untransformedByte = b;
                byteLog.add(b2);
                
                if(b2 != 0) {
                    //TODO: process DNL. in current example no DNL
                }
            }
        }
        
        int bit  = b >>> 7;
        cnt--;
        b = (b << 1) & 0xff;
        
        return bit; 
    }
    
}
