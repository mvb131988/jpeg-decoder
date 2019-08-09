package decoder;

import java.io.IOException;
import java.util.List;

import markers.FrameHeader;
import markers.HuffmanTableSelector;
import markers.HuffmanTableSpecification;
import markers.QuantizationTableSelector;
import markers.QuantizationTableSpecification;
import markers.ScanHeader;

public class MCUDecoderProcedure {

    private HuffmanTableSelector huffmanTableSelector = new HuffmanTableSelector();
    
    private DataUnitDecoderProcedure dataUnitDecoderProcedure = new DataUnitDecoderProcedure();
    
    private QuantizationTableSelector quantizationTableSelector = new QuantizationTableSelector();
    
    /**
     * 
     * Returns MCU that looks like(explained by example):
     * There are 3 components of the following sizes:
     * (1) component 1 contains 2 rows and 2 columns
     * (2) component 2 contains 1 rows and 1 columns
     * (3) component 3 contains 1 rows and 1 columns
     * 
     * Output has 6 data units: 4 for component 1, 1 for component 2, 1 for component 3
     *
     *           component1        component2  component3
     * Ordering: du1,du1,du1,du1   du2,du2     du3,du3  
     * 
     * or
     * 
     * res[0] = du1
     * ....
     * res[5] = du3
     * 
     * where du1, du2, du3 are two dimensional arrays (size 8x8) that contains DC/AC coefficients in
     * their original order
     * 
     * 
     * @param nbr
     * @param dc
     * @return
     * @throws IOException
     */
    public int[][][] decodeMCU(NextBitReader nbr, DecoderContext dc) throws IOException {
        FrameHeader fh = dc.frameHeader;
        ScanHeader sh = dc.scanHeader;
        List<HuffmanTableSpecification> htsList = dc.htsList;
        List<QuantizationTableSpecification> qtsList = dc.qtsList;
        int[] predDCs = dc.predDcs;
        
        //MCU consists of data units from different image components.
        //Sizes contain number of data units in single MCU per image component.
        int[] sizes = new int[fh.Cs.length];
        //number of data units in the MCU
        int Nb = 0;
        for (int i = 0; i < sizes.length; i++) {sizes[i] = fh.Vs[i] * fh.Hs[i]; Nb += sizes[i];}
        
        int[][][] res = new int[Nb][][];
        int resI = 0;
        for(int i=0; i<sizes.length; i++)
            while(sizes[i] > 0) {
                res[resI++] = decodeDataUnit(nbr, 
                                             huffmanTableSelector.select(htsList, 0, sh.Td[i]), 
                                             huffmanTableSelector.select(htsList, 1, sh.Ta[i]),
                                             quantizationTableSelector.select(qtsList, sh.Td[i]));
                res[resI-1][0][0] += predDCs[i];
                predDCs[i] = res[resI-1][0][0];
                sizes[i]--;
            }
        
        return res;
    }
    
    /**
     * 
     * @param dHt - Huffman table for DC coefficients
     * @param aHt - Huffman table for AC coefficients
     * @param qts 
     * @param br 
     * @throws IOException 
     */
    private int[][] decodeDataUnit(NextBitReader nbr, 
                                   HuffmanTableSpecification dHt, 
                                   HuffmanTableSpecification aHt, 
                                   QuantizationTableSpecification qts) throws IOException 
    {
        int[][] zz = dataUnitDecoderProcedure.decode(nbr, dHt, aHt, qts);
        return zz;
    }
    
}
