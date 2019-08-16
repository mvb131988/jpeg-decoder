package debug;

public class BmpFile {

    BmpFileHeader header;
    Pixel[][] pixels;
    
    public BmpFile(BmpFileHeader header, Pixel[][] pixels) {
        super();
        this.header = header;
        this.pixels = pixels;
    }

    /**
     * According to specification lowest rows come first 
     */
    public byte[] pixelsToBytes() {
        int height = (int) header.imageHeight;
        int width = (int) header.imageWidth * 3;
        int widthWithPadding = widthWithPadding(width);
        
        byte[] b = new byte[height*widthWithPadding];
        int i = 0;

        for (int j = pixels.length - 1; j >= 0; j--) {
            for (Pixel p : pixels[j]) {
                b[i++] = (byte) p.b;
                b[i++] = (byte) p.g;
                b[i++] = (byte) p.r;
            }
            //padding
            for(int z=0; z<widthWithPadding-width; z++, i++);
        }

        return b;
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
