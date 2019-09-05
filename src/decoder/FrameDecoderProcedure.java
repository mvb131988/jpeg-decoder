package decoder;

import java.io.IOException;

import markers.FrameHeader;
import markers.HuffmanTableSpecification;
import markers.Image;
import markers.QuantizationTableSpecification;
import util.BufferedReader;

public class FrameDecoderProcedure {
    
    private ScanDecoderProcedure sdp = new ScanDecoderProcedure();
    
    private DimensionsCalculator dimensionsCalculator = new DimensionsCalculator();
    
    private HeadersDecoder hd = new HeadersDecoder();
    
    /**
     * 
     * @param br - is set to the first frame header bit(frame header marker already processed) 
     * @throws IOException 
     */
    public Image decodeFrame(BufferedReader br, DecoderContext dc) throws IOException {
        int[] fs = new int[2]; fs[0] = br.next(); fs[1] = br.next();
        int frameSize = (fs[0] << 8) + fs[1];
        
        //2 bytes of frame header marker are not counted in frameSize, but 2 bytes of frame header size are
        int[] frameHeader = new int[frameSize];
        frameHeader[0] = fs[0];
        frameHeader[1] = fs[1];        
        for(int i=2; i<frameSize; i++) frameHeader[i] = br.next();
        
        FrameHeader fh = new FrameHeader(frameHeader);
        dc.frameHeader = fh;
        dc.dimensionsContext = dimensionsCalculator.calculate(fh);
        
        //lookup for start of scan SOS. Expected only image that consists of one scan. 
        //TODO - check number of scans for baseline DCT
        int[] marker = new int[2];  marker[0] = br.next(); marker[1] = br.next();
        while(!(marker[0] == 0xff && marker[1] == 0xda) || endOfFile(marker[0], marker[1])) {
            //TODO: interpret markers
            
            if(hd.isAppMarker(marker)) hd.skipAppMarker(br);
            
            //DRI - restart interval
            if(hd.isRestartIntervalMarker(marker)) dc.restartInterval = hd.restartInterval(br);
            
            //DHT marker - Huffman tables
            if(marker[0] == 0xff && marker[1] == 0xc4) dc.htsList.add(decodeHuffmanTable(br));
            
            //DQT marker - Quantization tables
            if(marker[0] == 0xff && marker[1] == 0xdb) dc.qtsList.add(decodeQuantizationTable(br));
            
            marker[0] = marker[1]; marker[1] = br.next();
        }

        return sdp.decodeScan(br, dc);
        
        //TODO: check for EOI(end of image marker)
    }
    
    private HuffmanTableSpecification decodeHuffmanTable(BufferedReader br) throws IOException {
        int[] htsSize0 = new int[2]; htsSize0[0] = br.next(); htsSize0[1] = br.next();
        int htsSize = (htsSize0[0] << 8) + htsSize0[1];
        
        //2 bytes of frame header marker are not counted in frameSize, but 2 bytes of frame header size are
        int[] htsHeader = new int[htsSize];
        htsHeader[0] = htsSize0[0];
        htsHeader[1] = htsSize0[1];        
        for(int i=2; i<htsSize; i++) htsHeader[i] = br.next();
        
        HuffmanTableSpecification hts = new HuffmanTableSpecification(htsHeader);
        return hts;
    }
    
    private QuantizationTableSpecification decodeQuantizationTable(BufferedReader br) throws IOException {
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

    public ScanDecoderProcedure getSdp() {
        return sdp;
    }

    public void setSdp(ScanDecoderProcedure sdp) {
        this.sdp = sdp;
    }
    
}
