package decoder;

import java.util.ArrayList;
import java.util.List;

import main.FrameHeader;
import main.HuffmanTableSpecification;
import main.ScanHeader;

public class DecoderContext {

    public FrameHeader frameHeader;
    public ScanHeader scanHeader;
    public List<HuffmanTableSpecification> htsList = new ArrayList<HuffmanTableSpecification>();
    public DimensionsContext dimensionsContext;
    
}
