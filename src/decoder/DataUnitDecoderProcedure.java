package decoder;

import java.io.IOException;
import java.util.List;

import markers.HuffmanTableSpecification;
import markers.HuffmanTableSpecificationsTransformer;
import markers.QuantizationTableSpecification;
import markers.HuffmanTableSpecificationsTransformer.DecodeTables;

public class DataUnitDecoderProcedure {

    private HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
    
    private DCDecodeProcedure dcDp = new DCDecodeProcedure();
    
    private ACDecodeProcedure acDp = new ACDecodeProcedure();
    
    //calculate zz
    public int[] decode(NextBitReader nbr, 
                        HuffmanTableSpecification dHt, 
                        HuffmanTableSpecification aHt, 
                        QuantizationTableSpecification qts) throws IOException 
    {
        //Decode DC coefficient
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
        //set DC coefficient
        zz[0] = val;
        
        return zz;
    }

    public HuffmanTableSpecificationsTransformer getHtst() {
        return htst;
    }

    public void setHtst(HuffmanTableSpecificationsTransformer htst) {
        this.htst = htst;
    }

    public DCDecodeProcedure getDcDp() {
        return dcDp;
    }

    public void setDcDp(DCDecodeProcedure dcDp) {
        this.dcDp = dcDp;
    }

    public ACDecodeProcedure getAcDp() {
        return acDp;
    }

    public void setAcDp(ACDecodeProcedure acDp) {
        this.acDp = acDp;
    }

}
