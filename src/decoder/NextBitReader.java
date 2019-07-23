package decoder;

import java.io.IOException;

import main.ImageReader;
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
    
    public int nextBit() throws IOException {
        if(cnt == 0) {
            b = br.next();
            cnt = 8;
            if(b == 0xff) {
                //TODO: implement couple of corner cases
            }
        }
        
        int bit  = b >>> 7;
        cnt--;
        b = (b << 1) & 0xff;
        
        //System.out.print(bit);
        return bit; 
    }
    
}
