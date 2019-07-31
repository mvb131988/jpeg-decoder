package decoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import markers.ScanHeader;
import util.BufferedReader;

public class ScanDecoderProcedureTest {

    private ScanDecoderProcedure sdp;
    
    @Before
    public void setUp() {
        sdp = new ScanDecoderProcedure();
        sdp.setRidp(Mockito.mock(RestartIntervalDecoderProcedure.class));
    }
    
    @Test
    public void testDecodeFrame() throws IOException {
        //scan header(without 0xff 0xda)
        int[] input = {0x00,0x0c,0x03,0x01,0x00,0x02,0x11,0x03,0x11,0x00,0x3f,0x00,0xf2,
                       0xaa};
        ScanDecoderProcedureTestStream is = new ScanDecoderProcedureTestStream(input);
        
        DecoderContext dc = new DecoderContext();
        sdp.decodeScan(new BufferedReader(is), dc);
        
        ScanHeader sh = dc.scanHeader;
        
        //Scan header length 12
        assertEquals(12, sh.Ls);
        //Number of image components in scan 3
        assertEquals(3, sh.Ns);
        
        //Scan component1 specification parameters
        //Scan component selector 1 
        assertEquals(1, sh.Cs[0]);
        //DC entropy coding table destination selector 0
        assertEquals(0, sh.Td[0]);
        //AC entropy coding table destination selector 0 
        assertEquals(0, sh.Ta[0]);
        
        //Scan component selector 2
        assertEquals(2, sh.Cs[1]);
        //DC entropy coding table destination selector 1
        assertEquals(1, sh.Td[1]);
        //AC entropy coding table destination selector 1
        assertEquals(1, sh.Ta[2]);
        
        //Scan component selector 3 
        assertEquals(3, sh.Cs[2]);
        //DC entropy coding table destination selector 1
        assertEquals(1, sh.Td[1]);
        //AC entropy coding table destination selector 1
        assertEquals(1, sh.Ta[2]);
        
        //Start of spectral or predictor selection 0
        assertEquals(0, sh.Ss);
        //End of spectral selection 63
        assertEquals(63, sh.Se);
        //Successive approximation bit position high 0
        assertEquals(0, sh.Ah);
        //Successive approximation bit position low or point transform 0
        assertEquals(0, sh.Al);
    }
    
    private static class ScanDecoderProcedureTestStream extends InputStream {

        private int[] input;
        private int pos;
        
        public ScanDecoderProcedureTestStream(int[] input) {
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
