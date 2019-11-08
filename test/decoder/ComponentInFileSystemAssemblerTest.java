package decoder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import util.FileSystemDUReader;

public class ComponentInFileSystemAssemblerTest {

	/**
	 * Phase1: Process MCU row(by MCU here is defined part of the MCU that belongs to one specific component).
	 * 		   Break up MCU row into DU rows and save them into separate files(one file per DU row). 
	 * @throws Exception 
	 */
	@Test
	public void testAddPhase1() throws Exception {
		//image component serial number
		int id = 0;
		//horizontal sampling factor for component0
		int hs = 2;
		//vertical sampling factor for component0
		int vs = 2;
		//number of MCUs(not the entire MCU, just component0 part of the MCU) in a row 
		int mcuHs = 2;
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Input & expectation(expected to files, one for each row; each file contains for DU, one followed 
		// by another)
		//
		// Input ordering is 
	    // MCU1 							  | MCU2
		// -----------------------------------------------------------------------
		// row1[0], row1[1], row2[0], row2[1] | row1[2], row1[3], row2[2], row2[3]		
		
		//1 MCU row consisted of 2 DU rows, each row of 4 DU
		int[][][] row1 = new int[4][][];
		for(int i=0; i<4; i++) 
			row1[i] = new int[][] {
			            			{1,   2,  3,  4,  5,  6,  7,  8},
						            {9,  10, 11, 12, 13, 14, 15, 16},
						            {17, 18, 19, 20, 21, 22, 23, 24},
						            {25, 26, 27, 28, 29, 30, 31, 32},
						            {33, 34, 35, 36, 37, 38, 39, 40},
						            {41, 42, 43, 44, 45, 46, 47, 48},
						            {49, 50, 51, 52, 53, 54, 55, 56},
						            {57, 58, 59, 60, 61, 62, 63, 64+i}
						        };
		
        int[][][] row2 = new int[4][][];
		for(int i=0; i<4; i++)
			row2[i] = new int[][] {
			            			{1,   2,  3,  4,  5,  6,  7,  8},
						            {9,  10, 11, 12, 13, 14, 15, 16},
						            {17, 18, 19, 20, 21, 22, 23, 24},
						            {25, 26, 27, 28, 29, 30, 31, 32},
						            {33, 34, 35, 36, 37, 38, 39, 40},
						            {41, 42, 43, 44, 45, 46, 47, 48},
						            {49, 50, 51, 52, 53, 54, 55, 56},
						            {57, 58, 59, 60, 61, 62, 63, 64-(i+1)}
						        };
				        
						        
	    /////////////////////////////////////////////////////////////////////////////////////////////////////						        
				
		try(ComponentInFileSystemAssembler ca = new ComponentInFileSystemAssembler(id, hs, vs, mcuHs)) {
			ca.add(row1[0]); ca.add(row1[1]); ca.add(row2[0]); ca.add(row2[1]);
			ca.add(row1[2]); ca.add(row1[3]); ca.add(row2[2]); ca.add(row2[3]);
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		//Result verification
		
		//read both DU rows 
		try(FileSystemDUReader fsdur1 = new FileSystemDUReader(0, 0);
			FileSystemDUReader fsdur2 = new FileSystemDUReader(0, 1)) 
		{	
			assertDU(row1[0], fsdur1.read());
			assertDU(row1[1], fsdur1.read());
			assertDU(row1[2], fsdur1.read());
			assertDU(row1[3], fsdur1.read());
			
			assertDU(row2[0], fsdur2.read());
			assertDU(row2[1], fsdur2.read());
			assertDU(row2[2], fsdur2.read());
			assertDU(row2[3], fsdur2.read());
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	/**
	 * Phase2: For each file with DU rows 8 new files are created(each file for a row). Output is rearranged
	 * 		   such that firstly goes consolidation of all first lines of all DUs from a given DU row, then
	 * 		   the second one and down to the 8th line. Order of the samples is the same as DU order in DU row.   
	 */
	@Test
	public void testAddPhase2() {
		
	}
	
	/**
	 * Phase3: 8 files that represents samples rows of the component saved into component output file.
	 * 		   Order of rows is from 1t row till the 8th. 
	 *         Important: at this moment all padding samples are eliminated, based on the component width
	 *         			  and height
	 */
	@Test
	public void testAddPhase3() {
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Expected result				
		
		//2 DU rows, with 4 DUs in each of the row produce 16x32 output array of samples
		int[][] samples = new int[16][32];
		
		//1't DU row initialization
		for(int i=0; i<8; i++)
		for(int j=0; j<32; j++)
		samples[i][j] = (j+1)%8 == 0 ? 8+(i*8) : (j+1)%8+i*8;
		
		//2'd DU row initialization		
		for(int i=0; i<8; i++)
		for(int j=0; j<32; j++)
		samples[8+i][j] = (j+1)%8 == 0 ? 8+(i*8) : (j+1)%8+i*8; 
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	private void assertDU(int[][] expectedDU, int[][] actualDU) {
		assertEquals(expectedDU.length, actualDU.length);
		assertEquals(expectedDU[0].length, actualDU.length);
		for(int i=0; i<expectedDU.length; i++)
		    for(int j=0; j<expectedDU[0].length; j++)
		        assertEquals(expectedDU[i][j], actualDU[i][j]);
	}
	
}
