package decoder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import markers.HuffmanTableSpecification;

public class HuffmanTableSpecificationDecodeProcedureTest {

	private HuffmanTableSpecificationDecoderProcedure htsdp = new HuffmanTableSpecificationDecoderProcedure();
	
    @Test
    public void test() {
        //Huffman table specification marker
        int[] header = {0xff, 0xc4, 0x00, 0x1f, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                        0x0a, 0x0b};
        header = Arrays.copyOfRange(header, 2, header.length);
        
        List<HuffmanTableSpecification> htsList = htsdp.decode(header);
        
        HuffmanTableSpecification hts = htsList.get(0);
        
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
    }
    
    private void assertOrder(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) assertEquals(expected[i], actual[i]);
        
    }
    
}
