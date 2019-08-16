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
    
    //------------------------------------------------------------------------------------------------------------
    // decodeTest1, receiveTest1, extendTest1 correspond to the same test case
    //------------------------------------------------------------------------------------------------------------
    
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
    public void decodeTest1() throws IOException {
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
    
    /**
     * Consider the following example:
     * 
     * encoded data: 0xf2 0xaa
     *               1111 0010 1010 1010
     * 
     * Decode procedure reads 5 first most significant bits(11110) of the 0xf2.
     * This represents number bits(7 bits) followed by actual bits, that have to be read by receive procedure.
     * 
     * Input:           (010 1010 1010)2 = 
     * (Complement to 2 bytes, will look like: 0101 0101 0100 0000)
     * Expected result: (0101010)2 = 42
     * @throws IOException 
     */
    @Test
    public void receiveTest1() throws IOException {
        InputStream is = Mockito.mock(InputStream.class);
        when(is.read(any(byte[].class), any(Integer.class), any(Integer.class))).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                //0101 0101 0100 0000
                ((byte[])args[0])[0] = 0b0101_0101;
                ((byte[])args[0])[1] = 0b0100_0000;
                return 1;
            }
        });
        
        NextBitReader nbr = new NextBitReader(new BufferedReader(is)); 
        
        int SSSS = 7;
        int dc = dcDp.receive(SSSS, nbr);
        assertEquals(42, dc);
    }
    
    /**
     * There is a logic behind the sign extension. 
     * Le't assume 7bit length value is transfered.
     *
     * Possible values are: 
     *  2       1000000..1111111 (sign bit 0 is omitted)
     * 10       64..127 
     * and 
     *  2       0000001..1000000 (sign bit 1 is omitted)
     * 10       -127..-64
     * 
     * Positive values don't require sign extension. However negative values need to be
     * distinguished from positive values with same binary representation.
     * 
     * To accomplish that negative value is replaced with the value: 127 + (negative value).
     * This would give a positive value less than 64 for any negative value from the range,
     * meaning that this new value would not collide with positive range for 7bit length values.
     * This also means that the most significant bit would be 0 for all such replacements,
     * that is used to distinguish negative replacement of 7bit negative value with 7bit
     * positive value(most significant bit of the positive 7bit value is always 1).
     * 
     * This leads to simple check. If most significant bit is 1 no modifications are required.
     * However if it's 0 recovery must be done by doing -127 + (replacement of negative value).
     */
    @Test
    public void extendTest1() {
        //Diff
        int v = 42;
        //Huffval
        int t = 7;
        v = dcDp.extend(v, t);
        assertEquals(-85, v);
    }
    
    /**
     * This test is just a consolidation of decodeTest1, receiveTest1, extendTest1
     * @throws IOException 
     */
    @Test
    public void testDecodeDc1() throws IOException {
        int[] maxCode = new int[] { -1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1 };
        int[] minCode = new int[] { 0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0 };
        int[] valPtr = new int[] { 0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0 };
        int[] huffVal = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        DecodeTables dcDt = new DecodeTables(maxCode, minCode, valPtr);
        
        InputStream is = Mockito.mock(InputStream.class);
        when(is.read(any(byte[].class), any(Integer.class), any(Integer.class))).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                //0xf2 0xaa (1111 0010 1010 1010), most significant bit is replaced by - sign 
                ((byte[])args[0])[0] = (byte)242;
                ((byte[])args[0])[1] = (byte)170;
                
                //number of bytes that have been read
                return 2;
            }
        });
        
        NextBitReader nbr = new NextBitReader(new BufferedReader(is)); 

        int dc = dcDp.decodeDc(dcDt, huffVal, nbr)[0];
        assertEquals(-85, dc);
    }
    
    //------------------------------------------------------------------------------------------------------------
    
    /**
     * For the given 
     * Diff = 66, SSSS=7(size of Diff is 7 bits), Huffcode = 126(calculated, corresponds to length 7), 
     * HuffVal = 7(from input Huffman tables), 
     *                         
     * In encoded sequence most significant 8 bits contain Huffcode + least significant 7 bits of Diff.
     * Decode procedure reads Huffcode(111110) and determines corresponding Huffval of Diff(126 corresponds 
     * to length of 7). Huffval defines size of Diff(7 bits). Receive reads next 7 bits(Diff itself).
     * 
     * Positive Diff are encoded like they are(66 = (1000010)2).
     * Negative Diff are encoded the following way: 
     * (1)Diff = Diff - 1
     * (2)Find Diff twos complement
     * (3)Write (2) value without sign bit in output
     * 
     *                   Huffcode Diff(DC)                  
     * Encoded sequence: 111110   1000010
     * 
     */
    @Test
    public void extendPositiveTest() {
        //Diff
        int v = 66;
        //Huffval
        int t = 7;
        v = dcDp.extend(v, t);
        assertEquals(66, v);
    }
    
    /**
     * See description from extendPositiveTest
     */
    @Test
    public void extendNegativeTest() {
        //Diff -66 is encoded like 61
        int v = 61;
        //Huffval
        int t = 7;
        v = dcDp.extend(v, t);
        assertEquals(-66, v);
    }
    
}
