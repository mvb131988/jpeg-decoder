package poc;

/**
 * Same structure that BmpImageHeaderRaw, but with transformed values to base 10
 */
public class BmpFileHeader {
    //constant value, isn't changed
    public int signature;
    
    //size of the entire bmp file in bytes
    public long fileSize; 
    
    //constant value, isn't changed
    public int reserved1; 
    
    //constant value, isn't changed
    public int reserved2; 
    
    //position of the first byte of pixels data relative to the beginning of the file
    //use (54)10 as size of the header structure is constant and equal to 54
    public long imageDataOffset;
    
    //must be (40)16 (hex value)
    public long bitmapInfoHeaderSize;
    
    //image width in pixels
    //width ~ number of columns
    public long imageWidth;
    
    //image height in pixels
    //height ~ number of rows
    public long imageHeight;
    
    //constant value, isn't changed
    public int numberOfPlanes;
    
    //must be 24, isn't changed
    public int numberOfBitsPerPixel;
    
    //must be 0, isn't changed
    public long compressionType;
    
    //size of pixels data in bytes
    public long sizeOfImageData;
    
    //must be 3780 ppm, isn't changed
    public long horizontalResolution;
    
    //must be 3780 ppm, isn't changed
    public long verticalResolution;
    
    //must be 0, isn't changed
    public long numberOfColors;
    
    //must be 0, isn't changed
    public long numberOfImportantColors;
}
