package decoder;

import java.util.List;

public class DecodePreProcedureContext {

    public int[] minCode;
    public int[] maxCode;
    public int[] valPtr;
    public List<Integer> huffVal;

    public DecodePreProcedureContext(int[] minCode, int[] maxCode, int[] valPtr,List<Integer> huffVal) {
        super();
        this.minCode = minCode;
        this.maxCode = maxCode;
        this.valPtr = valPtr;
        this.huffVal = huffVal;
    }

}
