package persister;

public class BmpFile {

	BmpFileHeader header;
	Pixel[][] pixels;

	public BmpFile(Pixel[][] pixels) {
		super();
		this.header = new BmpFileHeader(pixels);
		this.pixels = pixels;
	}

	/**
	 * Transforms object representation of the pixel to the file representation.  
	 * According to specification lowest rows come first
	 */
	public byte[] rawPixels() {
		int height = (int) header.imageHeight;
		int width = (int) header.imageWidth * 3;
		int widthWithPadding = widthWithPadding(width);

		byte[] b = new byte[height * widthWithPadding];
		int i = 0;

		for (int j = pixels.length - 1; j >= 0; j--) {
			for (Pixel p : pixels[j]) {
				b[i++] = (byte) p.b;
				b[i++] = (byte) p.g;
				b[i++] = (byte) p.r;
			}
			// padding
			for (int z = 0; z < widthWithPadding - width; z++, i++)
				;
		}

		return b;
	}

	/**
	 * Bmp requirement: Image width must be divided by 4 without reminder. If exists
	 * reminder width must be enlarged to minimum value greater than original width,
	 * that is divided by 4 without reminder
	 * 
	 * @return
	 */
	private int widthWithPadding(int width) {
		int newWidth = width;
		while (newWidth % 4 != 0)
			newWidth++;
		return newWidth;
	}
	
	/**
	 * Converts object representation of the header(single value in base 10) to a file representation
	 * (sequence of bytes hexadecimal representation)
	 * 
	 * @param h
	 * @return
	 */
	public byte[] rawHeader() {
        BmpFileHeaderRaw h0 = new BmpFileHeaderRaw(header);
        byte[] header = h0.toByteArray();
        return header;
    }

	public long imageWidth() {
		return header.imageWidth;
	}
	
	public long imageHeight() {
		return header.imageHeight;
	}
	
	/**
	 * Same structure that BmpImageHeaderRaw, but with transformed values to base 10
	 */
	private static class BmpFileHeader {
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
		
	}

	/**
    * Fields are declared in the same order as they go in bmp file header(do not change).
    * After image scaling the following parameters must be updated:
    * - fileSize
    * - imageWidth
    * - imageHeight
    * - sizeOfImageData
    */
   public class BmpFileHeaderRaw {
       
       //constant value, isn't changed
       public int[] signature = new int[2];
       
       //size of the entire bmp file in bytes
       public int[] fileSize = new int[4]; 
       
       //constant value, isn't changed
       public int[] reserved1 = new int[2]; 
       
       //constant value, isn't changed
       public int[] reserved2 = new int[2]; 
       
       //position of the first byte of pixels data relative to the beginning of the file
       //use (54)10 as size of the header structure is constant and equal to 54
       public int[] imageDataOffset = new int[4];
       
       //must be (40)16 (hex value)
       public int[] bitmapInfoHeaderSize = new int[4];
       
       //image width in pixels
       public int[] imageWidth = new int[4];
       
       //image height in pixels
       public int[] imageHeight = new int[4];
       
       //constant value, isn't changed
       public int[] numberOfPlanes = new int[2];
       
       //must be 24, isn't changed
       public int[] numberOfBitsPerPixel = new int[2];
       
       //must be 0, isn't changed
       public int[] compressionType = new int[4];
       
       //size of pixels data in bytes
       public int[] sizeOfImageData = new int[4];
       
       //must be 3780 ppm, isn't changed
       public int[] horizontalResolution = new int[4];
       
       //must be 3780 ppm, isn't changed
       public int[] verticalResolution = new int[4];
       
       //must be 0, isn't changed
       public int[] numberOfColors = new int[4];
       
       //must be 0, isn't changed
       public int[] numberOfImportantColors = new int[4];
       
       public BmpFileHeaderRaw(BmpFileHeader h) {
           signature = littleEndian2Bytes(h.signature);
           fileSize = littleEndian4Bytes(h.fileSize);
           reserved1 = littleEndian2Bytes(h.reserved1);
           reserved2 = littleEndian2Bytes(h.reserved2);
           imageDataOffset = littleEndian4Bytes(h.imageDataOffset);
           bitmapInfoHeaderSize = littleEndian4Bytes(h.bitmapInfoHeaderSize);
           imageWidth = littleEndian4Bytes(h.imageWidth);
           imageHeight = littleEndian4Bytes(h.imageHeight);
           numberOfPlanes = littleEndian2Bytes(h.numberOfPlanes);
           numberOfBitsPerPixel = littleEndian2Bytes(h.numberOfBitsPerPixel);
           compressionType = littleEndian4Bytes(h.compressionType);
           sizeOfImageData = littleEndian4Bytes(h.sizeOfImageData);
           horizontalResolution = littleEndian4Bytes(h.horizontalResolution);
           verticalResolution = littleEndian4Bytes(h.verticalResolution);
           numberOfColors = littleEndian4Bytes(h.numberOfColors);
           numberOfImportantColors = littleEndian4Bytes(h.numberOfImportantColors);
       }
       
       private int[] littleEndian2Bytes(int i) {
           int[] out = new int[2];
           out[0] = i & 0xff;
           out[1] = (i & 0xff00) >> 8;
           return out;
       }
       
       private int[] littleEndian4Bytes(long l) {
           int[] out = new int[4];
           out[0] = (int) (l & 0xff);
           out[1] = (int) ((l & 0xff00) >> 8);
           out[2] = (int) ((l & 0xff0000) >> 16);
           out[3] = (int) ((l & 0xff000000) >> 24);
           return out;
       }
       
       public byte[] toByteArray() {
           byte[] b = new byte[54];
           int i = 0;
           
           b[i++] = (byte)signature[0]; b[i++] = (byte)signature[1];
           b[i++] = (byte)fileSize[0]; b[i++] = (byte)fileSize[1]; b[i++] = (byte)fileSize[2]; b[i++] = (byte)fileSize[3];
           b[i++] = (byte)reserved1[0]; b[i++] = (byte)reserved1[1];
           b[i++] = (byte)reserved2[0]; b[i++] = (byte)reserved2[0];
           
           b[i++] = (byte)imageDataOffset[0]; b[i++] = (byte)imageDataOffset[1]; 
           b[i++] = (byte)imageDataOffset[2]; b[i++] = (byte)imageDataOffset[3];
           
           b[i++] = (byte)bitmapInfoHeaderSize[0]; b[i++] = (byte)bitmapInfoHeaderSize[1];
           b[i++] = (byte)bitmapInfoHeaderSize[2]; b[i++] = (byte)bitmapInfoHeaderSize[3];
           
           b[i++] = (byte)imageWidth[0]; b[i++] = (byte)imageWidth[1]; 
           b[i++] = (byte)imageWidth[2]; b[i++] = (byte)imageWidth[3];
           
           b[i++] = (byte)imageHeight[0]; b[i++] = (byte)imageHeight[1]; 
           b[i++] = (byte)imageHeight[2]; b[i++] = (byte)imageHeight[3];
           
           b[i++] = (byte)numberOfPlanes[0]; b[i++] = (byte)numberOfPlanes[1];
           b[i++] = (byte)numberOfBitsPerPixel[0]; b[i++] = (byte)numberOfBitsPerPixel[1];
                   
           b[i++] = (byte)compressionType[0]; b[i++] = (byte)compressionType[1]; 
           b[i++] = (byte)compressionType[2]; b[i++] = (byte)compressionType[3];
           
           b[i++] = (byte)sizeOfImageData[0]; b[i++] = (byte)sizeOfImageData[1];
           b[i++] = (byte)sizeOfImageData[2]; b[i++] = (byte)sizeOfImageData[3];
           
           b[i++] = (byte)horizontalResolution[0]; b[i++] = (byte)horizontalResolution[1];
           b[i++] = (byte)horizontalResolution[2]; b[i++] = (byte)horizontalResolution[3];
           
           b[i++] = (byte)verticalResolution[0]; b[i++] = (byte)verticalResolution[1];
           b[i++] = (byte)verticalResolution[2]; b[i++] = (byte)verticalResolution[3];
           
           b[i++] = (byte)numberOfColors[0]; b[i++] = (byte)numberOfColors[1]; 
           b[i++] = (byte)numberOfColors[2]; b[i++] = (byte)numberOfColors[3];
           
           b[i++] = (byte)numberOfImportantColors[0]; b[i++] = (byte)numberOfImportantColors[1];
           b[i++] = (byte)numberOfImportantColors[2]; b[i++] = (byte)numberOfImportantColors[3];
           
           return b;
       }
   }
}