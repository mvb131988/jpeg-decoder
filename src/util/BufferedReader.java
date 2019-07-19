package util;

import java.io.IOException;
import java.io.InputStream;

public class BufferedReader {

    private byte[] buffer;
    private int length;
    private InputStream is;
    private int pos;
    
    public BufferedReader(InputStream is) {
        this.is = is;

        buffer = new byte[16];
        pos = buffer.length;
        length = buffer.length;
    }
    
    public int next() throws IOException {
        //last chunk of data could be less than buffer.length
        if(pos == length && length < buffer.length) {
            return Integer.MIN_VALUE;
        }
        
        if(pos == length) {
            buffer = new byte[16];
            length = is.read(buffer, 0, buffer.length);
            pos = 0;
        }
        
        if(length == -1) {
            pos = -1;
            return -1;
        }
        
        return buffer[pos++] & 0xff;
    }
    
    public void close() throws IOException {
        is.close();
    }
    
}
