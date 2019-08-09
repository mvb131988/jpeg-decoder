package markers;

import java.util.List;

public class QuantizationTableSelector {

    /**
     * 
     * @param qtsList
     * @param tq - quantization table destination selector 
     */
    public QuantizationTableSpecification select(List<QuantizationTableSpecification> qtsList, int tq) {
        for(int i=0; i<qtsList.size(); i++)
            if(qtsList.get(i).Tq == tq) return qtsList.get(i);
        return null;
    }
    
}
