package decoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import markers.FrameHeader;
import util.BufferedReader;

public class FrameDecoderProcedureTest {

    private FrameDecoderProcedure fdp;
    
    @Before
    public void setUp() {
        fdp = new FrameDecoderProcedure();
        fdp.setSdp(Mockito.mock(ScanDecoderProcedure.class));
    }
    
    @Test
    public void testDecodeFrame() throws IOException {
        //frame header(without 0xff 0xc0) + Huffman tables + scan header + two bytes of encoded data
        int[] input = {0x00,0x11,0x08,0x07,0x58,0x0a,0x6e,0x03,0x01,0x22,0x00,0x02,0x11,0x01,0x03,0x11,
                       0x01,0xff,0xc4,0x00,0x1f,0x00,0x00,0x01,0x05,0x01,0x01,0x01,0x01,0x01,0x01,0x00,
                       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,
                       0x0a,0x0b,0xff,0xc4,0x00,0xb5,0x10,0x00,0x02,0x01,0x03,0x03,0x02,0x04,0x03,0x05,
                       0x05,0x04,0x04,0x00,0x00,0x01,0x7d,0x01,0x02,0x03,0x00,0x04,0x11,0x05,0x12,0x21,
                       0x31,0x41,0x06,0x13,0x51,0x61,0x07,0x22,0x71,0x14,0x32,0x81,0x91,0xa1,0x08,0x23,
                       0x42,0xb1,0xc1,0x15,0x52,0xd1,0xf0,0x24,0x33,0x62,0x72,0x82,0x09,0x0a,0x16,0x17,
                       0x18,0x19,0x1a,0x25,0x26,0x27,0x28,0x29,0x2a,0x34,0x35,0x36,0x37,0x38,0x39,0x3a,
                       0x43,0x44,0x45,0x46,0x47,0x48,0x49,0x4a,0x53,0x54,0x55,0x56,0x57,0x58,0x59,0x5a,
                       0x63,0x64,0x65,0x66,0x67,0x68,0x69,0x6a,0x73,0x74,0x75,0x76,0x77,0x78,0x79,0x7a,
                       0x83,0x84,0x85,0x86,0x87,0x88,0x89,0x8a,0x92,0x93,0x94,0x95,0x96,0x97,0x98,0x99,
                       0x9a,0xa2,0xa3,0xa4,0xa5,0xa6,0xa7,0xa8,0xa9,0xaa,0xb2,0xb3,0xb4,0xb5,0xb6,0xb7,
                       0xb8,0xb9,0xba,0xc2,0xc3,0xc4,0xc5,0xc6,0xc7,0xc8,0xc9,0xca,0xd2,0xd3,0xd4,0xd5,
                       0xd6,0xd7,0xd8,0xd9,0xda,0xe1,0xe2,0xe3,0xe4,0xe5,0xe6,0xe7,0xe8,0xe9,0xea,0xf1,
                       0xf2,0xf3,0xf4,0xf5,0xf6,0xf7,0xf8,0xf9,0xfa,0xff,0xc4,0x00,0x1f,0x01,0x00,0x03,
                       0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x01,
                       0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0xff,0xc4,0x00,0xb5,0x11,0x00,
                       0x02,0x01,0x02,0x04,0x04,0x03,0x04,0x07,0x05,0x04,0x04,0x00,0x01,0x02,0x77,0x00,
                       0x01,0x02,0x03,0x11,0x04,0x05,0x21,0x31,0x06,0x12,0x41,0x51,0x07,0x61,0x71,0x13,
                       0x22,0x32,0x81,0x08,0x14,0x42,0x91,0xa1,0xb1,0xc1,0x09,0x23,0x33,0x52,0xf0,0x15,
                       0x62,0x72,0xd1,0x0a,0x16,0x24,0x34,0xe1,0x25,0xf1,0x17,0x18,0x19,0x1a,0x26,0x27,
                       0x28,0x29,0x2a,0x35,0x36,0x37,0x38,0x39,0x3a,0x43,0x44,0x45,0x46,0x47,0x48,0x49,
                       0x4a,0x53,0x54,0x55,0x56,0x57,0x58,0x59,0x5a,0x63,0x64,0x65,0x66,0x67,0x68,0x69,
                       0x6a,0x73,0x74,0x75,0x76,0x77,0x78,0x79,0x7a,0x82,0x83,0x84,0x85,0x86,0x87,0x88,
                       0x89,0x8a,0x92,0x93,0x94,0x95,0x96,0x97,0x98,0x99,0x9a,0xa2,0xa3,0xa4,0xa5,0xa6,
                       0xa7,0xa8,0xa9,0xaa,0xb2,0xb3,0xb4,0xb5,0xb6,0xb7,0xb8,0xb9,0xba,0xc2,0xc3,0xc4,
                       0xc5,0xc6,0xc7,0xc8,0xc9,0xca,0xd2,0xd3,0xd4,0xd5,0xd6,0xd7,0xd8,0xd9,0xda,0xe2,
                       0xe3,0xe4,0xe5,0xe6,0xe7,0xe8,0xe9,0xea,0xf2,0xf3,0xf4,0xf5,0xf6,0xf7,0xf8,0xf9,
                       0xfa,0xff,0xda,0x00,0x0c,0x03,0x01,0x00,0x02,0x11,0x03,0x11,0x00,0x3f,0x00,0xf2,
                       0xaa};
        FrameDecoderProcedureTestStream is = new FrameDecoderProcedureTestStream(input);
        
        DecoderContext dc = new DecoderContext();
        fdp.decodeFrame(new BufferedReader(is), dc);
        
        /////////////////////////////////////////////////////////////////////////////////
        FrameHeader fh = dc.frameHeader;
        
        //size 17 including the length parameter and excluding the two-byte marker
        assertEquals(17, fh.Lf);
        //sample precision 8
        assertEquals(8, fh.P);
        //number of lines(Y however actual x or i on array iteration) 1880
        assertEquals(1880, fh.Y);
        //number of samples per line(X however actual y or j on array iteration) 2670 
        assertEquals(2670, fh.X);
        //number of image components in frame 3
        assertEquals(3, fh.Nf);
        
        assertEquals(3, fh.Cs.length);
        assertEquals(3, fh.Hs.length);
        assertEquals(3, fh.Vs.length);
        assertEquals(3, fh.Tqs.length);
        
        //component1
        //component identifier 1
        assertEquals(1, fh.Cs[0]);
        //horizontal sampling factor 2
        assertEquals(2, fh.Hs[0]);
        //vertical sampling factor 2
        assertEquals(2, fh.Vs[0]);
        //quantization table destination selector 0
        assertEquals(0, fh.Tqs[0]);
        
        //component2
        //component identifier 2
        assertEquals(2, fh.Cs[1]);
        //horizontal sampling factor 1
        assertEquals(1, fh.Hs[1]);
        //vertical sampling factor 1
        assertEquals(1, fh.Vs[1]);
        //quantization table destination selector 1
        assertEquals(1, fh.Tqs[1]);
        
        //component3
        //component identifier 3
        assertEquals(3, fh.Cs[2]);
        //horizontal sampling factor 1
        assertEquals(1, fh.Hs[2]);
        //vertical sampling factor 1
        assertEquals(1, fh.Vs[2]);
        //quantization table destination selector 1
        assertEquals(1, fh.Tqs[2]);
        
        //assert dimension calculation
        /////////////////////////////////////////////////////////////////////////////////
        DimensionsContext dimc = dc.dimensionsContext;
        
        assertEquals(3, dimc.Xs.length);
        assertEquals(3, dimc.Ys.length);
        assertEquals(3, dimc.extXs.length);
        assertEquals(3, dimc.extYs.length);
        assertEquals(3, dimc.dataUnitsXs.length);
        assertEquals(3, dimc.dataUnitsYs.length);
        assertEquals(3, dimc.extXDataUnit.length);
        assertEquals(3, dimc.extYDataUnit.length);
        assertEquals(3, dimc.finalExtXs.length);
        assertEquals(3, dimc.finalExtYs.length);
        
        //X = fh.X = 2670, Y = fh.Y = 1880
        //Hmax = max{Hs[0],Hs[1],Hs[2]} = max{2,1,1} = 2
        //Vmax = max{Vs[0],Vs[1],Vs[2]} = max{2,1,1} = 2
        //Xs[i] = ceiling(X*Hs[i]/Hmax)
        //Ys[i] = ceiling(Y*Vs[i]/Vmax)
        
        //Xs[0] = 2670*2/2 = 2670
        assertEquals(2670, dimc.Xs[0]);
        //Ys[0] = 1880*2/2 = 1880
        assertEquals(1880, dimc.Ys[0]);
        
        //Xs[1] = 2670*1/2 = 1335
        assertEquals(1335, dimc.Xs[1]);
        //Ys[1] = 1880*1/2 = 940
        assertEquals(940, dimc.Ys[1]);
        
        //Xs[2] = 2670*1/2 = 1335
        assertEquals(1335, dimc.Xs[2]);
        //Ys[2] = 1880*1/2 = 940
        assertEquals(940, dimc.Ys[2]);
        
        //Extend Xs[i], Ys[i] in order to be multiple of 8
        //extended Xs[0] = Xs[0]/8 = 2670/8 = 333.75 -> 334*8 = 2672
        assertEquals(2672, dimc.extXs[0]);
        //extended Ys[0] = Ys[0]/8 = 1880/8 = 235 -> 235*8 = 1880
        assertEquals(1880, dimc.extYs[0]);
        
        //extended Xs[1] = Xs[1]/8 = 1335/8 = 166.875 -> 167*8 = 1336
        assertEquals(1336, dimc.extXs[1]);
        //extended Ys[1] = Ys[1]/8 = 940/8 = 117.5 -> 118 = 944
        assertEquals(944, dimc.extYs[1]);
        
        //extended Xs[2] = Xs[2]/8 = 1335/8 = 166.875 -> 167*8 = 1336
        assertEquals(1336, dimc.extXs[2]);
        //extended Ys[2] = Ys[2]/8 = 940/8 = 117.5 -> 118 = 944
        assertEquals(944, dimc.extYs[2]);
        
        //number of data units per component
        //per columns: extended Xs[0] / 8 = 2672/8 = 334
        assertEquals(334, dimc.dataUnitsXs[0]);
        //per rows: extended Ys[0] / 8 = 1880/8 = 235
        assertEquals(235, dimc.dataUnitsYs[0]);
        
        //per columns: extended Xs[1] / 8 = 1336/8 = 167
        assertEquals(167, dimc.dataUnitsXs[1]);
        //per rows: extended Ys[1] / 8 = 944/8 = 118
        assertEquals(118, dimc.dataUnitsYs[1]);
        
        //per columns: extended Xs[2] / 8 = 1336/8 = 167
        assertEquals(167, dimc.dataUnitsXs[2]);
        //per rows: extended Ys[2] / 8 = 944/8 = 118
        assertEquals(118, dimc.dataUnitsYs[2]);
        
        //number of extended data units per component
        //per columns: number of data units 1 component / Hs[0] = 334/2 = 167 -> 167*2 -> 334
        assertEquals(334, dimc.extXDataUnit[0]);
        //per rows: number of data units 1 component / Vs[0] = 235/2 = 117.5 -> 118*2 -> 236
        assertEquals(236, dimc.extYDataUnit[0]);
        
        //per columns: number of data units 2 component / Hs[1] = 167/1 = 167 -> 167*1 -> 167
        assertEquals(167, dimc.extXDataUnit[1]);
        //per columns: number of data units 2 component / Vs[1] = 118/1 = 118 -> 118*1 -> 118
        assertEquals(118, dimc.extYDataUnit[1]);
        
        //per columns: number of data units 3 component / Hs[2] = 167/1 = 167 -> 167*1 -> 167
        assertEquals(167, dimc.extXDataUnit[2]);
        //per columns: number of data units 3 component / Vs[2] = 118/1 = 118 -> 118*1 -> 118
        assertEquals(118, dimc.extYDataUnit[2]);
        
        //second extension of samples number per line/column(each extended data unit corresponds to 
        //8 additional samples)
        //per columns: final extended Xs[0] = extended Xs[0] + (block diff between extended and initial
        //             number of data blocks)*8 = 2672 + (334-334)*8 = 2672
        assertEquals(2672, dimc.finalExtXs[0]);
        //per row: final extended Ys[0] = extended Ys[0] + (block diff between extended and initial
        //         number of data blocks)*8 = 1880 + (236-235)*8 = 1888
        assertEquals(1888, dimc.finalExtYs[0]);
        
        //per columns: final extended Xs[1] = extended Xs[1] + (block diff between extended and initial
        //             number of data blocks)*8 = 1336 + (167-167)*8 = 1336
        assertEquals(1336, dimc.finalExtXs[1]);
        //per row: final extended Ys[1] = extended Ys[1] + (block diff between extended and initial
        //         number of data blocks)*8 = 944 + (118-118)*8 = 944
        assertEquals(944, dimc.finalExtYs[1]);
        
        //per columns: final extended Xs[2] = extended Xs[2] + (block diff between extended and initial
        //             number of data blocks)*8 = 1336 + (167-167)*8 = 1336
        assertEquals(1336, dimc.finalExtXs[2]);
        //per row: final extended Ys[2] = extended Ys[2] + (block diff between extended and initial
        //         number of data blocks)*8 = 944 + (118-118)*8 = 944
        assertEquals(944, dimc.finalExtYs[2]);
    }
    
    private static class FrameDecoderProcedureTestStream extends InputStream {

        private int[] input;
        private int pos;
        
        public FrameDecoderProcedureTestStream(int[] input) {
            this.input = input;
        }
        
        @Override
        public int read() throws IOException {
            return 0;
        }
        
        public int read(byte b[], int off, int len) throws IOException {
            int readBytes = 0;
            for(int i=0; i<len; i++) {
                if(pos < input.length) {
                    readBytes++;
                    b[i] = (byte) input[pos++];
                }
            } 
            return readBytes;
        }
        
    }
}
