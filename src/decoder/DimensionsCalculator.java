package decoder;

import java.math.BigDecimal;
import java.math.RoundingMode;

import markers.FrameHeader;

public class DimensionsCalculator {

    public DimensionsContext calculate(FrameHeader fh) {
        int X = fh.X;
        int Y = fh.Y;
        int Nf = fh.Nf;
        int[] Cs = fh.Cs;
        int[] Hs = fh.Hs;
        int[] Vs = fh.Vs;
        
        //calculate max sampling factor throughout all sampling factors Hs/Vs 
        int Hmax = 0;
        int Vmax = 0;
        for(int i=0; i<Cs.length; i++) {
            if(Hmax < Hs[i]) Hmax = Hs[i];
            if(Vmax < Vs[i]) Vmax = Vs[i];
        }
        
        //calculate components size
        int[] Xs = new int[Cs.length];
        int[] Ys = new int[Cs.length];
        for(int i=0; i<Cs.length; i++) {
            Xs[i] = new BigDecimal(X * Hs[i]).divide(new BigDecimal(Hmax), RoundingMode.CEILING).intValue();
            Ys[i] = new BigDecimal(Y * Vs[i]).divide(new BigDecimal(Vmax), RoundingMode.CEILING).intValue();
        }
        
        //extend number of lines(columns/rows) so that the value is multiple of 8
        /////////////////////////////////////////////////////////////////////////
        int[] extXs = extend1(Xs);
        int[] extYs = extend1(Ys);
        
        // calculate number of data units per component
        //
        //number of data units in a row per component
        int[] dataUnitsXs = new int[Cs.length];
        //number of data units in a column per component
        int[] dataUnitsYs = new int[Cs.length];
        for(int i=0; i<Cs.length; i++) {
            dataUnitsXs[i] = extXs[i]/8;
            dataUnitsYs[i] = extYs[i]/8;
        }
        
        //extend number of lines(columns/rows) so that the value is multiple 
        //of Hs[i]/Vs[i]
        /////////////////////////////////////////////////////////////////////////
        int[] extXDataUnit = null;
        int[] extYDataUnit = null;
        int[] finalExtXs = null;
        int[] finalExtYs = null;

        if(Nf > 1) {
            extXDataUnit = extend2(dataUnitsXs, Hs);
            extYDataUnit = extend2(dataUnitsYs, Vs);
        
            //second extension of numbers of samples per row AND/OR column.
            //after number of data units has been extended, number of samples in row/column also
            //need to be extended by 8*(number of extended data units - number of data units)
            finalExtXs = extend3(dataUnitsXs, extXDataUnit, extXs);
            finalExtYs = extend3(dataUnitsYs, extYDataUnit, extYs);
        }
        
        return new DimensionsContext(Xs, 
                                     Ys, 
                                     extXs, 
                                     extYs, 
                                     dataUnitsXs, 
                                     dataUnitsYs,
                                     extXDataUnit, 
                                     extYDataUnit,
                                     finalExtXs,
                                     finalExtYs);
    }
    
    private int[] extend1(int[] in) {
        int[] copy = new int[in.length];
        for (int i = 0; i < in.length; i++)
            copy[i] = in[i];

        for (int i = 0; i < copy.length; i++)
            while (copy[i] % 8 != 0)
                copy[i]++;
        return copy;
    }
    
    private int[] extend2(int[] in, int[] ks) {
        int[] copy = new int[in.length];
        for (int i = 0; i < in.length; i++)
            copy[i] = in[i];

        for (int i = 0; i < copy.length; i++)
            while (copy[i] % ks[i] != 0)
                copy[i]++;
        return copy;
    }
    
    private int[] extend3(int[] dataUnits, int[] extXDataUnit, int[] extSamples) {
        int[] res = new int[dataUnits.length];
        
        for(int i=0; i<res.length; i++)
            res[i] = extSamples[i] + (extXDataUnit[i]-dataUnits[i])*8;
        
        return res;
    }
    
}
