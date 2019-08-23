package decoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class DataUnitDequantizationProcedureTest {

    private DataUnitDequantizationProcedure dudp = new DataUnitDequantizationProcedure();
    
    /**
     * Checks normal order restoring from zig zag order for data unit(du, zz) coefficients
     * This is valid zig zag coefficients used in ACDecodeProcedureTest. 
     * 
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    public void testInverseZigZag1() throws IOException, 
                                           NoSuchMethodException, 
                                           SecurityException, 
                                           IllegalAccessException, 
                                           IllegalArgumentException, 
                                           InvocationTargetException 
    {
        int[] zigZagDu = new int[] { 379,  270,  238,  -19,  -230,  -87,  -8,  15, 
                                       2,  -10,    0,   16,    37,   33,   2,   4,
                                      -9,  -28,  -11,    4,    -3,    3,   0,  -8,
                                       1,    4,   -3,   -1,    -2,    0,   3,   5,
                                       6,    2,   -2,   -1,     0,    0,  -1,  -4,
                                      -4,    0,    3,   -2,     1,    1,  -2,  -1,
                                       0,    0,    2,    2,     0,    1,  -1,  -1,
                                      -1,    0,    0,    0,     0,    1,   2,  -2 };
        
        //after zigZag reordering
        int[][] du = new int[][] {{379,   270,  -87,  -8,    2,    4,    -1,   -2},
                                  {238,  -230,   15,  33,   -9,   -3,     0,    3},
                                  { -19,    2,   37, -28,    4,    3,     0,   -2}, 
                                  { -10,   16,  -11,   1,    5,   -4,     1,    1},
                                  {  0,     4,   -8,   6,   -4,    1,     0,   -1},
                                  { -3,     0,    2,  -1,   -2,    2,    -1,    0},
                                  {  3,    -2,    0,  -1,    2,   -1,     0,    1},
                                  { -1,     0,    0,   0,    0,    0,     2,   -2}};
        
        Method method = dudp.getClass().getDeclaredMethod("inverseZigZag", int[].class);
        method.setAccessible(true);
        int[][] orderedZz = (int[][]) method.invoke(dudp, zigZagDu);
        
        assertEquals(du.length, orderedZz.length);
        for (int i = 0; i < du.length; i++) {
            for (int j = 0; j < du.length; j++) {
                 assertEquals(du[i][j], orderedZz[i][j]);
            }
        }
    }
    
    /**
     * Checks normal order restoring from zig zag order for quantization coefficients.
     * This is valid coefficients taken from fx_rate_pair.jpg 
     * 
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    public void testInverseZigZag2() throws IOException, 
                                            NoSuchMethodException, 
                                            SecurityException, 
                                            IllegalAccessException, 
                                            IllegalArgumentException, 
                                            InvocationTargetException 
    {
        int[] zigZagQks = new int[] { 2,   1,    1,   2,   1,   1,   2,   2, 
                                      2,   2,    2,   2,   2,   2,   3,   5, 
                                      3,   3,    3,   3,   3,   6,   4,   4, 
                                      3,   5,    7,   6,   7,   7,   7,   6, 
                                      7,   7,    8,   9,  11,   9,   8,   8, 
                                      10,   8,   7,   7,  10,  13,  10,  10, 
                                      11,  12,  12,  12,  12,   7,   9,  14, 
                                      15,  13,  12,  14,  11,  12,  12,  12};
        
        int[][] Qks = new int[][] {{2,   1,   1,   2,   3,   5,   6,   7},
                                   {1,   1,   2,   2,   3,   7,   7,   7},
                                   {2,   2,   2,   3,   5,   7,   8,   7},
                                   {2,   2,   3,   3,   6,  10,  10,   7},
                                   {2,   3,   4,   7,   8,  13,  12,   9},
                                   {3,   4,   7,   8,  10,  12,  14,  11},
                                   {6,   8,   9,  10,  12,  15,  14,  12},
                                   {9,  11,  11,  12,  13,  12,  12,  12}};
        
        Method method = dudp.getClass().getDeclaredMethod("inverseZigZag", int[].class);
        method.setAccessible(true);
        int[][] orderedQks = (int[][]) method.invoke(dudp, zigZagQks);
        
        assertEquals(Qks.length, orderedQks.length);
        for (int i = 0; i < Qks.length; i++) {
            for (int j = 0; j < Qks.length; j++) {
                 assertEquals(Qks[i][j], orderedQks[i][j]);
            }
        }
    }
    
}
