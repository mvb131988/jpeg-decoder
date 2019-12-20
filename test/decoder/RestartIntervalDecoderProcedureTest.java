package decoder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import markers.FrameHeader;
import markers.Image;
import markers.ScanHeader;
import util.BufferedReader;
import util.FileSystemMCUReader; 

public class RestartIntervalDecoderProcedureTest {

    private RestartIntervalDecoderProcedure ridp;
    
    private MCUDecoderProcedure dp;
    
    private MCUsFlattener msf;
    
    @Before
    public void init() {
        dp = Mockito.mock(MCUDecoderProcedure.class);
        msf = Mockito.mock(MCUsFlattener.class);
        ridp = new RestartIntervalDecoderProcedure();
        ridp.setDp(dp);
        ridp.setMsf(msf);
    }
    
    /**
     * Input represents image with the following parameters:
     * - height x width = 20 x 18
     * - 3 components with sampling factors(vertical and horizontal) 2x2, 1x1, 1x1 (1t, 2d, 3d component)
     * 
     * After all extensions components would look like:
     * 
     * 1't component of 16 du's         2'd and 3'd component of 4 du's 
     * |----|----|----|----|            |----|----|
     * |    |    |    |    |            |    |    |        
     * |----|----|----|----|            |----|----|        
     * |    |    |    |    |            |    |    |
     * |----|----|----|----|            |----|----|
     * |    |    |    |    |
     * |----|----|----|----|
     * |    |    |    |    |
     * |----|----|----|----|
     * 
     * Image would consist of 4 MCUs, each MCU of 4 du's from the 1't component, 1 du from the 2'd component
     * and 1 du from the 3'd component
     * @throws Exception 
     */
    @Test
    public void testDecodeRestartInterval() throws Exception {
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        // input
        
        //horizontal and vertical sampling factors by components
        int[] Hs = new int[] {2, 1, 1};
        int[] Vs = new int[] {2, 1, 1};
        
        //size in samples per component
        int[] Ys = new int[] {20, 10, 10};
        int[] Xs = new int[] {18, 9, 9};
        
        //extended size in samples per component(multiple of 8)
        int[] extYs = new int[] {24, 16, 16};
        int[] extXs = new int[] {24, 16, 16};
        
        //number of data units, based on extended size
        int[] dataUnitsYs = new int[] {3, 2, 2};
        int[] dataUnitsXs = new int[] {3, 2, 2};
        
        //number of extended data units based on number of data units(multiple
        //of corresponding sampling factor)
        int[] extYDataUnit = new int[] {4, 2, 2};
        int[] extXDataUnit = new int[] {4, 2, 2};
        
        //size in samples based on extended data units(per component)
        int[] finalExtYs = new int[] {32, 16, 16};
        int[] finalExtXs = new int[] {32, 16, 16};
        
        DimensionsContext dimc = new DimensionsContext(Xs, Ys, extXs, extYs, dataUnitsXs, dataUnitsYs,
                                                       extXDataUnit, extYDataUnit, finalExtXs, finalExtYs);
        
        FrameHeader fh = new FrameHeader();
        fh.Nf = 3;
        fh.Cs = new int[] {1, 2, 3};
        fh.Hs = Hs;
        fh.Vs = Vs;
        
        ScanHeader sh = new ScanHeader();
        sh.Ns = 3;
        sh.Cs = new int[] {1, 2, 3};
        
        DecoderContext dc = new DecoderContext();
        dc.frameHeader = fh;
        dc.scanHeader = sh;
        dc.dimensionsContext = dimc;
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        // MCUs
        
        when(dp.decodeMCU(Mockito.any(NextBitReader.class), Mockito.any(DecoderContext.class)))
        .then(new Answer<int[][][]>() {
            
            private int[][][][] mcus = new int[4][][][];
            private int mcuPos = 0;
           
            {
                for(int i=0; i<4; i++) {
                    mcus[i] = new int[6][][];
                    //init DU
                    for(int j=0; j<6; j++) {
                        mcus[i][j] = new int[][] {
                                                    {1,   2,  3,  4,  5,  6,  7,  8},
                                                    {9,  10, 11, 12, 13, 14, 15, 16},
                                                    {17, 18, 19, 20, 21, 22, 23, 24},
                                                    {25, 26, 27, 28, 29, 30, 31, 32},
                                                    {33, 34, 35, 36, 37, 38, 39, 40},
                                                    {41, 42, 43, 44, 45, 46, 47, 48},
                                                    {49, 50, 51, 52, 53, 54, 55, 56},
                                                    {57, 58, 59, 60, 61, 62, 63, 64}
                                                };
                    }
                };

            }
            
            @Override
            public int[][][] answer(InvocationOnMock invocation) throws Throwable {
                return mcus[mcuPos++];
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        
        BufferedReader br = Mockito.mock(BufferedReader.class);
        ridp.decodeRestartInterval(br, dc);
        
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        // asserts
        
        //read 4 MCUs
        FileSystemMCUReader fsmr = new FileSystemMCUReader(6);
        for(int m=0; m<4; m++) {
	        int[][][] mcu = fsmr.read();
	        assertEquals(6, mcu.length);
	        for(int i=0; i<6; i++) {
	        	int sample = 1;
	        	for(int j=0; j<8; j++)
	        		for(int k=0; k<8; k++)
	        			assertEquals(sample++, mcu[i][j][k]);
	        }
        }
        
		assertArrayEquals(null, fsmr.read());
		fsmr.close();
	}
    
}
