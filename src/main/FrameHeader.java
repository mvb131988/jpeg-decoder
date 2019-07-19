package main;

public class FrameHeader {
    
    private int sofN;
    private int frameHeaderLength;
    private int samplePrecision;
    private int numberOfLines;
    private int numberOfSamplesPerLine;
    private int numberOfImageComponentsInFrame;
    
    private int[] componentIdentifier;
    private int[] horizontalSamplingFactor;
    private int[] verticalSamplingFactor;
    private int[] quantizationTableDestinationSelector;
    
    public FrameHeader(int[] header) {
        int start = 0;
        
        sofN = 0xffc0;
        frameHeaderLength = (header[start] << 8) + header[start+1];
        samplePrecision = header[start+2];
        numberOfLines = (header[start+3] << 8) + header[start+4];
        numberOfSamplesPerLine = (header[start+5] << 8) + header[start+6];
        numberOfImageComponentsInFrame = header[start+7];
        
        componentIdentifier = new int[numberOfImageComponentsInFrame];
        horizontalSamplingFactor = new int[numberOfImageComponentsInFrame];
        verticalSamplingFactor = new int[numberOfImageComponentsInFrame];
        quantizationTableDestinationSelector = new int[numberOfImageComponentsInFrame];
        for(int i=0; i<numberOfImageComponentsInFrame; i++) {
            int b1 = header[start + 8 + 3*i];
            int b2 = header[start + 9 + 3*i];
            int b3 = header[start + 10 + 3*i];
            
            componentIdentifier[i] = b1;
            horizontalSamplingFactor[i] = b2 & 0x0F;
            verticalSamplingFactor[i] = (b2 & 0xF0) >> 4;
            quantizationTableDestinationSelector[i] = b3;
        }
    }
    
    public void print() {
        System.out.println("============= frame header =============");
        System.out.println("sofN : " + sofN + "(" +  Integer.toHexString((sofN & 0xff00) >> 8) + "" + Integer.toHexString(sofN & 0xff)  + ")");
        System.out.println("frameHeaderLength : " + frameHeaderLength);
        System.out.println("samplePrecision : " + samplePrecision);
        System.out.println("numberOfLines : " + numberOfLines);
        System.out.println("numberOfSamplesPerLine : " + numberOfSamplesPerLine);
        System.out.println("numberOfImageComponentsInFrame : " + numberOfImageComponentsInFrame);
        for(int i=0; i<numberOfImageComponentsInFrame; i++) {
            System.out.println();
            System.out.println("componentIdentifier : " + componentIdentifier[i]);
            System.out.println("horizontalSamplingFactor : " + horizontalSamplingFactor[i]);
            System.out.println("verticalSamplingFactor : " + verticalSamplingFactor[i]);
            System.out.println("quantizationTableDestinationSelector : " + quantizationTableDestinationSelector[i]);
        }
        System.out.println("========================================");
        System.out.println();
    }
    
}
