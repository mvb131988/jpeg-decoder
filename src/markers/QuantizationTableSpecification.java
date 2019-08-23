package markers;

public class QuantizationTableSpecification {
    
    //Quantization table definition length 
    public int Lq;
    
    //Quantization table element precision
    public int Pq;
    
    //Quantization table destination identifier
    public int Tq;
    
    //Quantization table element in zig zag order
    public int[] Qks;
    
    public QuantizationTableSpecification() {}
    
    public QuantizationTableSpecification(int[] header) {
        this.Lq = (header[0] << 8) + header[1];
        
        int pos = 2;
        
        //only 8 bit precision is supported
        //TODO: add assertion
        this.Pq = ((header[pos] & 0xf0) >>> 4) == 0 ? 8 : 16;
        this.Tq =  header[pos] & 0x0f;
        
        pos++;
        
        this.Qks = new int[64];
        for(int i=0; i<64; i++)
            this.Qks[i] = header[pos+i];
    }

    public int[] getQks() {
        return Qks;
    }
    
}
