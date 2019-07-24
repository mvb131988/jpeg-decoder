package decoder;

import java.util.ArrayList;
import java.util.List;

import markers.FrameHeader;
import markers.HuffmanTableSpecification;
import markers.ScanHeader;

public class DecoderContext {

    public FrameHeader frameHeader;
    public ScanHeader scanHeader;
    public List<HuffmanTableSpecification> htsList = new ArrayList<HuffmanTableSpecification>();
    public DimensionsContext dimensionsContext;
    
}
