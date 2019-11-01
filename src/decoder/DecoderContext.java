package decoder;

import java.util.ArrayList;
import java.util.List;

import markers.FrameHeader;
import markers.HuffmanTableSpecification;
import markers.QuantizationTableSpecification;
import markers.RestartInterval;
import markers.ScanHeader;

public class DecoderContext {

    public FrameHeader frameHeader;
    public ScanHeader scanHeader;
    public RestartInterval restartInterval;
    public List<HuffmanTableSpecification> htsList = new ArrayList<>();
    public List<QuantizationTableSpecification> qtsList = new ArrayList<>();
    public DimensionsContext dimensionsContext;
    public int[] predDcs;
    //---------------------- MCU parameters --------------------------------
    //MCU consists of data units from different image components.
    //Sizes contain number of data units in single MCU per image component.
    private int[] mcuComponentsSize;
    //For memory consumption measurement only (not relying on GC)
    private int[] mcuComponentSizeCopy;
    
    		//number of data units in the MCU
    public int mcuSize = 0;
    //------------------------------------------------------------------------
    
    public void mcuComponentsSize(int[] mcuComponentsSize) {
    	this.mcuComponentsSize = mcuComponentsSize;
    	this.mcuComponentSizeCopy = new int[mcuComponentsSize.length];
    }
    
    public int[] mcuComponentsSize() {
    	for(int i=0; i<mcuComponentsSize.length; i++) mcuComponentSizeCopy[i] = mcuComponentsSize[i];
    	return mcuComponentSizeCopy;
    } 
    
    /**
     * Init PRED value of DC coefficient for each component
     * Preserve components order in scan header
     */
    public void initPredDC() {
        predDcs = new int[scanHeader.Ns];
    }
}
