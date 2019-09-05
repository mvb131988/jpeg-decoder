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
    
    /**
     * Init PRED value of DC coefficient for each component
     * Preserve components order in scan header
     */
    public void initPredDC() {
        predDcs = new int[scanHeader.Ns];
    }
}
