package decoder;


public class DataUnitDequantizationProcedure {

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
        
        System.out.println("================");
        for(int y=0; y<samples.length; y++) {
            for(int x=0; x<samples.length; x++) {
                //+128 is a level shift for P=8. Only O=8 is implemented
                samples[y][x] = idct(y,x,orderedZz) + 128;
                
                if(samples[y][x] > 255) {
                    System.out.println("debug");
                }
                
                System.out.print(samples[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println("================");
        
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
