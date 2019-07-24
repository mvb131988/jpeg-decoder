package markers;

import java.util.HashMap;
import java.util.Map;

public class HuffmanTableSpecification {

    private int[] header;
    
    //Huffman table definition length
    //Most significant byte first
    public int lh;
    
    // Table class – 0 = DC table or lossless table, 1 = AC table
    // Most significant 4 bits of the byte
    public int tc;
    
    //Huffman table destination identifier
    // Least significant 4 bits of the byte
    public int th;
    
    //Number of Huffman codes of length i 
    public int[] lis = new int[16];
    
    //Value associated with each Huffman code
    //
    //How to calculate
    //Consider an example:
    //
    //lis = [0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
    //header = [255, 196, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
    //
    //At this point pos = 21 (look at header[pos])
    //lis[0] = 0 corresponds to  number of Huffman code of length 1 and is equal to 0, hence no values are associated
    //lis[1] = 1 corresponds to  number of Huffman code of length 2 and is equal to 1, hence one value is associated
    //and it's header[21] = 0
    //lis[2] = 5 corresponds to  number of Huffman code of length 3 and is equal to 5, hence 5 values are associated
    //and they are header[22] = 1, header[23] = 2, header[24] = 3, header[25] = 4, header[26] = 5
    //Same procedure is applied till the end. Sum of lis values is equal to 'header.length-pos' (where pos just after lis 
    //is initialized)
    public Map<Integer, int[]> vij = new HashMap<>();
    
    public HuffmanTableSpecification(int[] header) {
        this.header = header;
        
        int pos = 0;
        lh = (header[pos] << 8) + header[pos+1];
        pos+=2;
        
        tc = (header[pos] & 0x0F0)>>4;
        th = header[pos] & 0x0F;
        pos++;
        
        for(int i0=0, i=pos; i<pos+16; i++, i0++) {
            lis[i0] = header[i];
        }
        pos += 16;
        
        for(int i=0; i<16; i++) {
            if(lis[i] != 0) {
                vij.put(i+1, new int[lis[i]]);
                for(int j=0; j<lis[i]; j++) vij.get(i+1)[j] = header[pos++];
            }
        }
        
//        System.out.println(vij);
    }
    
    public static HuffmanTableSpecification checkAndBuild(int[] header) {
        if(header[1] == 0xc4) return new HuffmanTableSpecification(header);
        return null;
    }

    public int[] getLis() {
        return lis;
    }

    public Map<Integer, int[]> getVij() {
        return vij;
    }
    
}
