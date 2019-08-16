package debug;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ImageInHtmlWriter {

    public void create(long height, long width, Pixel[][] pixels) throws FileNotFoundException {
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
