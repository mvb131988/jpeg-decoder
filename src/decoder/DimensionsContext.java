package decoder;

public class DimensionsContext {

    //original image dimension in number of samples (per image component)
    public int[] Xs;
    public int[] Ys;
    
    //extended number of samples(extension of Xs, Ys) to be multiple of 8 
    //this complements last data unit in a row or column that has number of samples
    //in a row or column less than 8. Extended samples don't contain meaningful data.
    public int[] extXs;
    public int[] extYs;
    
    //number of data units based on extXs, extYs
    public int[] dataUnitsXs;
    public int[] dataUnitsYs;
    
    //extended number of data units(extension of dataUnitsXs, dataUnitsYs) to be
    //multiple of sampling factors(vertical and horizontal). Additional data units
    //don't contain meaningful data.
    public int[] extXDataUnit;
    public int[] extYDataUnit;
    
    //secondly extended number of samples. Is calculated based on extended number of data units
    //(extXDataUnit, extYDataUnit)
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
        this.Xs = xs;
        this.Ys = ys;
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
