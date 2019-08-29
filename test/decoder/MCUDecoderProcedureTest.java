package decoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import decoder.DataUnitDequantizationProcedure.Idct;
import markers.FrameHeader;
import markers.HuffmanTableSpecification;
import markers.QuantizationTableSpecification;
import markers.ScanHeader;

public class MCUDecoderProcedureTest {

    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    private DataUnitDecoderProcedure dataUnitDecoderProcedure;
    
    private DataUnitDequantizationProcedure dudp;
    
    @Before
    public void init() {
        dataUnitDecoderProcedure = Mockito.mock(DataUnitDecoderProcedure.class);
        dudp =  Mockito.mock(DataUnitDequantizationProcedure.class);
        dp.setDataUnitDecoderProcedure(dataUnitDecoderProcedure);
        dp.setDudp(dudp);
    } 
    
    /**
     * This test case imitates situation when MCU consists of 3 components of the following sizes:
     * -component 1 2rowsx2columns
     * -component 2 1rowsx1columns
     * -component 3 1rowsx1columns
     * 
     * Overall MCU contains of 6 data units.
     * 
     * Only DC coefficients are populated. Only addition of DC previous value is tested.
     * Quantization logic is mocked, only zig zag reordering is immitated without changes
     * in DC coefficient values.
     * 
     * @throws IOException
     */
    @Test
    public void testDecodeMCU() throws IOException {
        /////////////////////////////////////////////////////////////////////////////
        //frame header
        FrameHeader fh = new FrameHeader();

        //number of image components 
        fh.Nf = 3;
        //component factors (per component)
        fh.Vs = new int[] {2, 1, 1};
        fh.Hs = new int[] {2, 1, 1};
        
        //ids of quantization tables per component
        fh.Tqs = new int[] {0, 1, 1};
        //ids of quantization tables are equal to Tqs[i] from the frame header
        List<QuantizationTableSpecification> qtsList = new ArrayList<>();
        QuantizationTableSpecification qts0 = new QuantizationTableSpecification();
        qts0.Tq = 0;
        QuantizationTableSpecification qts1 = new QuantizationTableSpecification();
        qts1.Tq = 1;
        qtsList.add(qts0); qtsList.add(qts1);
        /////////////////////////////////////////////////////////////////////////////
        
        /////////////////////////////////////////////////////////////////////////////
        //scan header
        ScanHeader sh = new ScanHeader();
        //number of image components 
        sh.Ns = 3;
        //ids of dc tables, maps to Huffman table with tc=0 and th = Td[i] (per component)
        sh.Td = new int[] {0, 1, 1};
        //ids of ac tables, maps to Huffman table with tc=1 and th = Ta[i] (per component)
        sh.Ta = new int[] {0, 1, 1};
        
        List<HuffmanTableSpecification> htsList = new ArrayList<>();
        HuffmanTableSpecification hts0 = new HuffmanTableSpecification();
        HuffmanTableSpecification hts1 = new HuffmanTableSpecification();
        HuffmanTableSpecification hts2 = new HuffmanTableSpecification();
        HuffmanTableSpecification hts3 = new HuffmanTableSpecification();
        
        //DC tables
        hts0.tc=0; hts0.th=0;
        hts1.tc=0; hts1.th=1;
        //AC tables
        hts2.tc=1; hts2.th=0;
        hts3.tc=1; hts3.th=1;
        htsList.add(hts0); htsList.add(hts1); htsList.add(hts2); htsList.add(hts3);

        //size is equal to Ns
        //values of previous DC per each image component
        int[] predDCs = new int[] {1, 2, 3};
        /////////////////////////////////////////////////////////////////////////////
        
        /////////////////////////////////////////////////////////////////////////////
        //decoder context
        DecoderContext dc = new DecoderContext();
        dc.frameHeader = fh;
        dc.scanHeader = sh;  
        dc.qtsList = qtsList;
        dc.htsList = htsList;
        dc.predDcs = predDCs;
        /////////////////////////////////////////////////////////////////////////////
        
        /////////////////////////////////////////////////////////////////////////////
        //6 zz coefficient blocks in zig zag order(first 4 for 1-s component, 5'th 
        //for the second and 6'th for the third)
        int[] zz01 = new int[64]; int[] zz02 = new int[64]; int[] zz03 = new int[64];
        int[] zz04 = new int[64]; int[] zz05 = new int[64]; int[] zz06 = new int[64];
        zz01[0] = 10; zz02[0] = 20; zz03[0] = 30;
        zz04[0] = 40; zz05[0] = 50; zz06[0] = 60;
        /////////////////////////////////////////////////////////////////////////////
        
        NextBitReader nbr = Mockito.mock(NextBitReader.class);
        when(dataUnitDecoderProcedure.decode(nbr, hts0, hts2, qts0)).then(new Answer<int[]>() {
            
            //zig zag order data units for the first component
            private int[][] zzs01 = new int[][] {zz01, zz02, zz03, zz04};
            private int zz01Pos = 0;
           
            @Override
            public int[] answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                HuffmanTableSpecification dHts = ((HuffmanTableSpecification)args[1]);
                HuffmanTableSpecification aHts = ((HuffmanTableSpecification)args[2]);
                QuantizationTableSpecification qts = ((QuantizationTableSpecification)args[3]); 
                
                int[] zz0 = null;
                //first image component
                if(dHts.th == 0 && aHts.th == 0 && qts.Tq == 0) {
                    zz0 = zzs01[zz01Pos++];
                }    
                return zz0;
            }
        });
        
        when(dataUnitDecoderProcedure.decode(nbr, hts1, hts3, qts1)).then(new Answer<int[]>() {
            
            //zig zag order data units for the second and third component
            private int[][] zzs023 = new int[][] {zz05, zz06};
            private int zzs023Pos = 0;
            
            @Override
            public int[] answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                HuffmanTableSpecification dHts = ((HuffmanTableSpecification)args[1]);
                HuffmanTableSpecification aHts = ((HuffmanTableSpecification)args[2]);
                QuantizationTableSpecification qts = ((QuantizationTableSpecification)args[3]);
                
                int[] zz0 = null;
                //second and third component contains of a single data unit each.
                //go consequently second followed by third
                if(dHts.th == 1 && aHts.th == 1 && qts.Tq == 1) {
                    zz0 = zzs023[zzs023Pos++];
                }    
                return zz0;
            }
            
        });
        
        //no dequantization logic will be tested. Just copy of dc coefficient from zig zag ordered
        //array to two dimensional zig zag reordered array
        when(dudp.dequantize(Mockito.any(int[].class), 
                             Mockito.any(QuantizationTableSpecification.class),
                             Mockito.any(Idct.class)))
        .then(new Answer<int[][]>() {
            @Override
            public int[][] answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                int[] zz0 = ((int[])args[0]);
                int[][] zz1 = new int[8][8];
                zz1[0][0] = zz0[0];
                return zz1;
            }
        });
        
        int[][][] mcu = dp.decodeMCU(nbr, dc);
        
        /////////////////////////////////////////////////////////////////////////////
        //Asserts
        
        //number of data units in the MCU
        assertEquals(6, mcu.length);
        
        int[] initialPredDCs = new int[] {1, 2, 3};
        //initial zz DC coefficients
        int zz010 = 10, zz020 = 20, zz030 = 30;
        int zz040 = 40, zz050 = 50, zz060 = 60;
        
        int dc1 = zz010 + initialPredDCs[0];
        int dc2 = zz020 + dc1;
        int dc3 = zz030 + dc2;
        int dc4 = zz040 + dc3;
        int dc5 = zz050 + initialPredDCs[1];
        int dc6 = zz060 + initialPredDCs[2];
        
        assertEquals(dc1, mcu[0][0][0]);
        assertEquals(dc2, mcu[1][0][0]);
        assertEquals(dc3, mcu[2][0][0]);
        assertEquals(dc4, mcu[3][0][0]);
        assertEquals(dc5, mcu[4][0][0]);
        assertEquals(dc6, mcu[5][0][0]);
        
        assertEquals(64, mcu[0].length*mcu[0][0].length);
        assertEquals(64, mcu[0].length*mcu[1][0].length);
        assertEquals(64, mcu[0].length*mcu[2][0].length);
        assertEquals(64, mcu[0].length*mcu[3][0].length);
        assertEquals(64, mcu[0].length*mcu[4][0].length);
        assertEquals(64, mcu[0].length*mcu[5][0].length);
    }
    
}
