package decoder;

import java.io.IOException;
import java.util.List;

public abstract class AbstractDecodeProcedure {

    public int[] decode(DecodePreProcedureContext c, NextBitReader nbr) throws IOException {
        //Aliases
        ///////////////////////////////////////////////////////////////////////////////////////////
        int[] maxCode = c.maxCode;
        int[] valPtr = c.valPtr;
        int[] minCode = c.minCode;
        List<Integer> huffVal = c.huffVal;
        ///////////////////////////////////////////////////////////////////////////////////////////
        
        int i = 1;
        int code = nbr.nextBit();
        
        while(code > maxCode[i]) {
            i++;
            code = (code << 1) + nbr.nextBit();
        }
        
        int j = valPtr[i];
        j = j + code - minCode[i];
        int t = huffVal.get(j);
        
        int[] tArr = new int[] {t};
        return tArr;
    }
    
    public int receive(int SSSS, NextBitReader nbr) throws IOException {
        int i = 0;
        int v = 0;
        
        while(i != SSSS) {
            i = i + 1;
            v = (v << 1) + nbr.nextBit();
        }
        
        return v;
    }
    
    public int extend(int v, int t) {
        int vt = (int) Math.pow(2, t-1);
        while(v<vt) {
            vt = (-1 << t) + 1;
            v += vt; 
        }
        return v;
    }
    
}
