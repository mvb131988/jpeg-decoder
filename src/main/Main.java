package main;

import java.io.IOException;
import java.util.List;

import decoder.DecodePreProcedure;
import decoder.DecodeProcedureContext;
import decoder.DecoderControlProcedure;

public class Main {
    
    public static void main(String[] args) throws Exception {
        DecoderControlProcedure dcp = new DecoderControlProcedure("browny.jpg");
        dcp.decodeImage();
        
        ImageReader ir = new ImageReader();
        ir.read();
        List<HuffmanTableSpecification> htss = ir.getHts();
        
        HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
        DecodeProcedureContext dpc = htst.transform(htss.get(0));
        
        DecodePreProcedure dpp = new DecodePreProcedure();
        dpp.decode(dpc);
    }
}
