package decoder;

import java.io.IOException;

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
            mcus[i] = dp.decodeMCU(nbr, dc);
            System.out.println(iMcu++);
        }
        
        System.out.println("=== END OF READ ===");
    }
    
}
