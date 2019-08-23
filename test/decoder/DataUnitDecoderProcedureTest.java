package decoder;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import markers.HuffmanTableSpecification;
import markers.HuffmanTableSpecificationsTransformer;
import markers.QuantizationTableSpecification;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class DataUnitDecoderProcedureTest {

    private DataUnitDecoderProcedure dudp = new DataUnitDecoderProcedure();
    
    private HuffmanTableSpecificationsTransformer htst;
    
    private DCDecodeProcedure dcDp;
    
    private ACDecodeProcedure acDp;
    
    private DataUnitDequantizationProcedure duDequantPrc;
    
    @Before
    public void setUp() {
        htst = Mockito.mock(HuffmanTableSpecificationsTransformer.class);
        dcDp = Mockito.mock(DCDecodeProcedure.class);
        acDp = Mockito.mock(ACDecodeProcedure.class);
        duDequantPrc = Mockito.mock(DataUnitDequantizationProcedure.class);
        
        dudp.setHtst(htst);
        dudp.setDcDp(dcDp);
        dudp.setAcDp(acDp);
    }

    /**
     * This test case checks that dc coefficient is set as first element of
     * data unit zig zag coefficients sequence
     * 
     * @throws IOException
     */
    @Test
    public void testDecode() throws IOException {
        NextBitReader nbr = Mockito.mock(NextBitReader.class);
        QuantizationTableSpecification qts = Mockito.mock(QuantizationTableSpecification.class);
        
        int[] zzZigZag = new int[64];
        for(int i=0; i<64; i++) zzZigZag[i] = i;
        
        when(dcDp.decodeDc(null, null, nbr)).thenReturn(new int[] {-64});
        when(acDp.decodeAc(null, null, nbr)).thenReturn(zzZigZag);
        //no quantization table available at this moment. Just fake it with zzZigZag
        when(qts.getQks()).thenReturn(zzZigZag);
        
        int[] zz = dudp.decode(nbr, 
                               Mockito.mock(HuffmanTableSpecification.class), 
                               Mockito.mock(HuffmanTableSpecification.class),
                               qts);
        
        int[] expectedZZ = new int[] {-64,  1,   2,  3,  4,  5,  6,  7,
                                        8,  9,  10, 11, 12, 13, 14, 15,
                                       16,  17, 18, 19, 20, 21, 22, 23,
                                       24,  25, 26, 27, 28, 29, 30, 31,
                                       32,  33, 34, 35, 36, 37, 38, 39,
                                       40,  41, 42, 43, 44, 45, 46, 47,
                                       48,  49, 50, 51, 52, 53, 54, 55,
                                       56,  57, 58, 59, 60, 61, 62, 63};
        
        assertEquals(expectedZZ.length, zz.length);                                          
        for(int i=0; i<zz.length; i++) assertEquals(expectedZZ[i], zz[i]);
    }
    
}
