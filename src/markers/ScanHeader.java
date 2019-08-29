package markers;

public class ScanHeader {

    private int sos;
    
    //Scan header length
    public int Ls;
    
    //Number of image components in scan
    public int Ns;
    
    //Scan component selector
    public int[] Cs;
    //DC entropy coding table destination selector
    public int[] Td;
    //AC entropy coding table destination selector
    public int[] Ta;
    
    //Start of spectral or predictor selection 
    public int Ss;
    //End of spectral selection
    public int Se; 
    //Successive approximation bit position high
    public int Ah;
    //Successive approximation bit position low or point transform 
    public int Al;
    
    public ScanHeader(int[] header) {
        int start = 0;
        
        sos = 0xffda;
        Ls = (header[start] << 8) + header[start+1];
        Ns = header[start+2];
        
        Cs = new int[Ns];
        Td = new int[Ns];
        Ta = new int[Ns];
        for(int i=0; i<Ns; i++) {
            int b1 = header[start+3 + 2*i];
            int b2 = header[start+4 + 2*i];
            
            Cs[i] = b1;
            Td[i] = b2 & 0x0F;
            Ta[i] = (b2 & 0xF0) >> 4;
        }
        
        int pos = start + 3  + Ns*2;
        Ss = header[pos];
        Se = header[pos+1];
        Ah = header[pos+2] & 0x0F;
        Al = (header[pos+2] & 0xF0) >> 4;
    }
    
    public ScanHeader() {
        
    }
    
    public static ScanHeader checkAndBuild(int[] header) {
        if(header[1] == 0xda) return new ScanHeader(header);
        return null;
    }
    
    /**
     * 
     * @param componentIdentifier - component number saved in Cs[componentIdentifier]
     * @return component index in Cs, Td, Ta
     */
    public int indexOfComponent(int componentIdentifier) {
        for(int i=0; i<Cs.length; i++) if(Cs[i] == componentIdentifier) return i;
        return -1;
    }
    
    public void print() {
        System.out.println("============= scan header =============");
        System.out.println("sos : " + sos + "(" +  Integer.toHexString((sos & 0xff00) >> 8) + "" + Integer.toHexString(sos & 0xff)  + ")");
        System.out.println("scanHeaderLength : " + Ls);
        System.out.println("numberOfImageComponentsInScan : " + Ns);
        for(int i=0; i<Ns; i++) {
            System.out.println();
            System.out.println("scanComponentSelector : " + Cs[i]);
            System.out.println("dcEntropyCodingTableDestinationSelector : " + Td[i]);
            System.out.println("acEntropyCodingTableDestinationSelector : " + Ta[i]);
        }
        System.out.println();
        System.out.println("startOfSpectralOrPredictorSelection : " + Ss);
        System.out.println("endOfSpectralSelection : " + Se);
        System.out.println("successiveApproximationBitPositionHigh : " + Ah);
        System.out.println("successiveApproximationBitPositionLowOrPointTransform : " + Al);
        System.out.println("========================================");
        System.out.println();
    }
    
}
