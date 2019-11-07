package decoder;

import org.junit.Test;

public class ComponentInFileSystemAssemblerTest {

	private ComponentInFileSystemAssembler ca;
	
	@Test
	public void testAdd() {
		//image component serial number
		int id = 0;
		//horizontal sampling factor for component0
		int hs = 2;
		//vertical sampling factor for component0
		int vs = 2;
		//number of MCUs(not the entire MCU, just component0 part of the MCU) in a row 
		int mcuHs = 2;
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Input
		
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
						            {57, 58, 59, 60, 61, 62, 63, 64}
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
						            {57, 58, 59, 60, 61, 62, 63, 64}
						        };
	    /////////////////////////////////////////////////////////////////////////////////////////////////////						        
				
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
						        
		ca = new ComponentInFileSystemAssembler(id, hs, vs, mcuHs);
		//ca.add(du);
	}
	
}
