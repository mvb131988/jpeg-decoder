package main;

import java.nio.file.Paths;

import decoder.DecoderControlProcedure;
import markers.Image;
import persister.BmpFileWriter;
import persister.Pixel;
import persister.PixelConverter;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
    public static void main(String[] args) throws Exception {
    	//TODO: Move to property file
    	
    	//sets the root of the file system, that will be scanned for jpeg images
    	String inputPath = "C:\\endava\\workspace\\jpeg-decoder";
    	//sets the root of the file system, that will persist bmp images(the result
    	//of jpeg transformation, found in inputPath)
    	String outputPath = "C:\\endava\\workspace\\jpeg-decoder\\output";
    	//output(bmp) file name
    	String fileName = "bmp_img_out.bmp";
    	
    	new BmpFileWriter().writeAll(Paths.get(inputPath), Paths.get(outputPath));
    	
        DecoderControlProcedure dcp = new DecoderControlProcedure("IMG_20190828_201042.jpg");
        Image img = dcp.decodeImage();
        
        PixelConverter pc = new PixelConverter(); 
        Pixel[][] pixels = pc.scale(pc.convert(img));
        
//        BmpInHtmlWriter writer = new BmpInHtmlWriter();
//        writer.create(pixels);

        BmpFileWriter bfw = new BmpFileWriter();
        bfw.write(Paths.get(outputPath), Paths.get(""), Paths.get(fileName), pixels);
        
        System.out.println("img");
    }
}
