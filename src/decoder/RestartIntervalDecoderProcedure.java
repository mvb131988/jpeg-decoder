package decoder;

import util.BufferedReader;

public class RestartIntervalDecoderProcedure {

    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    public void decodeRestartInterval(BufferedReader br, DecoderContext dc) {
        //TODO: It's assumed that only one restart interval exist,
        //however needs to check on the images with several restart intervals
        
        dp.decodeMCU(br, dc);
    }
    
}
