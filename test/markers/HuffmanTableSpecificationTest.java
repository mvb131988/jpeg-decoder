package markers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class HuffmanTableSpecificationTest {

    @Test
    public void test() {
        //Huffman table specification marker
        int[] header = {0xff, 0xc4, 0x00, 0x1f, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                        0x0a, 0x0b};
        header = Arrays.copyOfRange(header, 2, header.length);
        
        HuffmanTableSpecification hts = new HuffmanTableSpecification(header);
        
        assertEquals(31, hts.lh);
        assertEquals(0, hts.tc);
        assertEquals(0, hts.th);
        
        //Number of Huffman codes of length 1
        assertEquals(0, hts.lis[0]);
        //Number of Huffman codes of length 2
        assertEquals(1, hts.lis[1]);
        //Number of Huffman codes of length 3
        assertEquals(5, hts.lis[2]);
        //Number of Huffman codes of length 4
        assertEquals(1, hts.lis[3]);
        //Number of Huffman codes of length 5
        assertEquals(1, hts.lis[4]);
        //Number of Huffman codes of length 6
        assertEquals(1, hts.lis[5]);
        //Number of Huffman codes of length 7
        assertEquals(1, hts.lis[6]);
        //Number of Huffman codes of length 8
        assertEquals(1, hts.lis[7]);
        //Number of Huffman codes of length 9
        assertEquals(1, hts.lis[8]);
        //Number of Huffman codes of length 10
        assertEquals(0, hts.lis[9]);
        //Number of Huffman codes of length 11
        assertEquals(0, hts.lis[10]);
        //Number of Huffman codes of length 12
        assertEquals(0, hts.lis[11]);
        //Number of Huffman codes of length 13
        assertEquals(0, hts.lis[12]);
        //Number of Huffman codes of length 14
        assertEquals(0, hts.lis[13]);
        //Number of Huffman codes of length 15
        assertEquals(0, hts.lis[14]);
        //Number of Huffman codes of length 16
        assertEquals(0, hts.lis[15]);
        
        assertOrder(new int[] {0,1,2,3,4,5,6,7,8,9,10,11}, hts.vij);
        
        //TODO: asserts for huffcode huffsize generation
//        //Values associated with Huffman codes of length 1
//        assertEquals(null, hts.vij.get(1));
//        
//        //Values associated with Huffman codes of length 2
//        assertOrder(new int[] {0}, hts.vij.get(2));
//        //Values associated with Huffman codes of length 3
//        assertOrder(new int[] {1,2,3,4,5}, hts.vij.get(3));
//        //Values associated with Huffman codes of length 4
//        assertOrder(new int[] {6}, hts.vij.get(4));
//        //Values associated with Huffman codes of length 5
//        assertOrder(new int[] {7}, hts.vij.get(5));
//        //Values associated with Huffman codes of length 6
//        assertOrder(new int[] {8}, hts.vij.get(6));
//        //Values associated with Huffman codes of length 7
//        assertOrder(new int[] {9}, hts.vij.get(7));
//        //Values associated with Huffman codes of length 8
//        assertOrder(new int[] {10}, hts.vij.get(8));
//        //Values associated with Huffman codes of length 9
//        assertOrder(new int[] {11}, hts.vij.get(9));
//        //Values associated with Huffman codes of length 10
//        assertEquals(null, hts.vij.get(10));
//        //Values associated with Huffman codes of length 11
//        assertEquals(null, hts.vij.get(11));
//        //Values associated with Huffman codes of length 12
//        assertEquals(null, hts.vij.get(12));
//        //Values associated with Huffman codes of length 13
//        assertEquals(null, hts.vij.get(13));
//        //Values associated with Huffman codes of length 14
//        assertEquals(null, hts.vij.get(14));
//        //Values associated with Huffman codes of length 15
//        assertEquals(null, hts.vij.get(15));
//        //Values associated with Huffman codes of length 16
//        assertEquals(null, hts.vij.get(16));
    }
    
    private void assertOrder(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) assertEquals(expected[i], actual[i]);
        
    }
    
}
