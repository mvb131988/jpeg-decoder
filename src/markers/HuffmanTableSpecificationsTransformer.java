package markers;

import java.util.ArrayList;
import java.util.List;

import decoder.DecodePreProcedureContext;
import decoder.DecodeProcedureContext;

public class HuffmanTableSpecificationsTransformer {

//    private List<Integer> huffmanCodeSizesTable;
    
    public DecodeProcedureContext transform(HuffmanTableSpecification hts) {
        //number of codes of each size
        int[] bits = hts.getLis();
        
        int[] huffVal = hts.vij;
        List<Integer> huffSize = huffSize(bits);
        List<Integer> huffCode = huffCode(huffSize);
        
        //TODO: pass huffVal
        DecodeProcedureContext dpc = new DecodeProcedureContext(null, huffSize, huffCode, bits);
        
//        int lastK = huffSize.size();
        //////////////////////////////////////////////////////////
        
        //////////////////////////////////////////////////////////
        // USED ONLY FOR ENCODER!!!
//        int i=0;
//        int[] eHufCo = new int[256];
//        int[] eHufSi = new int[256];
//        for(int i0=0; i0<256; i0++) {eHufCo[i0] = -1; eHufSi[i0] = -1;}
//        
//        int k = 0;
//        while (k < lastK-1) {
//            i = huffVal.get(k);
//            eHufCo[i] = huffCode.get(k);
//            eHufSi[i] = huffSize.get(k);
//            k++;
//        }
        
        //TODO: probably eHufCo, eHufSi sizes need to be reduced by throwing away -1 elements
        //This way their size would be equal to huffVal, huffCode and huffSize values
        
        //System.out.println(huffCode);
        
        return dpc;
    }
    
    /**
     * Values associated with huffman codes. Could be interpreted only together with bits structure.
     * Example:
     * bits(from header):    [0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
     * huffVal(from header): [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
     * 
     * bits[0] = 0 means there is no codes of length 0
     * bits[1] = 1 means there is one huffVal of length 2 and its value huffVal[0] = 0
     * bits[2] = 5 means there five huffVal of length 5 and their values are 
     *             huffVal[1]=1, huffVal[2]=3, huffVal[3]=3, huffVal[4]=4, huffVal[5]=5
     * Same principle till the end
     * 
     * @param hts
     * @return
     */
//    private List<Integer> huffVal(HuffmanTableSpecification hts) {
//        //symbol values to be associated with codes from bits
//        //code length -> number of symbol values
//        Map<Integer, int[]> huffValMap = hts.getVij();
//        
//        //TODO: no need to keep it in a map, just leave it as a list
//        List<Integer> huffVal = new ArrayList<>();
//        for(int i=1; i<=16; i++)
//            if(huffValMap.containsKey(i))
//                for(int hv: huffValMap.get(i)) huffVal.add(hv);
//        
//        return huffVal;
//    }
    
    public List<Integer> huffSize(int[] bits) {
        //////////////////////////////////////////////////////////
        // huffSize list generation
        // Example = [2, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9, 0]
        // Ordered ascending list with 0 termination element.
        // Element 2 means that current code length is equal to 2,
        // than go 5 sequential code of length of 3, than one code length of 4
        // and so on and so forth till the end.
        List<Integer> huffSize = new ArrayList<>();

        int k = 0;
        int i = 1;
        int j = 1;

        while (i <= 16) {
            if (j > bits[i - 1]) {
                i++;
                j = 1;
            } else {
                huffSize.add(k, i);
                k++;
                j++;
            }
        }

        huffSize.add(k, 0);
        
        return huffSize;
    }
    
    public List<Integer> huffCode(List<Integer> huffSize) {
        //////////////////////////////////////////////////////////
        //huffCode list generation
        //each code huffCode[i] has length of huffSize[i] bits
        // Exists mapping:
        // length in bits(huffSize) of the codes(huffCode)
        // [2, 3, 3, 3, 3, 3, 4,  5,  6,  7,   8,   9,   0]
        //  |  |  |  |  |  |  |   |   |   |    |    |    
        // [0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510]
        List<Integer> huffCode = new ArrayList<>();
        
        int k = 0;
        int code = 0;
        int si = huffSize.get(0);
        
        for(;;) {
            huffCode.add(k, code);
            code++;
            k++;
            
            if(huffSize.get(k) == si) continue;
            if(huffSize.get(k) == 0) break;
            
            code = code << 1;
            si++;
            
            while(huffSize.get(k) != si) {
                code = code << 1;
                si++;
            }
        }
        
        return huffCode;
    }
    
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
    public DecodeTables decodeTables(int[] bits, List<Integer> huffCode) {
        //largest code value for a given length
        int[] maxCode = new int[16];
        //smallest code value for a given length
        int[] minCode = new int[16];
        //index to the start of the list of values in huffVal, where in valPtr[i] i is word length 
        int[] valPtr = new int[16];
        
        //length of Huffman code. Starts with 1.
        int i = 0;
        
        int j = 0;
        
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
        
        return new DecodeTables(maxCode, minCode, valPtr);
    }
    
    public static class DecodeTables {
        public int[] maxCode;
        public int[] minCode;
        public int[] valPtr;
        
        public DecodeTables(int[] maxCode, int[] minCode, int[] valPtr) {
            super();
            this.maxCode = maxCode;
            this.minCode = minCode;
            this.valPtr = valPtr;
        }
        
    }
    
}
