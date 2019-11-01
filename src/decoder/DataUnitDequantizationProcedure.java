package decoder;

import java.io.IOException;

import markers.QuantizationTableSpecification;

//TODO: works only for 8 bit precision. Needs check.
public class DataUnitDequantizationProcedure {

    /**
     * Prepares zz coefficients for dequantization process by zig zag reordering.
     * Invokes dequantization process after zig zag reordering is finished.
     * 
     * @param zz
     * @param qts
     * @param idct
     * @return
     * @throws IOException
     */
    public int[][] dequantize(int[] zz,
                              QuantizationTableSpecification qts,
                              Idct idct,
                              int[][] du,
                              MCUCalculationDataHolder holder) throws IOException 
    {
        //zz coefficients in original order
        int[][] orderedZz = inverseZigZag(zz, holder.orderedQks);
        //dequantized zz
        int[][] dequantizedZz = dequantize(orderedZz, inverseZigZag(qts.getQks(), holder.orderedQks), idct, du);
        return dequantizedZz;
    }
    
    /**
     * Transforms decoded AC/DC coefficients represented as zig zag sequence into
     * two dimensional array of quantized samples(samples original position).
     * 
     * @param zz
     * @return
     */
    private int[][] inverseZigZag(int[] zz, int[][] temp) {
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
     * Dequantization procedure consists of the following steps:
     * (1) Dequantization of the element(multiplication by quantization factor)
     * (2) Inverse discrete cosine transformation (idct is implemented in different
     * flavors, that support different restored image quality)
     * (3) Level shift to change signed value into unsigned
     * 
     * During dequantization zz coefficient that could fit up two bytes is transformed 
     * into single byte value. Two bytes value is signed value, one byte value is unsigned
     * 
     * @param orderedZz
     * @param orderedQks - quantization coefficients restored from zig zag order to original
     * @return TODO
     */
    private int[][] dequantize(int[][] orderedZz, int[][] orderedQks, Idct idct, int[][] du) {
        //////////////// dequantization step /////////////////////////////
        for(int i=0; i<orderedZz.length; i++) {
            for(int j=0; j<orderedZz[0].length; j++) {
                orderedZz[i][j] = orderedZz[i][j]*orderedQks[i][j];
            }
        }
        //////////////////////////////////////////////////////////////////
        
        int[][] samples = du;
        
        for(int y=0; y<samples.length; y++) {
            for(int x=0; x<samples.length; x++) {
                //+128 is a level shift for P=8. Only P=8 is implemented
                samples[y][x] = idct.transform(y,x,orderedZz) + 128;
                
                //There are cases when sample value is out of 8 bit precision. In this case round 
                //to the nearest value.
                if(samples[y][x] < 0) samples[y][x] = 0;
                if(samples[y][x] > 255) samples[y][x] = 255;   
            }
        }
        
        return samples;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    // Different variants of Idct implementation
    
    public interface Idct {
        int transform(int y, int x, int[][] orderedZz);
    }
    
    /**
     * Generates preview images of a low quality with colors distortion, where all coefficients are equal to 1.
     * Used for testing purposes.
     */
    public int idctTest(int y, int x, int[][] orderedZz) {
        double sum = 0;
        for(int u=0; u<8; u++) {
            for(int v=0; v<8; v++) {
                //low quality restore, that is acceptable for small dimension image preview 
                double Cu = 1.0;
                double Cv = 1.0;
                double cosUX = 1.0;
                double cosVY = 1.0;
                sum += Cu*Cv*orderedZz[v][u]*cosUX*cosVY;
            }
        }
        sum *= 0.25;
        return (int) sum;
    };
    
    /**
     * Generates preview images of a low quality, suitable for image preview. 
     *
     * @param y
     * @param x
     * @param orderedZz
     * @return
     */
    public int idctLow(int y, int x, int[][] orderedZz) {
        double[][] cucv = new double[][] {{0.5, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
                                          {0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}};
                                          
        double sum = 0;
        for (int u = 0; u < 8; u++) {
            for (int v = 0; v < 8; v++) {
                // low quality restore, that is acceptable for small dimension image preview
                double CuCv = cucv[u][v];
                double cosUX = 1.0;
                double cosVY = 1.0;
                sum += CuCv*orderedZz[v][u] * cosUX * cosVY;
            }
        }
        sum *= 0.25;
        return (int) sum;                              
    }
    
    /**
     * Generates preview image with the highest possible quality.
     * 
     * @param y
     * @param x
     * @param orderedZz
     * @return
     */
    public int idctHigh(int y, int x, int[][] orderedZz) {
        double sum = 0;
        for(int u=0; u<8; u++) {
            for(int v=0; v<8; v++) {
                //highest quality restore
                double Cu = u == 0 ? 1/Math.sqrt(2) : 1;
                double Cv = v == 0 ? 1/Math.sqrt(2) : 1;
                double cosUX = Math.cos((2*x+1)*u*Math.PI/16);
                double cosVY = Math.cos((2*y+1)*v*Math.PI/16);
                sum += Cu*Cv*orderedZz[v][u]*cosUX*cosVY;
            }
        }
        sum *= 0.25;
        return (int) sum;
    };
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
}
