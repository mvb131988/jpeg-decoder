package decoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import markers.HuffmanTableSpecification;
import markers.QuantizationTableSpecification;
import util.BufferedReader;

//TODO: Need to check:
//      1 Jpeg type - only DCT base is supported
//      2 pixel size - only 8 bit is supported 
//      3 no DNL Number of lines must be greater than 0

//Eliminate all FFE0 through FFEF

/**
 * Entry point for the decoding process
 */
public class DecoderControlProcedure {

    //source(jpeg) image reader
    private BufferedReader br;
    
    private HeadersDecoder hd = new HeadersDecoder();
    
    private FrameDecoderProcedure fdp = new FrameDecoderProcedure();
    
    private HuffmanTableSpecificationDecoderProcedure htsdp = new HuffmanTableSpecificationDecoderProcedure();
    
    private static Logger logger = LogManager.getRootLogger();
    
	private QuantizationTableSpecificationDecoderProcedure qtsdp = new QuantizationTableSpecificationDecoderProcedure();
    
    /**
     * 
     * @param br - BufferedReader associated with a given jpeg image
     * @throws IOException 
     */
    public DecoderControlProcedure(BufferedReader br) throws IOException {
        this.br = br;
    }
    
    public void decodeImage(DecoderContext dc) throws Exception {
        decodeImageInternally(dc);
        br.close();
    }
    
    private void decodeImageInternally(DecoderContext dc) throws Exception {
        //read SOI(start of image marker)
        int[] soi = new int[2]; soi[0] = br.next(); soi[1] = br.next();
        if(soi[0] != 0xff && soi[1] != 0xd8) throw new Exception("SOI not found");
        
        //lookup for start of frame SOF0 - Baseline DCT. Only this format is supported
        int[] marker = new int[2];  marker[0] = br.next(); marker[1] = br.next();
        while(!(marker[0] == 0xff && (marker[1] == 0xc0 || marker[1] == 0xc2))
            || endOfFile(marker[0], marker[1])) {
            //TODO: interpret markers
            
            if(hd.isAppMarker(marker)) hd.skipAppMarker(br);
            
            //DRI - restart interval
            if(hd.isRestartIntervalMarker(marker)) dc.restartInterval = hd.restartInterval(br);
            
            //DHT marker - Huffman tables
            if(marker[0] == 0xff && marker[1] == 0xc4) dc.htsList.addAll(decodeHuffmanTable());
            
            //DQT marker - Quantization tables
            if(marker[0] == 0xff && marker[1] == 0xdb) dc.qtsList.addAll(decodeQuantizationTable());
            
            marker[0] = marker[1]; marker[1] = br.next();
        }
        
        if (skip(marker[0], marker[1])) 
          throw new Exception("Progressive DCT-based mode of operation is not supported"); 
        
        fdp.decodeFrame(br, dc);
    }
    
    private List<HuffmanTableSpecification> decodeHuffmanTable() throws IOException {
        int[] htsSize0 = new int[2]; htsSize0[0] = br.next(); htsSize0[1] = br.next();
        int htsSize = (htsSize0[0] << 8) + htsSize0[1];
        
        //2 bytes of header marker are not counted in size, but 2 bytes of header size are
        int[] htsHeader = new int[htsSize];
        htsHeader[0] = htsSize0[0];
        htsHeader[1] = htsSize0[1];        
        for(int i=2; i<htsSize; i++) htsHeader[i] = br.next();
        
        List<HuffmanTableSpecification> htsList = htsdp.decode(htsHeader);
        return htsList;
    }
    
    private List<QuantizationTableSpecification> decodeQuantizationTable() throws IOException {
        int[] qtsSize0 = new int[2]; qtsSize0[0] = br.next(); qtsSize0[1] = br.next();
        int qtsSize = (qtsSize0[0] << 8) + qtsSize0[1];
        
        //2 bytes of header marker are not counted in size, but 2 bytes of header size are
        int[] qtsHeader = new int[qtsSize];
        qtsHeader[0] = qtsSize0[0];
        qtsHeader[1] = qtsSize0[1];        
        for(int i=2; i<qtsSize; i++) qtsHeader[i] = br.next();
        
        List<QuantizationTableSpecification> qtsList = qtsdp.decode(qtsHeader);
        return qtsList;
    }
    
    //TODO: compare with Integer.MIN_VALUE, see BufferedReader for more details
    private boolean endOfFile(int m0, int m1) {
        return false;
    }
    
    // Progressive DCT-based mode of operation is not supported
    private boolean skip(int marker0, int marker1) {
      if(marker0 == 0xff && marker1 == 0xc2) {
        logger.info("Progressive DCT-based mode of operation is not supported");
        return true;
      }
      return false;
    }
    
}
