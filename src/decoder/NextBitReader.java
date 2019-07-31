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
    
    //saves each bit that went in the output
    private static List<Integer> log = new ArrayList<Integer>();
    
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
        
        log.add(bit);
        //System.out.print(bit);
        return bit; 
    }
    
    public void outputLog() {
        System.out.println("===== Bit reader log =====");
        log.stream().forEach(e->System.out.print(e));
        System.out.println();
        byteLog.stream().forEach(e->System.out.println(Integer.toHexString(e)));
        System.out.println("===== Bit reader log end =====");
    }
    
}
