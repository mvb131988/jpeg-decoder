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
        dudp.setDudp(duDequantPrc);
    }

    @Test
    public void testInverseZigZag() throws IOException {
        NextBitReader nbr = Mockito.mock(NextBitReader.class);
        QuantizationTableSpecification qts = Mockito.mock(QuantizationTableSpecification.class);
        
        int[] zzZigZag = new int[64];
        for(int i=0; i<64; i++) zzZigZag[i] = i;
        
        when(dcDp.decodeDc(null, null, nbr)).thenReturn(new int[] {0});
        when(acDp.decodeAc(null, null, nbr)).thenReturn(zzZigZag);
        //no quantization table available at this moment. Just fake it with zzZigZag
        when(qts.getQks()).thenReturn(zzZigZag);
        
        int[][] zz = dudp.decode(nbr, 
                                 Mockito.mock(HuffmanTableSpecification.class), 
                                 Mockito.mock(HuffmanTableSpecification.class),
                                 qts);
        
        int[][] expectedZZ = new int[][] {{0,  1,  5,  6,  14, 15, 27, 28},
                                          {2,  4,  7,  13, 16, 26, 29, 42},
                                          {3,  8,  12, 17, 25, 30, 41, 43},
                                          {9,  11, 18, 24, 31, 40, 44, 53},
                                          {10, 19, 23, 32, 39, 45, 52, 54},
                                          {20, 22, 33, 38, 46, 51, 55, 60},
                                          {21, 34, 37, 47, 50, 56, 59, 61},
                                          {35, 36, 48, 49, 57, 58, 62, 63}};
        
        assertEquals(expectedZZ.length, zz.length);                                          
        for(int i=0; i<expectedZZ.length; i++) {
            for(int j=0; j<expectedZZ.length; j++) {
                assertEquals(expectedZZ[i][j], zz[i][j]);
            }
        }
    }
    
}
