package markers;

public class RestartInterval {

    public int DRI;
    
    public int Lr;
    
    public int Ri;

    public RestartInterval(int[] header) {
        DRI = 0xffdd;

        int start = 0;
        Lr = (header[start] << 8) + header[start+1];
        
        start+=2;
        Ri = (header[start] << 8) + header[start+1];
    }
    
}
