package decoder;

import java.util.List;

public class DecodePreProcedure {

    //TODO: write test to cover the situation
    /**
     * Generates MINCODE, MAXCODE and VALPTR
     * 
     * Consider example: 
     * bits:     [0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
     * huffVal:  [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
     * huffCode: [0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510]
     * 
     * minCode: [_, 0, 2, 14, 30, 62, 126, 254, 510, _, _, _, _, _, _, _]
     * maxCode: [_, 0, 6, 14, 30, 62, 126, 254, 510, _, _, _, _, _, _, _]
     * valPtr:  [_, 0, 1, 6, 7, 8, 9, 10, 11, _, _, _, _, _, _, _]
     */
    public DecodePreProcedureContext decode(DecodeProcedureContext dpc) {
        //bits[0] contains number of values of length 1
        int[] bits = dpc.bits;
        List<Integer> huffCode = dpc.huffCode;
        
        //largest code value for a given length
        int[] maxCode = new int[16];
        //smallest code value for a given length
        int[] minCode = new int[16];
        //index to the start of the list of values in huffVal, where in valPtr[i] i is word length 
        int[] valPtr = new int[16];
        
        int i = 0, j = 0;
        
        for(;;) {
            i++;
            
            if(i>16) break;
            
            if(bits[i-1] == 0) {
                maxCode[i-1]--;
            } else {
                valPtr[i-1] = j;
                minCode[i-1] = huffCode.get(j);
                j = j + bits[i-1] - 1;
                maxCode[i-1] = huffCode.get(j);
                j = j + 1;
            }
        }
        
//        System.out.println(minCode);
        
        return new DecodePreProcedureContext(minCode, maxCode, valPtr, dpc.huffVal);
    }
    
}