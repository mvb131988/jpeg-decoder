package decoder;

import java.io.IOException;
import java.util.List;

import markers.FrameHeader;
import markers.HuffmanTableSelector;
import markers.HuffmanTableSpecification;
import markers.ScanHeader;

public class MCUDecoderProcedure {

    private HuffmanTableSelector huffmanTableSelector = new HuffmanTableSelector();
    
    private DataUnitDecoderProcedure dataUnitDecoderProcedure = new DataUnitDecoderProcedure();
    
    public int[][] decodeMCU(NextBitReader nbr, DecoderContext dc) throws IOException {
        FrameHeader fh = dc.frameHeader;
        ScanHeader sh = dc.scanHeader;
        List<HuffmanTableSpecification> htsList = dc.htsList;
        int[] predDCs = dc.predDcs;
        
        //MCU consists of data units from different image components.
        //Sizes contain number of data units in single MCU per image component.
        int[] sizes = new int[fh.Cs.length];
        //number of data units in the MCU
        int Nb = 0;
        for (int i = 0; i < sizes.length; i++) {sizes[i] = fh.Vs[i] * fh.Hs[i]; Nb += sizes[i];}
        
        int[][] res = new int[Nb][];
        int resI = 0;
        for(int i=0; i<sizes.length; i++)
            while(sizes[i] > 0) {
                res[resI++] = decodeDataUnit(nbr, 
                                             huffmanTableSelector.select(htsList, 0, sh.Td[i]), 
                                             huffmanTableSelector.select(htsList, 1, sh.Ta[i]));
                res[resI-1][0] += predDCs[i];
                predDCs[i] = res[resI-1][0];
                sizes[i]--;
            }
        
        return res;
    }
    
    /**
     * 
     * @param br 
     * @param dHt - Huffman table for DC coefficients
     * @param aHt - Huffman table for AC coefficients
     * @throws IOException 
     */
    private int[] decodeDataUnit(NextBitReader nbr, HuffmanTableSpecification dHt, HuffmanTableSpecification aHt) 
            throws IOException 
    {
        int[] zz = dataUnitDecoderProcedure.decode(nbr, dHt, aHt);
        return zz;
    }
    
}
