package util;

import decoder.DecoderContext;
import persister.Pixel;

public class FileSystemPixelConverter {

	//TODO:scaling here
	
	/**
	 * Takes three components that are currently in tmp dir and transforms
	 * it in two dimensional array of RGB pixels
	 * @throws Exception 
	 */
	public Pixel[][] convert(DecoderContext dc) throws Exception {
		//number of pixels per row
		int pxr = dc.frameHeader.X;
		//number of pixels per column
		int pxc = dc.frameHeader.Y;
		
		Pixel[][] rgbImage = new Pixel[pxc][pxr];

		//only 3 components images are supported
		FileSystemExtendedComponentReader[] fsecrs = new FileSystemExtendedComponentReader[3];
		//open component readers
		for(int i=0; i<fsecrs.length; i++) fsecrs[i] = new FileSystemExtendedComponentReader(i);
		
		for(int i=0; i<pxc; i++) {
			for(int j=0; j<pxr; j++) {
				int y = fsecrs[0].read(); 
                int cb = fsecrs[1].read(); 
                int cr = fsecrs[2].read();
                
                int r = (int)(y + 1.402 *(cr-128));
                if(r>255) r = 255;
                if(r<0) r = 0;
                
                int g = (int)(y - 0.34414*(cb-128) - 0.71414*(cr-128));
                if(g>255) g = 255;
                if(g<0) g = 0;
                
                int b = (int)(y + 1.772*(cb-128));
                if(b>255) b = 255;
                if(b<0) b = 0;
                
                rgbImage[i][j] = new Pixel(r,g,b);
			}
			System.out.println("Row +" + i);
		}
		
		//close component readers
		for(int i=0; i<fsecrs.length; i++) fsecrs[i].close();
		
		return rgbImage;
	}
	
}
