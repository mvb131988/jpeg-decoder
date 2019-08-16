package decoder;

import java.io.IOException;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;

public class DCDecodeProcedure extends AbstractDecodeProcedure {
    
    public int[] decodeDc(DecodeTables dcDt, int[] huffVal, NextBitReader nbr) throws IOException {
        //huffVal
        int t = decode(dcDt, huffVal, nbr)[0];
        int diff = receive(t, nbr);
        diff = extend(diff, t);
       
        int[] diffArr = new int[] {diff};
        return diffArr;
    }
    
}
