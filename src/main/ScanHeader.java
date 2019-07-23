package main;

public class ScanHeader {

    private int sos;
    private int scanHeaderLength;
    private int numberOfImageComponentsInScan;
    
    //Scan component selector
    public int[] Cs;
    //DC entropy coding table destination selector
    public int[] Td;
    //AC entropy coding table destination selector
    public int[] Ta;
    
    private int startOfSpectralOrPredictorSelection;
    private int endOfSpectralSelection; 
    private int successiveApproximationBitPositionHigh;
    private int successiveApproximationBitPositionLowOrPointTransform;
    
    public ScanHeader(int[] header) {
        int start = 0;
        
        sos = 0xffda;
        scanHeaderLength = (header[start] << 8) + header[start+1];
        numberOfImageComponentsInScan = header[start+2];
        
        Cs = new int[numberOfImageComponentsInScan];
        Td = new int[numberOfImageComponentsInScan];
        Ta = new int[numberOfImageComponentsInScan];
        for(int i=0; i<numberOfImageComponentsInScan; i++) {
            int b1 = header[start+3 + 2*i];
            int b2 = header[start+4 + 2*i];
            
            Cs[i] = b1;
            Td[i] = b2 & 0x0F;
            Ta[i] = (b2 & 0xF0) >> 4;
        }
        
        int pos = start + 3  + numberOfImageComponentsInScan*2;
        startOfSpectralOrPredictorSelection = header[pos];
        endOfSpectralSelection = header[pos+1];
        successiveApproximationBitPositionHigh = header[pos+2] & 0x0F;
        successiveApproximationBitPositionLowOrPointTransform = (header[pos+2] & 0xF0) >> 4;
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
        System.out.println("scanHeaderLength : " + scanHeaderLength);
        System.out.println("numberOfImageComponentsInScan : " + numberOfImageComponentsInScan);
        for(int i=0; i<numberOfImageComponentsInScan; i++) {
            System.out.println();
            System.out.println("scanComponentSelector : " + Cs[i]);
            System.out.println("dcEntropyCodingTableDestinationSelector : " + Td[i]);
            System.out.println("acEntropyCodingTableDestinationSelector : " + Ta[i]);
        }
        System.out.println();
        System.out.println("startOfSpectralOrPredictorSelection : " + startOfSpectralOrPredictorSelection);
        System.out.println("endOfSpectralSelection : " + endOfSpectralSelection);
        System.out.println("successiveApproximationBitPositionHigh : " + successiveApproximationBitPositionHigh);
        System.out.println("successiveApproximationBitPositionLowOrPointTransform : " + successiveApproximationBitPositionLowOrPointTransform);
        System.out.println("========================================");
        System.out.println();
    }
    
}
