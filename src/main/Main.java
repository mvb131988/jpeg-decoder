package main;

import decoder.DecoderControlProcedure;

public class Main {
    
    public static void main(String[] args) throws Exception {
        DecoderControlProcedure dcp = new DecoderControlProcedure("browny.jpg");
        dcp.decodeImage();
    }
}
