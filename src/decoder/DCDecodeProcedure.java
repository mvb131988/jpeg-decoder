package decoder;

import java.io.IOException;

import markers.HuffmanTableSpecificationsTransformer.DecodeTables;

public class DCDecodeProcedure extends AbstractDecodeProcedure {
    
    public int[] decodeDc(DecodeTables dcDt, int[] huffVal, NextBitReader nbr, MCUCalculationDataHolder holder) throws IOException {
        //huffVal
        int t = decode(dcDt, huffVal, nbr, holder)[0];
        int diff = receive(t, nbr);
        diff = extend(diff, t);
       
        int[] diffArr = holder.diffArr;
        diffArr[0] = diff;
        return diffArr;
    }
    
}
