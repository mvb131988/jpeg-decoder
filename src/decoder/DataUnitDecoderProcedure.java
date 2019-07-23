package decoder;

import java.io.IOException;

import main.HuffmanTableSpecification;
import main.HuffmanTableSpecificationsTransformer;
import util.BufferedReader;

public class DataUnitDecoderProcedure {

    private HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
    
    private DecodePreProcedure dpp = new DecodePreProcedure();
    
    private DCDecodeProcedure dcDp = new DCDecodeProcedure();
    
    private ACDecodeProcedure acDp = new ACDecodeProcedure();
    
    //TODO: return DC+AC
    public int[] decode(BufferedReader br, HuffmanTableSpecification dHt, HuffmanTableSpecification aHt) 
            throws IOException 
    {
        NextBitReader nbr = new NextBitReader(br);
        
        //Decode DC coefficient
        DecodeProcedureContext dpc0 = htst.transform(dHt);
        DecodePreProcedureContext dppc0 = dpp.decode(dpc0);
        int val = dcDp.decode(dppc0, nbr)[0];
        
        //Decode 63 AC coefficients
        DecodeProcedureContext dpc2 = htst.transform(aHt);
        DecodePreProcedureContext dppc2 = dpp.decode(dpc2);
        int[] zz = acDp.decode(dppc2, nbr);
        
        return zz;
    }
    
}
