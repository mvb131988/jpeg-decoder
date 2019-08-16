package decoder;

import java.io.IOException;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;

public abstract class AbstractDecodeProcedure {
    
    /**
     * Returns number of bits that have to be read next. 
     * 
     * For DC coefficient:
     * These bits represent DC coefficient(more precisely DIFF value without sign extension and 
     * addition of PRED DC value) 
     * 
     * @param dt
     * @param huffVal
     * @param nbr
     * @return
     * @throws IOException
     */
    public int[] decode(DecodeTables dt, int[] huffVal, NextBitReader nbr) throws IOException {
        //Aliases
        ///////////////////////////////////////////////////////////////////////////////////////////
        int[] maxCode = dt.maxCode;
        int[] valPtr = dt.valPtr;
        int[] minCode = dt.minCode;
        ///////////////////////////////////////////////////////////////////////////////////////////
        
        //length of Huffman code. Note maxCode, valPtr, minCode are 0 based arrays, meaning that 
        //value related to length 1 is located at index 0.
        //i is length of Huffman code
        int i = 1;
        int code = nbr.nextBit();
        
        while(code > maxCode[i-1]) {
            i++;
            code = (code << 1) + nbr.nextBit();
        }
        
        int j = valPtr[i-1];
        j = j + code;
        j -= minCode[i-1];
        int t = huffVal[j];
        
        int[] tArr = new int[] {t};
        return tArr;
    }
    
    /**
     * Reads SSSS bits from the input stream. Currently read bit the is the least significant.
     *
     * For DC coefficient:
     * These bits represent DC coefficient(more precisely DIFF value without sign extension and 
     * addition of PRED DC value)  
     * 
     * @param SSSS
     * @param nbr
     * @return
     * @throws IOException
     */
    public int receive(int SSSS, NextBitReader nbr) throws IOException {
        int i = 0;
        int v = 0;
        
        while(i != SSSS) {
            i = i + 1;
            v = (v << 1) + nbr.nextBit();
        }
        
        return v;
    }
    
    //coefficient size is not greater than (t+1) bits
    public int extend(int v, int t) {
        int vt = (int) Math.pow(2, t - 1);
        if (v < vt) {
            vt = ((-1 << t) + 1);
            v += vt;
        }
        return v;
    }
    
}
