package decoder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import util.FileSystemComponentReader;

public class ComponentInFileSystemAssemblerTest {

	/**
	 * Phase1: Process MCU row(by MCU here is defined part of the MCU that belongs to one specific component).
	 * 		   Break up MCU row into DU rows and save them into separate files(one file per DU row). 
	 *
	 * Phase2: For each file with DU rows 8 new files are created(each file for a row). Output is rearranged
	 * 		   such that firstly goes consolidation of all first lines of all DUs from a given DU row, then
	 * 		   the second one and down to the 8th line. Order of the samples is the same as DU order in DU row.
	 * 
	 * Phase3: 8 files that represents samples rows of the component saved into component output file.
	 * 		   Order of rows is from 1t row till the 8th. 
	 *         Important: at this moment all padding samples are eliminated, based on the component width
	 *         			  and height    
	 *
	 * @throws Exception 
	 */
	@Test
	public void testAssemble() throws Exception {
		//image component serial number
		int id = 0;
		//horizontal sampling factor for component0
		int hs = 2;
		//vertical sampling factor for component0
		int vs = 2;
		//original(no extensions) number of samples in a row per component 
		int xs = 32;
		//original(no extensions) number of samples in a column per component
		int ys = 16;
		//number of MCUs(not the entire MCU, just component0 part of the MCU) in a row 
		int mcuHs = 2;
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Input & expectation(expected two files, one for each row; each file contains four DU, one followed 
		// by another)
		//
		// Input ordering is 
	    // MCU1 		  | MCU2
		// -----------------------------------------------------------------
		// DU1[0], DU1[1] | DU2[0], DU2[1], 		
		// DU1[2], DU1[3] | DU2[2], DU2[3] 
		//
		// Input order:
		// DU1[0], DU1[1], DU1[2], DU1[3], DU2[0], DU2[1], DU2[2], DU2[3]
		//
		// Transformed order:
		// DU row1: DU1[0], DU1[1], DU2[0], DU2[1] 
		// DU row2: DU1[2], DU1[3], DU2[2], DU2[3]  
		
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
				
		try(ComponentInFileSystemAssembler ca = new ComponentInFileSystemAssembler(id, hs, vs, xs, ys, mcuHs)) {
			ca.add(row1[0]); ca.add(row1[1]); ca.add(row1[2]); ca.add(row1[3]);
			ca.add(row2[0]); ca.add(row2[1]); ca.add(row2[2]); ca.add(row2[3]);
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		//Result verification
		
		FileSystemComponentReader fscr = new FileSystemComponentReader(id);
		int[][] component = new int[ys][xs];
		for(int i=0; i<ys; i++) 
			for(int j=0; j<xs; j++) 
				component[i][j] = fscr.read();
		fscr.close();
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Expected result				
		
		//2 DU rows, with 4 DUs in each of the row produce 16x32 output array of samples
		int[][] samples = new int[][] {
			{1,   2,  3,  4,  5,  6,  7,  8,  1,  2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8},
            {9,  10, 11, 12, 13, 14, 15, 16,  9, 10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16},
            {17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24},
            {25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32},
            {33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40},
            {41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48},
            {49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56},
            {57, 58, 59, 60, 61, 62, 63, 64, 57, 58, 59, 60, 61, 62, 63, 65, 57, 58, 59, 60, 61, 62, 63, 63, 57, 58, 59, 60, 61, 62, 63, 62},
            {1,   2,  3,  4,  5,  6,  7,  8,  1,  2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8, 1,   2,  3,  4,  5,  6,  7,  8},
            {9,  10, 11, 12, 13, 14, 15, 16,  9, 10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16, 9,  10, 11, 12, 13, 14, 15, 16},
            {17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24, 17, 18, 19, 20, 21, 22, 23, 24},
            {25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32, 25, 26, 27, 28, 29, 30, 31, 32},
            {33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40, 33, 34, 35, 36, 37, 38, 39, 40},
            {41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48, 41, 42, 43, 44, 45, 46, 47, 48},
            {49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56},
            {57, 58, 59, 60, 61, 62, 63, 66, 57, 58, 59, 60, 61, 62, 63, 67, 57, 58, 59, 60, 61, 62, 63, 61, 57, 58, 59, 60, 61, 62, 63, 60}
		};
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		for(int i=0; i<ys; i++) 
			for(int j=0; j<xs; j++) {
				if(samples[i][j] != component[i][j]) System.out.println("i="+i+",j="+j);
				assertEquals(samples[i][j], component[i][j]);
			}
	}
	
}
