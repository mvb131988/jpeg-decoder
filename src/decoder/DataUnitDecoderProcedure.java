package decoder;

import java.io.IOException;
import java.util.List;

import markers.HuffmanTableSpecification;
import markers.HuffmanTableSpecificationsTransformer;
import markers.HuffmanTableSpecificationsTransformer.DecodeTables;
import util.BufferedReader;

public class DataUnitDecoderProcedure {

    private HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
    
    private DecodePreProcedure dpp = new DecodePreProcedure();
    
    private DCDecodeProcedure dcDp = new DCDecodeProcedure();
    
    private ACDecodeProcedure acDp = new ACDecodeProcedure();
    
    //TODO: return DC+AC
    public int[] decode(NextBitReader nbr, HuffmanTableSpecification dHt, HuffmanTableSpecification aHt) 
            throws IOException 
    {
        
        //Decode DC coefficient
//        DecodeProcedureContext dpc0 = htst.transform(dHt);
//        DecodePreProcedureContext dppc0 = dpp.decode(dpc0);
        
        //number of codes of each size
        int[] dcBits = dHt.getLis();
        List<Integer> dcHuffSize = htst.huffSize(dcBits);
        List<Integer> dcHuffCode = htst.huffCode(dcHuffSize);
        DecodeTables dcDt = htst.decodeTables(dcBits, dcHuffCode);
        int[] dcHuffVal = dHt.vij;
        int val = dcDp.decodeDc(dcDt, dcHuffVal, nbr)[0];
        
        //Decode 63 AC coefficients
        int[] acBits = aHt.getLis();
        List<Integer> acHuffSize = htst.huffSize(acBits);
        List<Integer> acHuffCode = htst.huffCode(acHuffSize);
        DecodeTables acDt = htst.decodeTables(acBits, acHuffCode);
        int[] acHuffVal = aHt.vij;
        
        int[] zz = acDp.decodeAc(acDt, acHuffVal, nbr);
        
        return zz;
    }
    
}
