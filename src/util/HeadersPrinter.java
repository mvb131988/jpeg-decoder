package util;

import java.util.LinkedList;
import java.util.Queue;

public class HeadersPrinter {

    private Queue<String> headers;
    
    public HeadersPrinter() {
        this.headers = new LinkedList<>();
    }
    
    public void add(int type) {
        if(type == 0xd8) {headers.add("image start"); return;}
        if(type == 0xd9) {headers.add("image end"); return;}
        if(type == 0xe0) {headers.add("application header"); return;}
        if(type == 0xdb) {headers.add("quantization table"); return;}
        if(type == 0xc0) {headers.add("frame header"); return;}
        if(type == 0xc4) {headers.add("Huffman tables"); return;}
        if(type == 0xda) {headers.add("scan header"); return;}
        headers.add("unknown: " + Integer.toHexString(type));
    }
    
    public void print() {
//        for(String h: headers)
//            System.out.println(h);
    }
    
}
