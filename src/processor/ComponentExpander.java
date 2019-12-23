package processor;

import java.io.IOException;

import decoder.DecoderContext;
import util.FileSystemComponentReader;
import util.FileSystemExtendedComponentWriter;
import util.FileSystemExtendedRowReader;
import util.FileSystemExtendedRowWriter;

/**
 * Pre-condition: there already exist three files in the file system(tmp dir), each file
 * 				  corresponds to one of the component image.
 *
 * Jpeg components are constructed in such a way that often exist one component that has 
 * width and length equal to the width and length of the image itself, and two components
 * whose width is Hmax/Hs and length Vmax/Vs smaller than the width and length of the original 
 * image.
 * 
 * -------------------------------------------------------------------------------------------
 * Example: 
 *    component1 component2 component3
 * Hs     2          1           1
 * Vs     2          1           1
 * 
 * X(number of samples per line, width) = 200
 * Y(number of lines, height)           = 100
 * 
 * component 1 width  = 200/(2/2) = 200
 * component 1 height = 100/(2/2) = 100
 * component 2 width  = 200/(2/1) = 100
 * component 2 height = 100/(2/1) = 50
 * component 3 width  = 200/(2/1) = 100
 * component 3 height = 100/(2/1) = 50
 * 
 * Let's consider components 1,2,3:
 * 
 * C1                 |C2       |C3
 * sample11 sample12  |sample21 |sample31
 * 
 * To restore pixel it necessary to have triplet of samples:
 * case 1: sample11 has pairs sample21 and sample31(we have this explicitly)
 * case 2: sample12 has pairs sample21 and sample31(we have this implicitly)
 * 
 * During image restore case2 should be handled by the decoder and pairs sample21 sample31
 * (also pairs of sample11) should be used for sample12 also. The main reason for that 
 * is to minimize space of component2 and component3, by avoiding duplicate values sample21 
 * sample31, should've been kept twice otherwise. 
 * -------------------------------------------------------------------------------------------
 * 
 * The main intent of this class is to extend smaller components to the size of the bigger 
 * one(hence image itself), by adding missing(duplicates that are avoided in smaller components)
 * samples. Note, that during restore missing columns would be added as well as missing rows.
 * Number of additions would be find based on Vs,Hs,Vmax,Hmax.
 * 
 * Given the example above, after restore is done:
 * C1                 |C2       		 |C3
 * sample11 sample12  |sample21 sample21 |sample31 sample31
 * 
 * At the end all three component sizes would be equal.
 * 
 */
public class ComponentExpander {
	
	public void extend(DecoderContext dc) throws Exception {
		int maxVs = maxSamplingFactor(dc.frameHeader.Vs);
		int maxHs = maxSamplingFactor(dc.frameHeader.Hs);
		for(int i=0; i<dc.frameHeader.Nf; i++) extendInternally(i, maxVs, maxHs, dc);
	}
	
	/**
	 * Extends component(defined by componentIndex) to the size of the biggest component by
	 * adding missing columns and rows(addition is made by duplication of the current pixel
	 * to the right(column addition) or to the bottom(row addition); number of duplicating
	 * columns/rows is set by Vs,Hs,Vmax,Hmax).
	 * 
	 * @param componentIndex
	 * @param maxVs
	 * @param maxHs
	 * @param dc
	 * @throws Exception
	 */
	private void extendInternally(int componentIndex, int maxVs, int maxHs, DecoderContext dc) throws Exception {
		//samples in a row
		//component original row length 
		int rowLength = dc.dimensionsContext.Xs[componentIndex];
		
		//samples in a column
		//component original column length		
		int columnLength = dc.dimensionsContext.Ys[componentIndex];
		
		//samples in a row
		//extended row length = component row length * [maxHs/Hs]
		int extRowLength = dc.dimensionsContext.Xs[componentIndex]*(maxHs/dc.frameHeader.Hs[componentIndex]);
		
		//samples in a column
		//extended column length = component column length * [maxVs/Vs]
		int extColumnLength = dc.dimensionsContext.Ys[componentIndex]*(maxVs/dc.frameHeader.Vs[componentIndex]);
		
		//times to repeat the same sample in a row
		int rr = maxHs/dc.frameHeader.Hs[componentIndex];
		
		//times to repeat the same sample in a column
		int rc = maxVs/dc.frameHeader.Vs[componentIndex];
		
		try(FileSystemComponentReader fscr = new FileSystemComponentReader(componentIndex);
			FileSystemExtendedComponentWriter fsecw = new FileSystemExtendedComponentWriter(componentIndex)) 
		{
			for(int i=0; i<columnLength; i++) {
				//create rc row writers
				FileSystemExtendedRowWriter[] fserws = new FileSystemExtendedRowWriter[rc];
				for(int k=0; k<rc; k++) fserws[k] = new FileSystemExtendedRowWriter(componentIndex, k);
				
				for(int j=0; j<rowLength; j++) repeatSample(fscr.read(), fserws, rr, rc);
				
				//close rc row writers
				for(int k=0; k<rc; k++) fserws[k].close();
				
				//merge rc rows into output/extended component file
				/////////////////////////////////////////////////////////////////////////////////////////
				//create rc row readers
				FileSystemExtendedRowReader[] fserrs = new FileSystemExtendedRowReader[rc];
				for(int k=0; k<rc; k++) fserrs[k] = new FileSystemExtendedRowReader(componentIndex, k);
				
				for(int k=0; k<rc; k++)
					for(int j=0; j<extRowLength; j++)
						fsecw.write(fserrs[k].read());
				
				//close rc row readers
				for(int k=0; k<rc; k++) fserrs[k].close();
			}
		}
	}
	
	/**
	 * Repeat the same sample both in a row and in a column.
	 * 
	 * Example:
	 * component1		    component2 
	 * sample11 sample12    sample21
	 * sample13 sample14 
	 * 
	 * Vs=[2,1] Hs=[2,1]
	 * 
	 * Select component2
	 * times to repeat the same sample in a row    = 2(maxHs)/1(Hs[1]) = 2
	 * times to repeat the same sample in a column = 2(maxVs)/1(Vs[1]) = 2
	 * 
	 * Extended component2
	 * sample21 sample21
	 * sample21 sample21
	 * @throws IOException 
	 */
	private void repeatSample(int sample, FileSystemExtendedRowWriter[] fserws, int rr, int rc) throws IOException {
		for(int i=0; i<rc; i++) 
			for(int j=0; j<rr; j++)
				fserws[i].write(sample);
	}
	
	/**
	 * 
	 * @param sf - sampling factors either Xs or Ys 
	 * @return
	 */
	private int maxSamplingFactor(int[] sf) {
		int max = 0;
		
		for(int i=0; i<sf.length; i++)
			if(sf[i] > max) max = sf[i];
			
		return max;
	}	
	
}
