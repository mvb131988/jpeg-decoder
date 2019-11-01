package decoder;

import java.util.ArrayList;
import java.util.List;

import markers.HuffmanTableSpecificationsTransformer;

/**
 * Main intension is to store temporary data used throughout MCU
 * calculation, avoiding the necessity to allocate new space to these
 * data structures o each iteration (during processing of each separate MCU).  
 */
public class MCUCalculationDataHolder {

	public int[][][] mcu;
	
	//--------------- Data unit decoding procedure ------------------------------------
	private List<Integer> huffSize = new ArrayList<>();
    
    List<Integer> huffCode = new ArrayList<>();
    
    //largest code value for a given length
    public int[] maxCode = new int[16];
    
    //smallest code value for a given length
    public int[] minCode = new int[16];
    
    //index to the start of the list of values in huffVal, where in valPtr[i] i is word length 
    public int[] valPtr = new int[16];
    
    public int[] zz = new int[64];
    
    public int[] tArr = new int[1];
    
    public int[] diffArr = new int[1];
    
    public HuffmanTableSpecificationsTransformer.DecodeTables dts = 
    		new HuffmanTableSpecificationsTransformer.DecodeTables();
    		
    public List<Integer> emptyHuffSize() {
    	huffSize.clear();
    	return huffSize;
    }
    
    public List<Integer> emptyHuffCode() {
    	huffCode.clear();
    	return huffCode;
    }
    //----------------------------------------------------------------------------------
    
    public int[][] orderedZz = new int[8][8];
    public int[][] orderedQks = new int[8][8];
    
}
