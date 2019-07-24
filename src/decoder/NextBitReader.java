package decoder;

import java.io.IOException;

import util.BufferedReader;

public class NextBitReader {

    private BufferedReader br;
    
    private int b;
    
    private int b2;
    
    private int cnt;
    
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
            cnt = 8;
            if(b == 0xff) {
                b2 = br.next();
                if(b2 != 0) {
                    //TODO: process DNL. in current example no DNL
                }
            }
        }
        
        int bit  = b >>> 7;
        cnt--;
        b = (b << 1) & 0xff;
        
        //System.out.print(bit);
        return bit; 
    }
    
}
