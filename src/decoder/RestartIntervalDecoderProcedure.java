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
        
        //TODO: Hs and Vs are at a wrong place, swap them
        
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
        int i = 0;
        while (i < mcus.length) {
        	//check restart interval marker
        	int b1 = br.next();
        	int b2 = br.next();
        	
        	if(!(b1 == 0xff && (b2 == 0xd0 || 
        						b2 == 0xd1 || 
        						b2 == 0xd2 || 
        						b2 == 0xd3 || 
        						b2 == 0xd4 || 
        						b2 == 0xd5 || 
        						b2 == 0xd6 ||
        						b2 == 0xd7))) {
        		br.pushBack(b1);
        		br.pushBack(b2);
        	
        		mcus[i++] = dp.decodeMCU(nbr, dc);
        	} else {
        		// refresh actions when restart marker is met 
        		dc.initPredDC();
        		nbr = new NextBitReader(br);
        	}
        }
        
        //TODO:
        // move flatten logic to a separate class
        // handle restart interval(definition marker) marker
        int[][][] samples = flattenMCUs(mcus, dc);
        
        return new Image(samples, dc.frameHeader.Hs, dc.frameHeader.Vs); 
    }
    
    /**
     * Reorganizes MCU into regular two dimensional array(for 3 component image each two dimensional array
     * contains values that are part of Y,Cb, Cr representation). These values now are set into
     * correct positions(index of each sample/partial value of a pixel in two dimensional array of the
     * largest image component corresponds to coordinates of the pixel in output bitmap image) 
     * 
     * @return components(3 components) as two dimensional arrays
     */
    private int[][][] flattenMCUs(int[][][][] mcus, DecoderContext dc) {
        
        //number of components
        int nComponents = dc.frameHeader.Nf;   
        //original(no extensions) number of samples in a row per component 
        int[] xs = dc.dimensionsContext.Xs;
        //original(no extensions) number of samples in a column per component
        int[] ys = dc.dimensionsContext.Ys;
        
        ComponentAssembler[] cas = new ComponentAssembler[nComponents];
        for(int i=0; i<nComponents; i++) cas[i] = new ComponentAssembler(xs[i], 
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
            System.out.println("MCU: " + mcuI + " is processed");
        }
        
        int[][][] samples = new int[nComponents][][];
        
        for(int i=0; i<nComponents; i++) samples[i] = cas[i].samples;
        
        return samples;
    } 
    
    private static class ComponentAssembler {
        
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
        //position in number of samples relative to the top most left most corner
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
        //Samples from du would be placed into final samples array according to rowPos and columnPos(starting point for 
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
        
        public ComponentAssembler(int xs, int ys, int hs, int vs) {
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
         * Note: Method eliminates padding samples and padding data units. 
         *       Here two cases are possible: 
         *       (1) Some of the samples from most right and most bottom du are used for padding. Procedure
         *           of du traversing within MCU remains the same as for any regular MCU, 
         *           just some of the samples within it are ignored
         *       (2) The entire du within MCU is used for padding. Similar to 1 all samples from this
         *           du are ignored, but what is the most important there are no more MCUs to the right or
         *           to the bottom(that if they existed shoud've been entirely from padding samples, would
         *           've been ignored by condition this.columnPos >= xs and would've considered as first 
         *           MCU starting from the new row). There are no MCU's to the right or bottom because it is padded
         *           to be multiple of horizontal or vertical scaling factor and hence number of padded
         *           du's is not greater than number of du's in a row or column in a regular MCU.    
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
