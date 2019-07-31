package decoder;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;
import util.BufferedReader;

public class DCDecodeProcedureTest {

    private DCDecodeProcedure dcDp = new DCDecodeProcedure();
    
    /**
     * Decode procedure test
     * 
     * Preloaded data structures:
     * maxCode = [-1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1]
     * minCode = [0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0]
     * valPtr  = [0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0]
     * huffVal = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
     * 
     * Input:  encoded byte 0xf2 (5 most significant bits)
     * Output: decoded byte 7 (8 bits)
     * 
     * @throws IOException
     */
    @Test
    public void decodeTest() throws IOException {
        int[] maxCode = new int[] { -1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1 };
        int[] minCode = new int[] { 0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0 };
        int[] valPtr = new int[] { 0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0 };
        int[] huffVal = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        DecodeTables dcDt = new DecodeTables(maxCode, minCode, valPtr);
        
        InputStream is = Mockito.mock(InputStream.class);
        //return single byte f2, where only 5 most significant bits(11110) would be used
        //to decode DC coefficient. Because in java byte is signed needs two complement to represent it.
        //Hence 0xf2(11110010) would be represented as -14
        when(is.read(any(byte[].class), any(Integer.class), any(Integer.class))).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ((byte[])args[0])[0] = -14;
                return 1;
            }
        });
        
        NextBitReader nbr = new NextBitReader(new BufferedReader(is)); 

        int dc = dcDp.decode(dcDt, huffVal, nbr)[0];
        assertEquals(7, dc);
    }
    
}
