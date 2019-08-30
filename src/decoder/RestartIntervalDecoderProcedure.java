package decoder;

import java.io.IOException;

import markers.FrameHeader;
import markers.Image;
import util.BufferedReader;

public class RestartIntervalDecoderProcedure {

    private MCUDecoderProcedure dp = new MCUDecoderProcedure();
    
    public Image decodeRestartInterval(BufferedReader br, DecoderContext dc) throws IOException {
        //Init PRED value of DC coefficient for each component
        //Preserve components order in scan header
        dc.initPredDC();
        
        //TODO: It's assumed that only one restart interval exist,
        //however needs to check on the images with several restart intervals
        
        DimensionsContext dimc = dc.dimensionsContext;
        int[] extXDataUnit = dimc.extXDataUnit;
        int[] extYDataUnit = dimc.extYDataUnit;
        
        //number of MCUs in one row
        int nX = extXDataUnit[0]/dc.frameHeader.Hs[0];
        //number of MCUs in one column
        int nY = extYDataUnit[0]/dc.frameHeader.Vs[0];
        
        int numberOfMcu = nX*nY;
        int[][][][] mcus = new int [numberOfMcu][][][];
        
        NextBitReader nbr = new NextBitReader(br);
        int iMcu = 1;
        for (int i = 0; i < mcus.length; i++) {
//            if(i==200) {
//                System.out.println("debug");
//            }
            
            mcus[i] = dp.decodeMCU(nbr, dc);
//            System.out.println(iMcu++);
        }
        
        System.out.println("=== END OF READ ===");
        
        int[][][] samples = flattenMCUs(mcus, dc);
        
        return new Image(samples, dc.frameHeader.Hs, dc.frameHeader.Vs); 
    }
    
    /**
     * 
     * 
     * @return
     */
    private int[][][] flattenMCUs(int[][][][] mcus, DecoderContext dc) {
        
        //number of components
        int nComponents = dc.frameHeader.Nf;   
        //number of data units in a row per component
        int[] extXDataUnit = dc.dimensionsContext.extXDataUnit;
        //number of data units in a column per component
        int[] extYDataUnit = dc.dimensionsContext.extYDataUnit;
        //original(no extensions) number of samples in a row per component 
        int[] xs = dc.dimensionsContext.Xs;
        //original(no extensions) number of samples in a column per component
        int[] ys = dc.dimensionsContext.Ys;
        
        ComponentAssembler[] cas = new ComponentAssembler[nComponents];
        for(int i=0; i<nComponents; i++) cas[i] = new ComponentAssembler(extXDataUnit[i], 
                                                                         extYDataUnit[i], 
                                                                         xs[i], 
                                                                         ys[i],
                                                                         dc.frameHeader.Hs[i],
                                                                         dc.frameHeader.Vs[i]);
        
        FrameHeader fh = dc.frameHeader;
        
        for(int mcuI = 0; mcuI<mcus.length; mcuI++) {
            //MCU consists of data units from different image components.
            //Sizes contain number of data units in single MCU per image component.
            int[] sizes = new int[fh.Cs.length];
            for (int i = 0; i < sizes.length; i++) {sizes[i] = fh.Vs[i] * fh.Hs[i];}
            
            int duI = 0;
            for(int i=0; i<nComponents; i++) 
                while(sizes[i]>0) {
                    cas[i].add(mcus[mcuI][duI++]);
                    sizes[i]--;
                }
        }
        
        int[][][] samples = new int[nComponents][][];
        
        for(int i=0; i<nComponents; i++) samples[i] = cas[i].samples;
        
        return samples;
        
        //TODO: Initialize ComponentBuilder for each component(set height, width in samples, height and 
        //width in data units per MCU)
        
        //disassemble each MCU in separate data units and pass data unit of i-s component to ComponentBuilder
        //of i-th component.
        
        //Disassembling of data units into samples and pixels allocation is done inside ComponentBuilder
    } 
    
    private static class ComponentAssembler {
        
        //data units number in a row
        private int extXDataUnit;
        
        //data units number in a column
        private int extYDataUnit;
        
        //number of samples in a row
        private int xs;
        
        //number of samples in a column
        private int ys;
        
        //two dimensional array of component's samples 
        private int[][] samples;
        
        //horizontal sampling factor(number of columns in MCUs part of this component)
        private int hs;
        
        //vertical sampling factor(number of rows in MCUs part of this component)
        private int vs;
        
        //current position
        //position in number of samples relative to the most top left corner
        private int rowPos;
        private int columnPos;
        
        //There are hs*vs data units(lets call this region) of this specific component in the MCU, hs columns 
        //and vs rows.
        //Example: Let's say hs=2, vs=2. This means each 4 consecutive data units form a region such that:
        //1't  du has neighbor 2 to the right and 3 to the bottom,
        //2'd  du has neighbor 1 to the left and 4 to the bottom
        //3'd  du has neighbor 1 to the top and 4 to the right
        //4'th du has neighbor 2 to the top and 3 to the left
        //
        //Samples from du would be placed into final samples array according to rowPos and columnPos(staring point for 
        //the current region) and duColumnNumber, duRowNumber
        //
        //Example:
        //For the first du from the region   rowStart = rowPos + duRowNumber*8 = 0 + 0 = 0
        //                                   columnStart = columnStart + duColumnNumber*8 = 0 + 0 = 0   
        //For the second du from the region  rowStart = rowPos + duRowNumber*8 = 0 + 0 = 0
        //                                   columnStart = columnStart + duColumnNumber*8 = 0 + 1*8 = 8
        //For the third du from the region   rowStart = rowPos + duRowNumber*8 = 0 + 1*8 = 8
        //                                   columnStart = columnStart + duColumnNumber*8 = 0 + 0 = 0   
        //For the forth du from the region   rowStart = rowPos + duRowNumber*8 = 0 + 1*8 = 8
        //                                   columnStart = columnStart + duColumnNumber*8 = 0 + 1*8 = 8
        private int duColumnNumber;
        private int duRowNumber;
        
        public ComponentAssembler(int extXDataUnit, int extYDataUnit, int xs, int ys, int hs, int vs) {
            this.extXDataUnit = extXDataUnit;
            this.extYDataUnit = extYDataUnit;
            this.xs = xs;
            this.ys = ys;
            this.hs = hs;
            this.vs = vs;
            
            this.samples = new int[this.ys][this.xs];
            this.rowPos = 0;
            this.columnPos = 0;
            this.duRowNumber = 0;
            this.duColumnNumber = 0;
        }
        
        /**
         * Adds data unit(8x8 block of samples) to the resulting samples two dimensional array
         * relative to the insertion point(coordinates relative to 0,0 sample that gives staring point
         * of insertion)
         * 
         * @param du
         */
        public void add(int[][] du) {
            addInternally(rowPos + duRowNumber * 8, columnPos + duColumnNumber * 8, du);
            
            duColumnNumber++;
            if(duColumnNumber == hs) {
                duColumnNumber = 0;
                duRowNumber++;
                if(duRowNumber == vs) {
                    duRowNumber = 0;
                    //move rowPos, columnPosition
                    this.columnPos += 8*hs;
                    
                    //when end of line is reached, move to the next row
                    if(this.columnPos >= xs) {
                        this.columnPos = 0;
                        this.rowPos += 8*vs; 
                    }
                }
            }
        }
        
        private void addInternally(int y, int x, int[][]du) {
            for (int i = y, i0 = 0; i < y + 8; i++, i0++)
                for (int j = x, j0 = 0; j < x + 8; j++, j0++)
                    //eliminates padding samples(either of uncompleted data unit(most right or bottom)
                    //or padding data units)
                    if(i<ys && j<xs) 
                        samples[i][j] = du[i0][j0];
        }
        
    }

    public MCUDecoderProcedure getDp() {
        return dp;
    }

    public void setDp(MCUDecoderProcedure dp) {
        this.dp = dp;
    }
    
}
