package decoder;

import java.io.IOException;

public class ACDecodeProcedure extends AbstractDecodeProcedure {

    public int[] decode(DecodePreProcedureContext c, NextBitReader nbr) throws IOException {
        int k=1;
        int[] zz = new int[63];
        
        while(k != 63) {
            int RS = super.decode(c, nbr)[0];
            int SSSS = RS % 16;
            int RRRR = RS >>> 4;
            int R = RRRR;
            
            if(SSSS == 0) {
                //separate flow here
                if(R != 15) break;
            }
            
            k = k + R;
            zz[k] = decodeZZk(SSSS, nbr);
        }
        
        return zz;
    }
    
    private int decodeZZk(int SSSS, NextBitReader nbr) throws IOException {
        int zzK = this.receive(SSSS, nbr);
        zzK = this.extend(zzK, SSSS);
        return zzK;
    }
    
}