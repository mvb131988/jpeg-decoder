package decoder;

import java.io.IOException;

import markers.FrameHeader;
import util.BufferedReader;

public class RestartIntervalDecoderProcedure {

    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    public void decodeRestartInterval(BufferedReader br, DecoderContext dc) throws IOException {
        //Init PRED value of DC coefficient for each component
        //Preserve components order in scan header
        dc.initPredDC();
        
        //TODO: It's assumed that only one restart interval exist,
        //however needs to check on the images with several restart intervals
        
        DimensionsContext dimc = dc.dimensionsContext;
        int[] extXDataUnit = dimc.extXDataUnit;
        int[] extYDataUnit = dimc.extYDataUnit;
        
        //number of MCUs in one row
        int nX = extXDataUnit[0]/dc.frameHeader.Hs[0];
        //number of MCUs in one column
        int nY = extYDataUnit[0]/dc.frameHeader.Vs[0];
        
        int numberOfMcu = nX*nY;
        int[][][][] mcus = new int [numberOfMcu][][][];
        
        NextBitReader nbr = new NextBitReader(br);
        int iMcu = 1;
        for (int i = 0; i < mcus.length; i++) {
            if(i==200) {
                System.out.println("debug");
            }
            
            mcus[i] = dp.decodeMCU(nbr, dc);
            System.out.println(iMcu++);
        }
        
        System.out.println("=== END OF READ ===");
        
        flattenMCUs(mcus, dc);
    }
    
    /**
     * 
     * 
     * @return
     */
    private int[][][] flattenMCUs(int[][][][] mcus, DecoderContext dc) {
        
        //number of data units in a row per component
        int[] extXDataUnit = dc.dimensionsContext.extXDataUnit;
        //number of data units in a column per component
        int[] extYDataUnit = dc.dimensionsContext.extYDataUnit;
        //number of samples in a row per component 
        int[] xs = dc.dimensionsContext.finalExtXs;
        //number of samples in a column per component
        int[] ys = dc.dimensionsContext.finalExtYs;
        
        FrameHeader fh = dc.frameHeader;
        
        //MCU consists of data units from different image components.
        //Sizes contain number of data units in single MCU per image component.
        int[] sizes = new int[fh.Cs.length];
        //number of data units in the MCU
        int Nb = 0;
        for (int i = 0; i < sizes.length; i++) {sizes[i] = fh.Vs[i] * fh.Hs[i]; Nb += sizes[i];}
        
        return null;
        
        //TODO: Initialize ComponentBuilder for each component(set height, width in samples, height and 
        //width in data units per MCU)
        
        //disassemble each MCU in separate data units and pass data unit of i-s component to ComponentBuilder
        //of i-th component.
        
        //Disassembling of data units into samples and pixels allocation is done inside ComponentBuilder
    } 
    
}
