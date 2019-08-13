package decoder;

import java.io.IOException;

import markers.QuantizationTableSpecification;

public class DataUnitDequantizationProcedure {

    //dequantize zz
    public int[][] dequantize(int[] zz,
                              QuantizationTableSpecification qts) throws IOException 
    {
        //zz coefficients in original order
        int[][] orderedZz = inverseZigZag(zz);
        //dequantized zz
        int[][] dequantizedZz = dequantize(orderedZz, inverseZigZag(qts.getQks()));
        return dequantizedZz;
    }
    
    /**
     * Transforms decoded AC/DC coefficients represented as zig zag sequence into
     * two dimensional array of quantized samples(samples original position).
     * 
     * @param zz
     * @return
     */
    private int[][] inverseZigZag(int[] zz) {
        int[][] temp = new int[8][8];
        
        int i=0, j=0;
        boolean isUpward = true;
        
        for(int k=0; k<64; k++) {
            temp[i][j] = zz[k];
            
            if(!isUpward && j==0 && i!=7) {i++; isUpward=true; continue;}
            if(!isUpward && i==7) {j++; isUpward=true; continue;}
            if(!isUpward) {i++; j--; continue;}
            
            if(isUpward && j==7) {i++; isUpward=false; continue;}
            if(isUpward && i==0) {j++; isUpward=false; continue;}
            if(isUpward) {i--; j++; continue;}
        }
        
        return temp;
    }
    
    /**
     * 
     * @param orderedZz
     * @param Qks - quantization coefficients restored from zig zag order to original
     * @return TODO
     */
    public int[][] dequantize(int[][] orderedZz, int[][] Qks) {
        for(int i=0; i<orderedZz.length; i++) {
            for(int j=0; j<orderedZz[0].length; j++) {
                orderedZz[i][j] = orderedZz[i][j]*Qks[i][j];
            }
        }
        
        int[][] samples = new int[8][8];
        
//        System.out.println("================");
        for(int y=0; y<samples.length; y++) {
            for(int x=0; x<samples.length; x++) {
                //+128 is a level shift for P=8. Only O=8 is implemented
                samples[y][x] = idct(y,x,orderedZz) + 128;
                
                //TODO: write tests, check cases when value of sample <0 or >255
                if(samples[y][x] < 0) {
                    System.out.println(samples[y][x]);
                    samples[y][x] = 0;
                }
                if(samples[y][x] > 255) {
                    //System.out.println(samples[y][x]);
                    samples[y][x] = 255;   
                }
                
//                System.out.print(samples[y][x] + " ");
            }
//            System.out.println();
        }
//        System.out.println("================");
        
        return samples;
    }
    
    private int idct(int y, int x, int[][] orderedZz) {
        double sum = 0;
        for(int u=0; u<8; u++) {
            for(int v=0; v<8; v++) {
                double Cu = u == 0 ? 1/Math.sqrt(2) : 1;
                double Cv = v == 0 ? 1/Math.sqrt(2) : 1;
                sum += Cu*Cv*orderedZz[v][u]*Math.cos((2*x+1)*u*Math.PI/16)*Math.cos((2*y+1)*v*Math.PI/16);
            }
        }
        sum *= 0.25;
        return (int) sum;
    }
    
}
