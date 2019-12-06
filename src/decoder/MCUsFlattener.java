package decoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import markers.FrameHeader;
import markers.Image;
import util.FileSystemMCUReader;

/**
 * Splits all MCUs so that DUs from single MCU are grouped by components
 * so that each DU is moved to its component.
 * (all DUs from component1 go to component1 data structure, the same for
 * DUs from component2 and component3)
 */
public class MCUsFlattener {

	private static Logger logger = LogManager.getRootLogger();
	
	public Image flattenMCUs(int numberOfMcu, DecoderContext dc) throws Exception {
		//number of dus in MCU 
        int numberOfDu = 0;
        for (int j = 0; j < dc.frameHeader.Cs.length; j++) numberOfDu += dc.frameHeader.Vs[j] * dc.frameHeader.Hs[j];
        
        int[][][] samples;
        try(FileSystemMCUReader fsmr = new FileSystemMCUReader(numberOfDu)) {
        	samples = flattenMCUsInternally(numberOfMcu, fsmr, dc);
        }
        
        logger.info("Free memory " + (Runtime.getRuntime().freeMemory())/1_000_000 +
		   " Total memory"  + (Runtime.getRuntime().totalMemory())/1_000_000);
        
        return new Image(samples, dc.frameHeader.Hs, dc.frameHeader.Vs); 
	}
	
	/**
     * Reorganizes MCU into regular two dimensional array(for 3 component image each two dimensional array
     * contains values that are part of Y,Cb, Cr representation). These values now are set into
     * correct positions(index of each sample/partial value of a pixel in two dimensional array of the
     * largest image component corresponds to coordinates of the pixel in output bitmap image) 
     * 
     * @return components(3 components) as two dimensional arrays
	 * @throws Exception 
     */
    private int[][][] flattenMCUsInternally(int numberOfMcu, FileSystemMCUReader fsmr, DecoderContext dc) throws Exception {
        
        //number of components
        int nComponents = dc.frameHeader.Nf;   
        //original(no extensions) number of samples in a row per component 
        int[] xs = dc.dimensionsContext.Xs;
        //original(no extensions) number of samples in a column per component
        int[] ys = dc.dimensionsContext.Ys;
        //extended number of data units in a data units row 
        int[] duXs = dc.dimensionsContext.extXDataUnit;
        
        ComponentInFileSystemAssembler[] cas = new ComponentInFileSystemAssembler[nComponents];
        for(int i=0; i<nComponents; i++) cas[i] = new ComponentInFileSystemAssembler(i,
                                                                         			 dc.frameHeader.Hs[i],
                                                                         			 dc.frameHeader.Vs[i],
                                                                         			 xs[i],
                                                                         			 ys[i],
                                                                         			 duXs[i]/dc.frameHeader.Hs[i]);
        
        FrameHeader fh = dc.frameHeader;
        
        for(int mcuI = 0; mcuI<numberOfMcu; mcuI++) {
            //MCU consists of data units from different image components.
            //Sizes contain number of data units in single MCU per image component.
            int[] sizes = new int[fh.Cs.length];
            for (int i = 0; i < sizes.length; i++) {sizes[i] = fh.Vs[i] * fh.Hs[i];}
            
            int[][][] mcu = fsmr.read();
            int duI = 0;
            for(int i=0; i<nComponents; i++) 
                while(sizes[i]>0) {
                    cas[i].add(mcu[duI++]);
                    sizes[i]--;
                }
            logger.info((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
        }
        
        //close component writers(output/component files) in the end
        for(int i=0; i<nComponents; i++) cas[i].close();
        
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //TODO: do not hold all samples of all(3) components in memory any more
        //change return type to void and remove below code
        int[][][] samples = new int[nComponents][][];
        
//        for(int i=0; i<nComponents; i++) samples[i] = null;
//        
//        ComponentRestoreProcedure crp = new ComponentRestoreProcedure();
//        samples = crp.restore(dc);
        
        //TODO: restore components for testing purpose only
        //		put it in debug package 
        
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        logger.info((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1_000_000 + " MB");
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
         * relative to the insertion point(coordinates relative to 0,0 sample that gives starting point
         * of insertion)
         * 
         * Note: Method eliminates padding samples and padding data units. 
         *       Here two cases are possible: 
         *       (1) Some of the samples from most right and most bottom du are used for padding. Procedure
         *           of du traversing within MCU remains the same as for any regular MCU, 
         *           just some of the samples within it are ignored
         *       (2) The entire du within MCU is used for padding. Similarly to 1 all samples from this
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
	
}
