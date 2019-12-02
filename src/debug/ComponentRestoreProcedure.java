package debug;

import decoder.DecoderContext;
import util.FileSystemComponentReader;

/**
 * Reads samples from component files and puts them into
 * two dimensional arrays
 */
public class ComponentRestoreProcedure {
	
	public int[][][] restore(DecoderContext dc) throws Exception {
		//number of components
        int nComponents = dc.frameHeader.Nf;   
        //original(no extensions) number of samples in a row per component 
        int[] xs = dc.dimensionsContext.Xs;
        //original(no extensions) number of samples in a column per component
        int[] ys = dc.dimensionsContext.Ys;
        
        int[][][] samples = new int[nComponents][][];
        for(int i=0; i<nComponents; i++)
        	samples[i] = new int[ys[i]][xs[i]];
        
        for(int i=0; i<nComponents; i++) {
        	FileSystemComponentReader fscr = new FileSystemComponentReader(i);
        	for(int j=0; j<ys[i]; j++)
        		for(int k=0; k<xs[i]; k++)
        			samples[i][j][k] = fscr.read();
        	fscr.close();
        }
        
        return samples;
	}
	
}
