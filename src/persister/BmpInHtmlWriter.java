package persister;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * For debug purpose only.
 * Outputs bmp two dimensional array of pixels into html format,
 * avoiding creation of bmp header. 
 */
public class BmpInHtmlWriter {

    public void create(Pixel[][] pixels) throws FileNotFoundException {
    	long height = pixels.length;
    	long width = pixels[0].length;
    	StringBuilder text = new StringBuilder();
        
        text.append("<html>"); 
        text.append("<body>");
        text.append("<table cellspacing=\"1\" cellpadding=\"0\">");
        
        for(int i=0; i<height; i++) {
            text.append("    <tr>");
            for(int j=0; j<width; j++) {
                text.append("<td>");
                text.append("<svg width=\"3\" height=\"3\">");
                text.append("  <rect width=\"3\" height=\"3\" style=fill:");
                text.append("rgb(");
                text.append(pixels[i][j].r);
                text.append(",");
                text.append(pixels[i][j].g);
                text.append(",");
                text.append(pixels[i][j].b);
                text.append(")");
                text.append(" />"); 
                text.append("</svg>");
                text.append("</td>");
            }
            text.append("    </tr>");
        }
        
        text.append("    </tr>");
        text.append("  </table>");
        text.append("</body>");
        text.append("</html>");
        
        try(PrintWriter out = new PrintWriter("image.html")) {
            out.println(text);
        }
    }
    
}
