package decoder;

import java.io.IOException;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;

public class ACDecodeProcedure extends AbstractDecodeProcedure {

    /**
     * 
     * Gets back an array of 64 elements(elements 1..63 are AC coefficients, element 0 is unfilled.
     * This is the position for DC coefficient)
     * 
     * @param acDt
     * @param huffVal
     * @param nbr
     * @return
     * @throws IOException
     */
    public int[] decodeAc(DecodeTables acDt, int[] huffVal, NextBitReader nbr, MCUCalculationDataHolder holder) throws IOException {
        int k=1;
        int[] zz = holder.zz;
        
        for(;;) {
            int RS = decode(acDt, huffVal, nbr, holder)[0];
            int SSSS = RS % 16;
            int RRRR = RS >>> 4;
            int R = RRRR;
            
            if(SSSS == 0) {
                if(R != 15) break;
                k = k + 16;
                continue;
            }
            
            k = k + R;
            zz[k] = decodeZZk(SSSS, nbr);
            
            if(k == 63) break;

            k++;
        }
        
        return zz;
    }
    
    private int decodeZZk(int SSSS, NextBitReader nbr) throws IOException {
        int zzK = receive(SSSS, nbr);
        zzK = extend(zzK, SSSS);
        return zzK;
    }
    
}