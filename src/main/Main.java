package main;

import debug.ImageInHtmlWriter;
import debug.ImageInHtmlWriter.Pixel;
import debug.PixelConverter;
import decoder.DecoderControlProcedure;
import markers.Image;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
    public static void main(String[] args) throws Exception {
        DecoderControlProcedure dcp = new DecoderControlProcedure("browny.jpg");
        Image img = dcp.decodeImage();
        
        Pixel[][] pixels = new PixelConverter().convert(img);
        
        ImageInHtmlWriter writer = new ImageInHtmlWriter();
        writer.create(pixels.length, pixels[0].length, pixels);
        
        System.out.println("img");
    }
}
