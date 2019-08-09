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
    
    private DataUnitDequantizationProcedure dudp = new DataUnitDequantizationProcedure();
    
    //calculate zz
    public int[][] decode(NextBitReader nbr, 
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
        
        //zz coefficients in original order
        int[][] orderedZz = inverseZigZag(zz);
        dudp.dequantize(orderedZz, inverseZigZag(qts.getQks()));
        
        return orderedZz;
    }
    
    public static void main(String[] args) {
        new DataUnitDecoderProcedure().inverseZigZag(null);
    }
    
    /**
     * Transforms decoded AC/DC coefficients represented as zig zag sequence into
     * two dimensional array of quantized samples(samples original position).
     * 
     * @param zz
     * @return
     */
    private int[][] inverseZigZag(int[] zz) {
        int[][] temp = new int[8][8];
        
        int i=0, j=0;
        boolean isUpward = true;
        
        for(int k=0; k<64; k++) {
            temp[i][j] = zz[k];
            
            if(!isUpward && j==0 && i!=7) {i++; isUpward=true; continue;}
            if(!isUpward && i==7) {j++; isUpward=true; continue;}
            if(!isUpward) {i++; j--; continue;}
            
            if(isUpward && j==7) {i++; isUpward=false; continue;}
            if(isUpward && i==0) {j++; isUpward=false; continue;}
            if(isUpward) {i--; j++; continue;}
        }
        
        return temp;
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

    public DataUnitDequantizationProcedure getDudp() {
        return dudp;
    }

    public void setDudp(DataUnitDequantizationProcedure dudp) {
        this.dudp = dudp;
    }
    
}
