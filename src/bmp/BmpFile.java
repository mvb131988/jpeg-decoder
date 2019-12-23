package bmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BmpFile {

	private static Logger logger = LogManager.getRootLogger();
	
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
			
			logger.info("Free memory " + (Runtime.getRuntime().freeMemory())/1_000_000 +
					   " Total memory" + (Runtime.getRuntime().totalMemory())/1_000_000);
			
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
	
}
