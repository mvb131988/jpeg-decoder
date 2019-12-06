package decoder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import markers.FrameHeader;
import markers.ScanHeader;
import util.FileSystemComponentReader;
import util.FileSystemMCUWriter;

public class MCUsFlattenerTest {

	private MCUsFlattener mcusFlattener = new MCUsFlattener();
	
	/**
	 * Refer to RestartIntervalDecoderProcedureTest
	 * @throws Exception
	 */
	@Test
	public void testFlattenMCUs() throws Exception {
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
		
		FileSystemMCUWriter fsmw = new FileSystemMCUWriter();
		//init 4 mcus
		for(int i=0; i<4; i++) {
			//one mcu of 6 dus
			int[][][] dus = new int[6][][];
			for(int m=0; m<6; m++) {
		        dus[m] = new int[][] {
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
			fsmw.write(dus);
		}
		fsmw.close();
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//total number of MCU(including padding extensions)
		int numberOfMcu = 4;
		mcusFlattener.flattenMCUs(numberOfMcu, dc);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// asserts
		
		//samples for component 1
        int[][] samples1 = 
        {{1,   2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8, 1,   2},
         {9,  10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16, 9,  10},
         {17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18},
         {25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26}, 
         {33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34},
         {41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42},
         {49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50},
         {57, 58, 59, 60, 61, 62, 63, 64, 57, 58, 59, 60, 61, 62, 63, 64, 57, 58},
         {1,   2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8, 1,   2},
         {9,  10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16, 9,  10},
         {17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18},
         {25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26}, 
         {33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34},
         {41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42},
         {49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50},
         {57, 58, 59, 60, 61, 62, 63, 64, 57, 58, 59, 60, 61, 62, 63, 64, 57, 58},
         {1,   2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8, 1,   2},
         {9,  10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16, 9,  10},
         {17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18},
         {25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26}};
        
        //samples for component 2, 3
        int[][] samples23 = 
        {{1,   2,  3,  4,  5,  6,  7,  8, 1}, 
         {9,  10, 11, 12, 13, 14, 15, 16, 9},
         {17, 18, 19, 20, 21, 22, 23, 24, 17}, 
         {25, 26, 27, 28, 29, 30, 31, 32, 25}, 
         {33, 34, 35, 36, 37, 38, 39, 40, 33}, 
         {41, 42, 43, 44, 45, 46, 47, 48, 41},
         {49, 50, 51, 52, 53, 54, 55, 56, 49},
         {57, 58, 59, 60, 61, 62, 63, 64, 57},
         {1,   2,  3,  4,  5,  6,  7,  8, 1},
         {9,  10, 11, 12, 13, 14, 15, 16, 9}};
        
        FileSystemComponentReader fscr0 = new FileSystemComponentReader(0);
        for(int i=0; i<samples1.length; i++)
        	for(int j=0; j<samples1[0].length; j++)
        		assertEquals(samples1[i][j], fscr0.read());
        //EOF
        assertEquals(-1, fscr0.read());
        fscr0.close();
        
        FileSystemComponentReader fscr1 = new FileSystemComponentReader(1);
        for(int i=0; i<samples23.length; i++)
        	for(int j=0; j<samples23[0].length; j++)
        		assertEquals(samples23[i][j], fscr1.read());
        //EOF
        assertEquals(-1, fscr1.read());
        fscr1.close();
        
        FileSystemComponentReader fscr2 = new FileSystemComponentReader(2);
        for(int i=0; i<samples23.length; i++)
        	for(int j=0; j<samples23[0].length; j++)
        		assertEquals(samples23[i][j], fscr2.read());
        //EOF
        assertEquals(-1, fscr2.read());
        fscr2.close();
	}
	
}
