package decoder;

import java.util.ArrayList;
import java.util.List;

import markers.HuffmanTableSpecification;

/**
 * In respect with the specification, single Huffman table specification marker 
 * could contain more than one Huffman table. If this is the case Huffman tables
 * are placed sequentially(no gaps or delimiters in between) in the marker. 
 */
public class HuffmanTableSpecificationDecoderProcedure {

	public List<HuffmanTableSpecification> decode(int[] htsHeader) {
		int pos = 0;
		
		//huffman table specification marker length
        int lh = (htsHeader[pos] << 8) + htsHeader[pos+1];
        
        pos = 2;
        
        List<HuffmanTableSpecification> htsList = new ArrayList<HuffmanTableSpecification>();
        while(pos < lh) {
        	//create huffman table specification
        	Context c = decodeInternally(htsHeader, pos);
        	htsList.add(c.hts);
        	pos = c.pos;
        }
        
		return htsList;
	}
	
	private Context decodeInternally(int[] htsHeader, int pos) {
		// Table class – 0 = DC table or lossless table, 1 = AC table
	    int tc = (htsHeader[pos] & 0x0F0)>>4;
	    //Huffman table destination identifier
	    int th = htsHeader[pos] & 0x0F;
	    pos++;
	    
		//Number of Huffman codes of length i 
	    int[] lis = new int[16];
		//sum of all numbers(each number is number of Huffman codes of length i)
	    int lisSum = 0;
	    
	    for(int i0=0, i=pos; i<pos+16; i++, i0++) {
            lis[i0] = htsHeader[i];
            lisSum += lis[i0];
        }
        pos += 16;
        
        int[] vij = new int[lisSum];
		for(int i0=0, i=pos; i<pos+lisSum; i++, i0++) vij[i0] = htsHeader[i];
		pos += lisSum;
         
        return new Context(new HuffmanTableSpecification(tc, th, lis, vij), pos);
	}
	
	private static class Context {
		HuffmanTableSpecification hts;
		int pos;

		public Context(HuffmanTableSpecification hts, int pos) {
			super();
			this.hts = hts;
			this.pos = pos;
		}
	}
	
}