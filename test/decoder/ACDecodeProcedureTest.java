package decoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;
import util.BufferedReader;

public class ACDecodeProcedureTest {

    private DCDecodeProcedure dcDp = new DCDecodeProcedure();
    
    private ACDecodeProcedure acDp = new ACDecodeProcedure();
    
    /**
     * This is a 4's block(data unit) of the first MCU in fx_rate_pair.jpg
     * DC, AC tables are also taken from fx_rate_pair.jpg, hence this is absolutely
     * valid example of data unit decoding.
     * 
     * In the 3'd hex row last two bytes(12 be) are from the next data unit. 
     * ------------------------------------------------------------------------------
     * 
     * hex    f9 fb fe 0a 1d fb 77 69 9f b0 cf c2 8b 7b f6 b5
     *        fe d0 f1 2f 88 5a 4b 6d 0e d2 44 7f b3 99 10 2e 
     *        f9 66 65 1c 47 1e f5 25 47 cc d9 00 77 65 12 be
     * ------------------------------------------------------------------------------
     * binary 11111001 11111011 11111110 00001010 00011101 11111011 01110111 01101001
     *        |DC            |AC[1]                      |AC[2]               |AC[3]       
     *        -----------------------------------------------------------------------     
     *        10011111 10110000 11001111 11000010 10001011 01111011 11110110 10110101
     *      AC[3]|AC[4]              |AC[5]           |AC[6]   |AC[7]   |8   |AC[9]           8 - alias for AC[8]
     *        ----------------------------------------------------------------------- 
     *        -----------------------------------------------------------------------
     *        11111110 11010000 11110001 00101111 10001000 01011010 01001011 01101101
     *        |AC[11]           |AC[12]       |AC[13]        |14 |AC[15]|AC[16]  |AC[17]      14 - alias for AC[14]
     *        -----------------------------------------------------------------------
     *        00001110 11010010 01000100 01111111 10110011 10011001 00010000 00101110
     *        AC[17]|AC[18]  |AC[19]|20  |21 |AC[23]        |24|AC[25]|26 |27 |28 |30
     *        -----------------------------------------------------------------------
     *        -----------------------------------------------------------------------
     *        11111001 01100110 01100101 00011100 01000111 00011110 11110101 00100101
     *          30|AC[31]|AC[32]|33 |34  |35|38    |39   |40    |42     |43  |44|45|46 
     *        -----------------------------------------------------------------------  
     *        01000111 11001100 11011001 00000000 01110111 01100101 00010010 10111110
     *        46|47|50        |51  |53   |54|55|56 |61     |62 |63  |
     *        -----------------------------------------------------------------------  
     *        
     * Decimal representation after decoding:
     * 
     * decimal -129  270  238  -19  -230  -87  -8  15 
     *            2  -10    0   16    37   33   2   4
     *           -9  -28  -11    4    -3    3   0  -8
     *            1    4   -3   -1    -2    0   3   5
     *            6    2   -2   -1     0    0  -1  -4
     *           -4    0    3   -2     1    1  -2  -1
     *            0    0    2    2     0    1  -1  -1 
     *           -1    0    0    0     0    1   2  -2 
     * ------------------------------------------------------------------------------
     * Note: coefficients are in zig zag sequence, even thought they are written as two
     *       dimensional array, actually this is one dimensional array. 
     * 
     * Note: consider AC[9], AC[11]. Element AC[10] is missed in binary representation 
     * because it's equal to zero. To get an understanding of why does this happen
     * look at element AC[11]. It's RC value is equal to 21, that is split in two 
     * parts RRRR=1 and SSSS=5. RRRR is a length of 0 sequence that lies between 
     * previous non zero element(AC[9]) and current non zero element(it's index
     * is (9 + 1) + 1 = 11, where 9 index of AC[9] 9+1 is index of the first zero element
     * in the 0 sequence and 1 length of zero sequence). This leads to AC[10]=0 and 
     * AC[11] decoded using SSSS value(as right border of zero border, AC[9] is it's 
     * left border). 
     *      
     * @throws IOException
     */
    @Test
    public void testDecodeAC() throws IOException {
        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////// encoded 63 AC coefficients /////////////////////////////////////// 
        InputStream is = Mockito.mock(InputStream.class);
        when(is.read(any(byte[].class), any(Integer.class), any(Integer.class))).then(new Answer<Integer>() {
            
            private byte[][] chunks = new byte[3][];
            private int chunkIndex = 0;
            
            {
                chunks[0] = new byte[] {
                        (byte)0xf9, (byte)0xfb, (byte)0xfe, 0x0a, 0x1d, (byte)0xfb, 0x77, 0x69, 
                        (byte)0x9f, (byte)0xb0, (byte)0xcf, (byte)0xc2, (byte)0x8b, 0x7b, (byte)0xf6, (byte)0xb5};
                chunks[1] = new byte[] {
                        (byte)0xfe, (byte)0xd0, (byte)0xf1, 0x2f, (byte)0x88, (byte)0x5a, 0x4b, 0x6d, 
                        (byte)0x0e, (byte)0xd2, (byte)0x44, (byte)0x7f, (byte)0xb3, (byte)0x99, (byte)0x10, (byte)0x2e};
                chunks[2] = new byte[] {
                        (byte)0xf9, (byte)0x66, (byte)0x65, (byte)0x1c, (byte)0x47, (byte)0x1e, (byte)0xf5, (byte)0x25, 
                        (byte)0x47, (byte)0xcc, (byte)0xd9, (byte)0x00, (byte)0x77, (byte)0x65, (byte)0x12, (byte)0xbe};
            }
            
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                byte[] buffer = ((byte[])args[0]);
                for(int i=0; i<16; i++) buffer[i] = chunks[chunkIndex][i];
                chunkIndex++;
                return 16;
            }
        });
        NextBitReader nbr = new NextBitReader(new BufferedReader(is)); 
        //////////////////////////////////////////////////////////////////////////////////////////
        
        //////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////// decode DC coefficient /////////////////////////////////////// 
        int[] maxCode = new int[] { -1, 0, 6, 14, 30, 62, 126, 254, 510, -1, -1, -1, -1, -1, -1, -1 };
        int[] minCode = new int[] { 0, 0, 2, 14, 30, 62, 126, 254, 510, 0, 0, 0, 0, 0, 0, 0 };
        int[] valPtr = new int[] { 0, 0, 1, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0 };
        int[] huffVal = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        DecodeTables dcDt = new DecodeTables(maxCode, minCode, valPtr);
        
        int dc = dcDp.decodeDc(dcDt, huffVal, nbr, new MCUCalculationDataHolder())[0];
        //////////////////////////////////////////////////////////////////////////////////////////
        
        //////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////// decode 63 AC coefficient //////////////////////////////////
        maxCode = new int[] {-1, 1, 4, 12, 28, 59, 123, 250, 506, 1018, 2041, 4087, -1, -1, 32704, 65534};
        minCode = new int[] { 0, 0, 4, 10, 26, 58, 120, 248, 502, 1014, 2038, 4084,  0,  0, 32704, 65410};
        valPtr =  new int[] { 0, 0, 2, 3,  6,  9,  11,  15,  18,  23,   28,   32,    0,  0, 36,    37};
        //
        huffVal = new int[] { 1,   2,   3,   0,   4,   17,  5,   18,  33,  49,  65,  6,   19,  81,  97,  7,   34,  113, 20,  50, 
                              129, 145, 161, 8,   35,  66,  177, 193, 21,  82,  209, 240, 36,  51,  98,  114, 130, 9,   10,  22, 
                              23,  24,  25,  26,  37,  38,  39,  40,  41,  42,  52,  53,  54,  55,  56,  57,  58,  67,  68,  69, 
                              70,  71,  72,  73,  74,  83,  84,  85,  86,  87,  88,  89,  90,  99,  100, 101, 102, 103, 104, 105, 
                              106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 
                              149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 
                              183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 
                              217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 
                              249, 250 };
        //
        DecodeTables dts = new DecodeTables(maxCode, minCode, valPtr);
        
        int[] actualDu = acDp.decodeAc(dts, huffVal, nbr, new MCUCalculationDataHolder());
        actualDu[0] = dc;
        //////////////////////////////////////////////////////////////////////////////////////////
        
        int[] expectedDu = new int[] {-129,  270,  238,  -19,  -230,  -87,  -8,  15, 
                                         2,  -10,    0,   16,    37,   33,   2,   4,
                                        -9,  -28,  -11,    4,    -3,    3,   0,  -8,
                                         1,    4,   -3,   -1,    -2,    0,   3,   5,
                                         6,    2,   -2,   -1,     0,    0,  -1,  -4,
                                        -4,    0,    3,   -2,     1,    1,  -2,  -1,
                                         0,    0,    2,    2,     0,    1,  -1,  -1,
                                        -1,    0,    0,    0,     0,    1,   2,  -2};
        
        assertEquals(expectedDu.length, actualDu.length);
        
        for(int i=0; i<actualDu.length; i++) assertEquals(expectedDu[i], actualDu[i]);
    }
    
}
