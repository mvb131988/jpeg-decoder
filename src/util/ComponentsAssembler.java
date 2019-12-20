package util;

import decoder.DecoderContext;
import persister.BmpFileHeader;
import persister.Pixel;

/**
 * Combines 3 image components and produces bmp file as an output result. 
 */
public class ComponentsAssembler {

	/**
	 * Takes three components that are currently in tmp dir and transforms
	 * it in two dimensional array of RGB pixels
	 * @throws Exception 
	 */
	public Pixel[][] convert(DecoderContext dc, String fileName) throws Exception {
		//number of pixels per row
		int width = dc.minX;
		int widthWithPadding = widthWithPadding(width);

		//number of pixels per column
		int height = dc.minY;
		
		//only 3 components images are supported
		FileSystemSquashedComponentReader[] fsscrs = new FileSystemSquashedComponentReader[3];
		//open component readers
		for(int i=0; i<fsscrs.length; i++) fsscrs[i] = new FileSystemSquashedComponentReader(i);
		
		FileSystemBmpWriter fsbw = new FileSystemBmpWriter(fileName);
		fsbw.writeHeader(new BmpFileHeader(dc));
		
		for(int i=0; i<height; i++) {
			for(int j=0; j<widthWithPadding; j++) {
				Pixel p = new Pixel(0,0,0);
				if(j<width) {
					int y = fsscrs[0].read(); 
	                int cb = fsscrs[1].read(); 
	                int cr = fsscrs[2].read();
	                
	                p = pixel(y, cb, cr);
				}
				fsbw.writePixel(p);
			}
			System.out.println("Row +" + i);
		}
		
		//close writer
		fsbw.close();
		
		//close component readers
		for(int i=0; i<fsscrs.length; i++) fsscrs[i].close();
		
		//return rgbImage;
		return null;
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
	
	private Pixel pixel(int y, int cb, int cr) {
		int r = (int)(y + 1.402 *(cr-128));
        if(r>255) r = 255;
        if(r<0) r = 0;
        
        int g = (int)(y - 0.34414*(cb-128) - 0.71414*(cr-128));
        if(g>255) g = 255;
        if(g<0) g = 0;
        
        int b = (int)(y + 1.772*(cb-128));
        if(b>255) b = 255;
        if(b<0) b = 0;
        
        return new Pixel(r,g,b);
	}
	
}
