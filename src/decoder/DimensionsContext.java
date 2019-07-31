package decoder;

public class DimensionsContext {

    public int[] Xs;
    public int[] Ys;
    public int[] extXs;
    public int[] extYs;
    public int[] dataUnitsXs;
    public int[] dataUnitsYs;
    public int[] extXDataUnit;
    public int[] extYDataUnit;
    public int[] finalExtXs;
    public int[] finalExtYs;
    
    public DimensionsContext(int[] xs, 
                             int[] ys, 
                             int[] extXs, 
                             int[] extYs, 
                             int[] dataUnitsXs, 
                             int[] dataUnitsYs,
                             int[] extXDataUnit, 
                             int[] extYDataUnit,
                             int[] finalExtXs,
                             int[] finalExtYs)
    {
        super();
        Xs = xs;
        Ys = ys;
        this.extXs = extXs;
        this.extYs = extYs;
        this.dataUnitsXs = dataUnitsXs;
        this.dataUnitsYs = dataUnitsYs;
        this.extXDataUnit = extXDataUnit;
        this.extYDataUnit = extYDataUnit;
        this.finalExtXs = finalExtXs;
        this.finalExtYs = finalExtYs;
    }
    
}
