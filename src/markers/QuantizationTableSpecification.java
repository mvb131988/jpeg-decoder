package markers;

public class QuantizationTableSpecification {
    
    //Quantization table element precision
    public int Pq;
    
    //Quantization table destination identifier
    public int Tq;
    
    //Quantization table element in zig zag order
    public int[] Qks;
    
    public QuantizationTableSpecification() {}
    
    public QuantizationTableSpecification(int pq, int tq, int[] qks) {
		super();
		Pq = pq;
		Tq = tq;
		Qks = qks;
	}

    public int[] getQks() {
        return Qks;
    }
    
}
