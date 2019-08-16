package debug;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BmpFileWriter {

    public static final int IMAGE_DATA_OFFSET = 54;
    
    public void write(Pixel[][] pixels) throws IOException {
        BmpFileHeader h = new BmpFileHeader();
        h.signature = 19778;
        h.fileSize = pixels.length*pixels[0].length + IMAGE_DATA_OFFSET; 
        h.reserved1 = 0; 
        h.reserved2 = 0;
        h.imageDataOffset = IMAGE_DATA_OFFSET;
        h.bitmapInfoHeaderSize = 40;
        h.imageWidth = pixels[0].length;
        h.imageHeight = pixels.length;
        h.numberOfPlanes = 1;
        h.numberOfBitsPerPixel = 24;
        h.compressionType = 0;
        h.sizeOfImageData = pixels.length*pixels[0].length;
        h.horizontalResolution = 16620;
        h.verticalResolution = 16620;
        h.numberOfColors = 0;
        h.numberOfImportantColors = 0;
        
        BmpFile f = new BmpFile(h, pixels);
        
        BmpFileTransformer t = new BmpFileTransformer();
        f = t.transform(f);
        
        write(f);
    }
    
    public void write(BmpFile bmpFile) throws IOException {
        Path path =  Paths.get("bmp_img_out.bmp");
        try(OutputStream os = Files.newOutputStream(path)) {
            os.write(convert(bmpFile.header));
            os.write(bmpFile.pixelsToBytes());
        }
        
    }
    
    private byte[] convert(BmpFileHeader h) {
        BmpFileHeaderRaw h0 = new BmpFileHeaderRaw(h);
        byte[] header = h0.toByteArray();
        return header;
    }
    
}
