package decoder;

import java.io.IOException;

import util.BufferedReader;

public class RestartIntervalDecoderProcedure {

    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    public void decodeRestartInterval(BufferedReader br, DecoderContext dc) throws IOException {
        //TODO: It's assumed that only one restart interval exist,
        //however needs to check on the images with several restart intervals
        
        DimensionsContext dimc = dc.dimensionsContext;
        int[] extXDataUnit = dimc.extXDataUnit;
        int[] extYDataUnit = dimc.extYDataUnit;
        
        int nX = extXDataUnit[0]/dc.frameHeader.Hs[0];
        int nY = extYDataUnit[0]/dc.frameHeader.Vs[0];
        
        int[][][][] rows = new int[nY][nX][][];
       
        for(int j=0; j<nY; j++)
            for(int i=0; i<nX; i++) {
                rows[j][i] = dp.decodeMCU(br, dc);
            }
        
        System.out.println("decoded x");
    }
    
}
