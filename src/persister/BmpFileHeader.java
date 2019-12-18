package persister;

import decoder.DecoderContext;

/**
 * Same structure that BmpImageHeaderRaw, but with transformed values to base 10
 */
public class BmpFileHeader {
	public static final int IMAGE_DATA_OFFSET = 54;

	// constant value, isn't changed
	public int signature;

	// size of the entire bmp file in bytes
	public long fileSize;

	// constant value, isn't changed
	public int reserved1;

	// constant value, isn't changed
	public int reserved2;

	// position of the first byte of pixels data relative to the beginning of the
	// file
	// use (54)10 as size of the header structure is constant and equal to 54
	public long imageDataOffset;

	// must be (40)16 (hex value)
	public long bitmapInfoHeaderSize;

	// image width in pixels
	// width ~ number of columns
	public long imageWidth;

	// image height in pixels
	// height ~ number of rows
	public long imageHeight;

	// constant value, isn't changed
	public int numberOfPlanes;

	// must be 24, isn't changed
	public int numberOfBitsPerPixel;

	// must be 0, isn't changed
	public long compressionType;

	// size of pixels data in bytes
	public long sizeOfImageData;

	// must be 3780 ppm, isn't changed
	public long horizontalResolution;

	// must be 3780 ppm, isn't changed
	public long verticalResolution;

	// must be 0, isn't changed
	public long numberOfColors;

	// must be 0, isn't changed
	public long numberOfImportantColors;

	public BmpFileHeader(Pixel[][] pixels) {
		this.signature = 19778;
		this.fileSize = pixels.length * pixels[0].length + IMAGE_DATA_OFFSET;
		this.reserved1 = 0;
		this.reserved2 = 0;
		this.imageDataOffset = IMAGE_DATA_OFFSET;
		this.bitmapInfoHeaderSize = 40;
		this.imageWidth = pixels[0].length;
		this.imageHeight = pixels.length;
		this.numberOfPlanes = 1;
		this.numberOfBitsPerPixel = 24;
		this.compressionType = 0;
		this.sizeOfImageData = pixels.length * pixels[0].length;
		this.horizontalResolution = 16620;
		this.verticalResolution = 16620;
		this.numberOfColors = 0;
		this.numberOfImportantColors = 0;
	}
	
	public BmpFileHeader(DecoderContext dc) {
		int height = dc.frameHeader.Y;
		int width = dc.frameHeader.X;
		
		this.signature = 19778;
		this.fileSize = height * width + IMAGE_DATA_OFFSET;
		this.reserved1 = 0;
		this.reserved2 = 0;
		this.imageDataOffset = IMAGE_DATA_OFFSET;
		this.bitmapInfoHeaderSize = 40;
		this.imageWidth = width;
		this.imageHeight = height;
		this.numberOfPlanes = 1;
		this.numberOfBitsPerPixel = 24;
		this.compressionType = 0;
		this.sizeOfImageData = height * width;
		this.horizontalResolution = 16620;
		this.verticalResolution = 16620;
		this.numberOfColors = 0;
		this.numberOfImportantColors = 0;
	}
	
}

