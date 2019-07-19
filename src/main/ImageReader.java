package main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import decoder.DecodePreProcedure;
import decoder.DecodePreProcedureContext;
import decoder.ACDecodeProcedure;
import decoder.DCDecodeProcedure;
import decoder.DecodeProcedureContext;
import decoder.NextBitReader;
import util.BufferedReader;
import util.HeadersPrinter;

/**
 * Assumptions:
 * 1. Image consists of only one frame
 * 2. Image consists of only one scan
 * 3. Only SOF0 is accepted
 */
@Deprecated
public class ImageReader {

    private List<HuffmanTableSpecification> hts = new ArrayList<>();
    
    public void read() {
        
        Path path =  Paths.get("browny.jpg");
        
        try(InputStream input = Files.newInputStream(path, StandardOpenOption.READ)) {
            
            loadFrameHeader(input);
                    
        } catch(IOException e) {
            System.out.println(e);
        }
        
    }
    
    private void loadFrameHeader(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(is);
        HeadersPrinter hp = new HeadersPrinter();
        
        int[] header = new int[] {0xff, 0, 0, 0};
        
        for(;;) {
            int b = br.next();
            
            if(b == -1) {
                break;
            }
            
            if(b == 0xff) {
                header[1] = br.next();
                hp.add(header[1]);
                
                //if start of image marker(SOI)
                if(header[1] == 0xd8) continue;
                if(header[1] == 0xd9) break;
                                
                header[2] = br.next();
                header[3] = br.next();
                
                int frameSize = header[3] + (header[2] << 8);
                
                //System.out.println(frameSize);
                
                //+2 because frame header marker is not counted in frameSize
                int[] frameHeader = new int[frameSize+2];
                for(int i=0; i<4; i++) frameHeader[i] = header[i];
                
                //header[0], header[1], header[2], header[3] have been already read 
                for(int i=4; i<frameHeader.length; i++)
                    frameHeader[i] = br.next();
                
                //save frame header
                FrameHeader fh = null;
//                        FrameHeader.checkAndBuild(frameHeader);
                if(fh != null) fh.print();
                
                ScanHeader sh = ScanHeader.checkAndBuild(frameHeader);
                if(sh != null) {
                    sh.print();
                    readEncodedData(br, hts);
                }
                
                //huffman table
                HuffmanTableSpecification ht = HuffmanTableSpecification.checkAndBuild(frameHeader);
                if(ht != null) hts.add(ht);
            }
            
        }
        
        hp.print();
    }
    
    
    private void readEncodedData(BufferedReader br, List<HuffmanTableSpecification> hts) throws IOException {
        NextBitReader nbr = new NextBitReader(br);
        DecodePreProcedure dpp = new DecodePreProcedure();
        HuffmanTableSpecificationsTransformer htst = new HuffmanTableSpecificationsTransformer();
        
        DecodeProcedureContext dpc0 = htst.transform(hts.get(0));
        DecodePreProcedureContext dppc0 = dpp.decode(dpc0);
        DCDecodeProcedure dp = new DCDecodeProcedure();
        int val = dp.decode(dppc0, nbr)[0];
        
        DecodeProcedureContext dpc2 = htst.transform(hts.get(2));
        DecodePreProcedureContext dppc2 = dpp.decode(dpc2);
        ACDecodeProcedure acDp = new ACDecodeProcedure();
        int[] zz = acDp.decode(dppc2, nbr);
        
        DecodeProcedureContext dpc1 = htst.transform(hts.get(1));
        DecodePreProcedureContext dppc1 = dpp.decode(dpc1);
        val = dp.decode(dppc1, nbr)[0];
        
        DecodeProcedureContext dpc3 = htst.transform(hts.get(3));
        DecodePreProcedureContext dppc3 = dpp.decode(dpc3);
        zz = acDp.decode(dppc3, nbr);
        
        val = dp.decode(dppc1, nbr)[0];
        zz = acDp.decode(dppc3, nbr);
        
//        NextBitReader nbr = new NextBitReader(br);
//        
//        System.out.println("first byte");
//        for(int i=0; i<8; i++) {
//            System.out.print(nbr.nextBit());
//        }
//        System.out.println();
//        System.out.println("==========");
    }
    

    public List<HuffmanTableSpecification> getHts() {
        return hts;
    }

    public void setHts(List<HuffmanTableSpecification> hts) {
        this.hts = hts;
    }
    
//    /**
//     * Image consists of only one frame, and hence only one frame header is present in the image.
//     * 
//     * Frame header starts with two bytes: [ff c0], followed by two bytes of the header length.
//     *  
//     * @param is
//     * @throws IOException
//     */
//    private void loadFrameHeader(InputStream is) throws IOException {
//        for(;;) {
//            byte[] buffer = new byte[16];
//            int length = is.read(buffer, 0, buffer.length);
//            
//            int ffIndex = ffIndex(buffer, length);
//            
//            if(ffIndex != -1) {
//                // 4 first bytes of frame header
//                int[] frameHeader4 = new int[] {0xff, 0, 0, 0};
//                //missed number of bytes
//                int mnb = readFF4Bytes(ffIndex, frameHeader4, buffer, length);
//                if(mnb > 0) {
//                    for(int i=0; i<mnb; i++) {
//                        frameHeader4[4-mnb+i] = is.read();
//                    }
//                }
//            }
//        }
//    }
//    
//    /**
//     * Searches for ff byte, returns its index if it's present or -1 otherwise.
//     * 
//     * Buffer length(buffer.length) could differ from actual buffer length(length).
//     * This is the case when end of file is reached. 
//     * 
//     * @return
//     */
//    private int ffIndex(byte[] buffer, int length) {
//        int ffIndex = -1;
//        for(int i = 0; i<length; i++)
//            if((buffer[i] & 0xff) == 0xff) {ffIndex = i; break;}
//        return ffIndex;
//    }
// 
//    /**
//     * ff bytes is a contiguous subarray(header4) of buffer of size 4, where
//     * 
//     * header4[0] == 0xff, header4[0] - type of header, header4[1] and header4[2] - 
//     * header size in bytes.
//     * 
//     * Starting ff follower byte, try to read next 3 bytes and saves its into frameHeader4.  
//     * Returns number of bytes missed in buffer.
//     * 
//     * @return
//     */
//    private int readFF4Bytes(int ffIndex, int[] frameHeader4, byte[] buffer, int bufferLength) {
//        //save up to 3 bytes, that follow ff start byte
//        int index0 = ffIndex+1;
//        int frameHeader4Index = 1;
//        while(index0 < bufferLength && frameHeader4Index < frameHeader4.length) 
//            frameHeader4[frameHeader4Index++] = buffer[index0++];
//        
//        //number of bytes, read from the buffer including ff
//        int diff = index0 - ffIndex;
//        
//        return 4-diff;
//    }
}
