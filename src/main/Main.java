package main;

import decoder.DecoderControlProcedure;

//https://www.w3.org/Graphics/JPEG/itu-t81.pdf
//https://www.ece.ucdavis.edu/cerl/reliablejpeg/compression/
public class Main {
    
    public static void main(String[] args) throws Exception {
        DecoderControlProcedure dcp = new DecoderControlProcedure("browny.jpg");
        dcp.decodeImage();
    }
}
