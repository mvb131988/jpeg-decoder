package markers;

public class FrameHeader {
    
    private int sofN;
    public int Lf;
    public int P;
    
    //Number of lines
    public int Y;
    //Number of samples per line
    public int X;
    //Number of image components in frame
    public int Nf;
    
    //Component identifiers
    public int[] Cs;
    //Horizontal sampling factor(number of columns in MCUs part of this component)
    public int[] Hs;
    //Vertical sampling factor(number of rows in MCUs part of this component)
    public int[] Vs;
    //Quantization table destination selector 
    public int[] Tqs;
    
    public FrameHeader(int[] header) {
        int start = 0;
        
        sofN = 0xffc0;
        Lf = (header[start] << 8) + header[start+1];
        P = header[start+2];
        Y = (header[start+3] << 8) + header[start+4];
        X = (header[start+5] << 8) + header[start+6];
        Nf = header[start+7];
        
        Cs = new int[Nf];
        Hs = new int[Nf];
        Vs = new int[Nf];
        Tqs = new int[Nf];
        for(int i=0; i<Nf; i++) {
            int b1 = header[start + 8 + 3*i];
            int b2 = header[start + 9 + 3*i];
            int b3 = header[start + 10 + 3*i];
            
            Cs[i] = b1;
            Hs[i] = (b2 & 0xF0) >> 4; 
            Vs[i] = b2 & 0x0F;
            Tqs[i] = b3;
        }
    }
    
    public FrameHeader() {
        
    }
    
    public void print() {
        System.out.println("============= frame header =============");
        System.out.println("sofN : " + sofN + "(" +  Integer.toHexString((sofN & 0xff00) >> 8) + "" + Integer.toHexString(sofN & 0xff)  + ")");
        System.out.println("frameHeaderLength : " + Lf);
        System.out.println("samplePrecision : " + P);
        System.out.println("numberOfLines : " + Y);
        System.out.println("numberOfSamplesPerLine : " + X);
        System.out.println("numberOfImageComponentsInFrame : " + Nf);
        for(int i=0; i<Nf; i++) {
            System.out.println();
            System.out.println("componentIdentifier : " + Cs[i]);
            System.out.println("horizontalSamplingFactor : " + Hs[i]);
            System.out.println("verticalSamplingFactor : " + Vs[i]);
            System.out.println("quantizationTableDestinationSelector : " + Tqs[i]);
        }
        System.out.println("========================================");
        System.out.println();
    }
    
}
