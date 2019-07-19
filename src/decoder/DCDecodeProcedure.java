package decoder;

import java.io.IOException;

public class DCDecodeProcedure extends AbstractDecodeProcedure {
    
    public int[] decode(DecodePreProcedureContext c, NextBitReader nbr) throws IOException {
        //huffVal
        int t = super.decode(c, nbr)[0];
        int diff = receive(t, nbr);
        diff = extend(diff, t);
       
        int[] diffArr = new int[] {diff};
        return diffArr;
    }
    
}
