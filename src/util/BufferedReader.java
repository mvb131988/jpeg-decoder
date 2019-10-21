package util;

import java.io.IOException;
import java.io.InputStream;

public class BufferedReader {

    private byte[] buffer;
    private int length;
    private InputStream is;
    private int pos;
    
    //buffer for pushed back characters
    //is used during reading of entropy-coded data when restart intervals
    //might occur. Note: pushback character is already casted from byte to
    //int
    private int[] buffer0;
    //buffer0 size
    private int size0;
    
    public BufferedReader(InputStream is) {
        this.is = is;

        buffer = new byte[16];
        pos = buffer.length;
        length = buffer.length;
        
        buffer0 = new int[2];
    }
    
    public int next() throws IOException {
    	//special case when there are pushed back characters
    	if(size0>0) {
    		int b = buffer0[0];
    		buffer0[0] = buffer0[1];
    		buffer0[1] = 0;
    		size0--;
    		return b;
    	}
    	
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
    
    public void pushBack(int b) {
    	if(size0 == 2) return;
    	buffer0[size0++] = b;
    }
    
    public void close() throws IOException {
        is.close();
    }
    
}
