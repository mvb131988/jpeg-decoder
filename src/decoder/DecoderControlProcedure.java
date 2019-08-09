package decoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import markers.HuffmanTableSpecification;
import markers.QuantizationTableSpecification;
import util.BufferedReader;

/**
 * Entry point for the decoding process
 */
public class DecoderControlProcedure {

    //source(jpeg) image reader
    private BufferedReader br;
    
    private FrameDecoderProcedure fdp = new FrameDecoderProcedure();
    
    /**
     * 
     * @param sPath - path to source(jpeg) image
     * @throws IOException 
     */
    public DecoderControlProcedure(String sPath) throws IOException {
        Path path = Paths.get(sPath);
        InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
        br = new BufferedReader(is);
    }
    
    public void decodeImage() throws Exception {
        decodeImageInternally(new DecoderContext());
        br.close();
    }
    
    private void decodeImageInternally(DecoderContext dc) throws Exception {
        //read SOI(start of image marker)
        int[] soi = new int[2]; soi[0] = br.next(); soi[1] = br.next();
        if(soi[0] != 0xff && soi[1] != 0xd8) throw new Exception("SOI not found");
        
        //lookup for start of frame SOF0 - Baseline DCT. Only this format is supported
        int[] marker = new int[2];  marker[0] = br.next(); marker[1] = br.next();
        while(!(marker[0] == 0xff && marker[1] == 0xc0) || endOfFile(marker[0], marker[1])) {
            //TODO: interpret markers
            
            //DHT marker - Huffman tables
            if(marker[0] == 0xff && marker[1] == 0xc4) dc.htsList.add(decodeHuffmanTable());
            
            //DQT marker - Quantization tables
            if(marker[0] == 0xff && marker[1] == 0xdb) dc.qtsList.add(decodeQuantizationTable());
            
            marker[0] = marker[1]; marker[1] = br.next();
        }
        
        fdp.decodeFrame(br, dc);
    }
    
    private HuffmanTableSpecification decodeHuffmanTable() throws IOException {
        int[] htsSize0 = new int[2]; htsSize0[0] = br.next(); htsSize0[1] = br.next();
        int htsSize = (htsSize0[0] << 8) + htsSize0[1];
        
        //2 bytes of header marker are not counted in size, but 2 bytes of header size are
        int[] htsHeader = new int[htsSize];
        htsHeader[0] = htsSize0[0];
        htsHeader[1] = htsSize0[1];        
        for(int i=2; i<htsSize; i++) htsHeader[i] = br.next();
        
        HuffmanTableSpecification hts = new HuffmanTableSpecification(htsHeader);
        return hts;
    }
    
    private QuantizationTableSpecification decodeQuantizationTable() throws IOException {
        int[] qtsSize0 = new int[2]; qtsSize0[0] = br.next(); qtsSize0[1] = br.next();
        int qtsSize = (qtsSize0[0] << 8) + qtsSize0[1];
        
        //2 bytes of header marker are not counted in size, but 2 bytes of header size are
        int[] qtsHeader = new int[qtsSize];
        qtsHeader[0] = qtsSize0[0];
        qtsHeader[1] = qtsSize0[1];        
        for(int i=2; i<qtsSize; i++) qtsHeader[i] = br.next();
        
        QuantizationTableSpecification qts = new QuantizationTableSpecification(qtsHeader);
        return qts;
    }
    
    //TODO: compare with Integer.MIN_VALUE, see BufferedReader for more details
    private boolean endOfFile(int m0, int m1) {
        return false;
    }
    
}
