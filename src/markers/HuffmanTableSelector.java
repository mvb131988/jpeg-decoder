package markers;

import java.util.List;

public class HuffmanTableSelector {

    /**
     * 
     * @param htsList
     * @param tc - Table class – 0 = DC table or lossless table, 1 = AC table
     * @param th - Huffman table destination identifier
     */
    public HuffmanTableSpecification select(List<HuffmanTableSpecification> htsList, int tc, int th) {
        for(int i=0; i<htsList.size(); i++)
            if(htsList.get(i).tc == tc && htsList.get(i).th == th) return htsList.get(i);
        return null;
    }
    
}
