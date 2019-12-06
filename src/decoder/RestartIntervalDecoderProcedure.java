package decoder;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import markers.Image;
import util.BufferedReader;
import util.FileSystemMCUWriter;

public class RestartIntervalDecoderProcedure {

	private static Logger logger = LogManager.getRootLogger();
	
    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    private MCUsFlattener msf = new MCUsFlattener();
    
    public Image decodeRestartInterval(BufferedReader br, DecoderContext dc) throws Exception {
        //Init PRED value of DC coefficient for each component
        //Preserve components order in scan header
        dc.initPredDC();
        
        DimensionsContext dimc = dc.dimensionsContext;
        int[] extXDataUnit = dimc.extXDataUnit;
        int[] extYDataUnit = dimc.extYDataUnit;
        
        //number of MCUs in one row
        int nX = extXDataUnit[0]/dc.frameHeader.Hs[0];
        //number of MCUs in one column
        int nY = extYDataUnit[0]/dc.frameHeader.Vs[0];
        
        int numberOfMcu = nX*nY;
        
        logger.info((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
        
        try(FileSystemMCUWriter fsmw = new FileSystemMCUWriter()) {
        	decodeRestartIntervalInternally(br, dc, fsmw, numberOfMcu);
        }
        
        logger.info((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
        
        return msf.flattenMCUs(numberOfMcu, dc);
    }

	private void decodeRestartIntervalInternally(BufferedReader br, 
												 DecoderContext dc, 
												 FileSystemMCUWriter fsmw, 
												 int numberOfMcu) throws IOException {
		NextBitReader nbr = new NextBitReader(br);
		int i = 0;
		while (i < numberOfMcu) {
			// check restart interval marker
			int b1 = br.next();
			int b2 = br.next();

			if (!(b1 == 0xff && (b2 == 0xd0 || 
								 b2 == 0xd1 || 
								 b2 == 0xd2 || 
								 b2 == 0xd3 || 
								 b2 == 0xd4 || 
								 b2 == 0xd5 || 
								 b2 == 0xd6 || 
								 b2 == 0xd7))) {
				br.pushBack(b1);
				br.pushBack(b2);

				int[][][] mcu = dp.decodeMCU(nbr, dc);
				fsmw.write(mcu);
				i++;
			} else {
				// refresh actions when restart marker is met
				dc.initPredDC();
				nbr = new NextBitReader(br);
			}
		}
	}
    
    public MCUDecoderProcedure getDp() {
        return dp;
    }

    public void setDp(MCUDecoderProcedure dp) {
        this.dp = dp;
    }

	public MCUsFlattener getMsf() {
		return msf;
	}

	public void setMsf(MCUsFlattener msf) {
		this.msf = msf;
	}
    
}
