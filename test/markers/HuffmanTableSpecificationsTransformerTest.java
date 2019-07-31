package markers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class HuffmanTableSpecificationsTransformerTest {

    private HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
    
    /**
     * input:  bits = [0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
     * output: huffSize = [2, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9, 0]
     */
    @Test
    public void testHuffSize() {
        //In this example there are 0 entries for code sizes 1(bits[0] corresponds to size/length 1)
        //1 entry for size 2, 5 entries for size 3 and so on ans so forth.
        //output is an array that contains ordered(ascending) code sizes for each code size from the bits(code size is equal 
        //to index+1 of bits. Example i=0 -> code size 1 and number of entries bits[i] = 0).
        //Each code size is repeated exactly the same times as the corresponding number of entries are 
        //(Example: code size = 3, number of entries bits[2]=5. This will gives 5 consecutive values of 3 in the output)
        int[] bits = new int[] { 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
        List<Integer> huffSize = htst.huffSize(bits);
        
        // Overall number of codes(across all sizes)
        int noc = Arrays.stream(bits).reduce(0, (a,b) -> a+b);
        //last element of huffSize is 0 end of huffSize symbol
        assertEquals(noc+1, huffSize.size());
        
        int k = 0;
        for(int i=0; i<bits.length; i++) {
            if(bits[i] != 0) {
                for(int j=0; j<bits[i]; j++) {
                    assertEquals(i+1, huffSize.get(k++).intValue());
                }
            }
        }
    }
    
    /**
     * Huffman codes in the output are ordered according to increasing Huffman code length
     * There is one to one correspondence between huffSize and huffCode.
     * 
     * input:  huffSize = [2, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9, 0]
     * output: huffCode = [0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510]
     */
    @Test
    public void testHuffCode() {
        List<Integer> huffSize = Arrays.asList(2, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9, 0);
        List<Integer> huffCode = htst.huffCode(huffSize);
        
        List<Integer> expected = Arrays.asList(0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510);
        assertOrder(expected, huffCode);
    }
    
    /**
     * There are 16 values in expectedMaxCode, expectedMinCode, expectedValPtr. Value with 0 index corresponds 
     * to the Huffman code of length 1. expectedValPtr contains pointer to huffVal structure. Value 0 from 
     * the expectedValPtr corresponds to huffVal[0].
     * 
     *  input:  bits     = [0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
     *          huffCode = [0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510]
     *  output: expectedMaxCode = [-1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1]
     *          expectedMinCode = [0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0]
     *          expectedValPtr =  [0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0]
     */
    @Test
    public void testDecodeTables() {
        int[] bits = new int[] { 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
        List<Integer> huffCode = Arrays.asList(0, 2, 3, 4, 5, 6, 14, 30, 62, 126, 254, 510);
        
        HuffmanTableSpecificationsTransformer.DecodeTables dts = htst.decodeTables(bits, huffCode);
        int[] expectedMaxCode = new int[] { -1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1};
        int[] expectedMinCode = new int[] { 0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0};
        int[] expectedValPtr = new int[] { 0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0};
        
        assertOrder(expectedMaxCode, dts.maxCode);
        assertOrder(expectedMinCode, dts.minCode);
        assertOrder(expectedValPtr, dts.valPtr);
    }
    
    private void assertOrder(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) assertEquals(expected[i], actual[i]);
        
    }
    
    private void assertOrder(List<Integer> expected, List<Integer> actual) {
        assertEquals(expected.size(), actual.size());
        for(int i=0; i<expected.size(); i++) assertEquals(expected.get(i), actual.get(i));
        
    }
    
}
