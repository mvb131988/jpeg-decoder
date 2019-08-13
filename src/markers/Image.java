package markers;

public class Image {

    //Contains fully restored components samples
    public int[][][] samples;
    
    //Horizontal sampling factor(number of columns in MCUs part of this component)
    public int[] Hs;
    
    //Vertical sampling factor(number of rows in MCUs part of this component)
    public int[] Vs;

    public Image(int[][][] samples, int[] hs, int[] vs) {
        super();
        this.samples = samples;
        Hs = hs;
        Vs = vs;
    }
    
}
