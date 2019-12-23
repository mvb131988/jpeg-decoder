package bmp;

import markers.Image;

public class PixelConverter {

    public Pixel[][] convert(Image img) throws Exception {
        if(img.samples.length != 3) throw new Exception("Only 3 component image is handled"); 
        
        int width = 0;
        int height = 0;
        
        for(int i=0; i<img.samples.length; i++)
            if(img.samples[i].length > height) {
                width = img.samples[i][0].length;
                height = img.samples[i].length;
            }
        
        int[] hs = img.Hs;
        int[] vs = img.Vs;
        int Hmax = 0;
        int Vmax = 0;
        
        for(int i=0; i<hs.length; i++) if(hs[i] > Hmax) Hmax = hs[i];
        for(int i=0; i<vs.length; i++) if(vs[i] > Vmax) Vmax = vs[i];
        
        int[][] ys = img.samples[0];
        int[][] cbs = img.samples[1];
        int[][] crs = img.samples[2];
        
        Pixel[][] rgbImage = new Pixel[height][width];
        for(int i=0; i<height; i++)
            for(int j=0; j<width; j++) {
                // i = hs[k]*i/Hmax 
                // j = vs[k]*j/Vmax
                int y = ys[vs[0]*i/Vmax][hs[0]*j/Hmax]; 
                int cb = cbs[vs[1]*i/Vmax][hs[1]*j/Hmax]; 
                int cr = crs[vs[2]*i/Vmax][hs[2]*j/Hmax];
                
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
        
        return rgbImage;
    }
    
    public Pixel[][] scale(Pixel[][] pixels0) {
        int width0 = pixels0[0].length;
        int height0 = pixels0.length;
        
        int scale = calculateScale(width0, height0);
        int width = (int)width0/scale;
        int height = (int)height0/scale;
        Pixel[][] pixels = new Pixel[height][width];
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                pixels[i][j] = pixels0[i*scale][j*scale];
            }
        }
        
        return pixels;
    }
    
    private int calculateScale(int width, int height) {
    	int widthScale = width/200;
    	int heightScale = height/200;
    	return Math.max(widthScale, heightScale);
    }
    
}
