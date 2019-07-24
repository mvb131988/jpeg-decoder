package main;

import decoder.DecoderControlProcedure;

public class Main {
    
    public static void main(String[] args) throws Exception {
        DecoderControlProcedure dcp = new DecoderControlProcedure("fx_rate_pair.jpg");
        dcp.decodeImage();
    }
}
