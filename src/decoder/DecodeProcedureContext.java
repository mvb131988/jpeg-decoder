package decoder;

import java.util.List;

public class DecodeProcedureContext {

    public List<Integer> huffVal;
    public List<Integer> huffSize;
    public List<Integer> huffCode;
    public int[] bits;

    public DecodeProcedureContext(List<Integer> huffVal, List<Integer> huffSize, List<Integer> huffCode, int[] bits) {
        super();
        this.huffVal = huffVal;
        this.huffSize = huffSize;
        this.huffCode = huffCode;
        this.bits = bits;
    }

}
