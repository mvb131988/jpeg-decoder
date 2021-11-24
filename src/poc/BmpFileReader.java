package poc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BmpFileReader {

    private String strPath;
  
    public BmpFileReader(String p) {
      strPath = p;
    }
    
    public BmpFile read() {
        BmpFile bmpFile = null;
        
        Path path =  Paths.get(this.strPath);
        
        try(InputStream input = Files.newInputStream(path, StandardOpenOption.READ)) {
            
            byte[] header0 = new byte[54];
            input.read(header0, 0, 54);
            BmpFileHeaderRaw rawHeader = new BmpFileHeaderRaw(header0);
            BmpFileHeader header = transform(rawHeader);
            
            int height = (int)header.imageHeight;
            int widthInPixels = (int)header.imageWidth;
            int widthInBytes = (int)header.imageWidth*3;
            int widthInBytesWithPadding = widthWithPadding((int)header.imageWidth*3);
            BmpFile.Pixel[][] pCanvas = new BmpFile.Pixel[height][];
            for(int z = 0; z<height; z++) {
                //read single row
                byte[] row  = new byte[widthInBytes];
                input.read(row, 0, widthInBytes);
                //read padding
                for(int z0=0; z0<widthInBytesWithPadding-widthInBytes; z0++) input.read();
                
                BmpFile.Pixel[] pRow = new BmpFile.Pixel[widthInPixels];
                //transform
                for(int i=0, j=0; i<widthInBytes; i=i+3, j++) {
                    pRow[j] = new BmpFile.Pixel(row[i+2] & 0xFF, row[i+1] & 0xFF, row[i] & 0xFF);
                }
                
                pCanvas[z] = pRow;
            }
            
            bmpFile = new BmpFile(header, pCanvas);
            
            reverse(pCanvas);
                    
        } catch(IOException e) {
            System.out.println(e);
        }
        
        return bmpFile;
    }
    
    private BmpFileHeader transform(BmpFileHeaderRaw rawHeader) {
        BmpFileHeader header = new BmpFileHeader();
        header.signature = littleEndian2Bytes(rawHeader.signature);
        header.fileSize = littleEndian4Bytes(rawHeader.fileSize);
        header.reserved1 = littleEndian2Bytes(rawHeader.reserved1);
        header.reserved2 = littleEndian2Bytes(rawHeader.reserved2);
        header.imageDataOffset = littleEndian4Bytes(rawHeader.imageDataOffset);
        header.bitmapInfoHeaderSize = littleEndian4Bytes(rawHeader.bitmapInfoHeaderSize);
        header.imageWidth = littleEndian4Bytes(rawHeader.imageWidth);
        header.imageHeight = littleEndian4Bytes(rawHeader.imageHeight);
        header.numberOfPlanes = littleEndian2Bytes(rawHeader.numberOfPlanes);
        header.numberOfBitsPerPixel = littleEndian2Bytes(rawHeader.numberOfBitsPerPixel);
        header.compressionType = littleEndian4Bytes(rawHeader.compressionType);
        header.sizeOfImageData = littleEndian4Bytes(rawHeader.sizeOfImageData);
        header.horizontalResolution = littleEndian4Bytes(rawHeader.horizontalResolution);
        header.verticalResolution = littleEndian4Bytes(rawHeader.verticalResolution);
        header.numberOfColors = littleEndian4Bytes(rawHeader.numberOfColors);
        header.numberOfImportantColors = littleEndian4Bytes(rawHeader.numberOfImportantColors);
        return header;
    }
    
    private long littleEndian4Bytes(int[] bytes4) {
        long l =  (long) bytes4[0] + 
                 ((long) bytes4[1] << 8) + 
                 ((long) bytes4[2] << 16) + 
                 ((long) bytes4[3] << 24);
        return l;
    }
    
    private int littleEndian2Bytes(int[] bytes2) {
        int i =  bytes2[0] + 
                (bytes2[1] << 8); 
        return i;
    }
    
    private void reverse(BmpFile.Pixel[][] pCanvas) {
        for(int i=0; i<pCanvas.length/2; i++) {
            BmpFile.Pixel[] tmp = pCanvas[i];
            pCanvas[i] = pCanvas[pCanvas.length-1-i];
            pCanvas[pCanvas.length-1-i] = tmp;
        }
    }
    
    /**
     * Bmp requirement:
     * Image width must be divided by 4 without reminder. If exists reminder width must be enlarged to minimum value
     * greater than original width, that is divide by 4 without reminder
     * 
     * @return
     */
    private int widthWithPadding(int width) {
        int newWidth = width;
        while(newWidth%4 != 0) newWidth++;
        return newWidth;
    }
    
}
