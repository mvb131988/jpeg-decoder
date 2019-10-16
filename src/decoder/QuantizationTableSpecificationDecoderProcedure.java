package decoder;

import java.util.ArrayList;
import java.util.List;

import markers.QuantizationTableSpecification;

/**
 * In respect with the specification, single quantization table specification marker 
 * could contain more than one quantization table. If this is the case quantization tables
 * are placed sequentially(no gaps or delimiters in between) in the marker. 
 */
public class QuantizationTableSpecificationDecoderProcedure {

	public List<QuantizationTableSpecification> decode(int[] qtsHeader) {
		int pos = 0;
		
		//huffman table specification marker length
        int lh = (qtsHeader[pos] << 8) + qtsHeader[pos+1];
        
        pos = 2;
        
        List<QuantizationTableSpecification> qtsList = new ArrayList<QuantizationTableSpecification>();
        while(pos < lh) {
        	//create huffman table specification
        	Context c = decodeInternally(qtsHeader, pos);
        	qtsList.add(c.qts);
        	pos = c.pos;
        }
        
		return qtsList;
	}
	
	private Context decodeInternally(int[] qtsHeader, int pos) {
		//only 8 bit precision is supported
        //TODO: add assertion
        int pq = ((qtsHeader[pos] & 0xf0) >>> 4) == 0 ? 8 : 16;
        int tq =  qtsHeader[pos] & 0x0f;
		pos++;
		
		int[] qks = new int[64];
		for(int i=0; i<64; i++) qks[i] = qtsHeader[pos+i];
		pos += 64;
        
		return new Context(new QuantizationTableSpecification(pq, tq, qks), pos);
	}
	
	private static class Context {
		QuantizationTableSpecification qts;
		int pos;

		public Context(QuantizationTableSpecification qts, int pos) {
			super();
			this.qts = qts;
			this.pos = pos;
		}
	}
	
}
