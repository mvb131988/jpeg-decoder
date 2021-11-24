package poc;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import poc.BmpFile.Pixel;

public class Main {

  public static void main(String[] args) throws IOException {
    String p = "C:\\endava\\temp\\bmp_test.bmp";
    BmpFileReader bmpReader = new BmpFileReader(p);
    BmpFile img = bmpReader.read();

    if (img == null)
      throw new RuntimeException();

    eraseGBComponents(img.pixels);

    String outP = "C:\\endava\\temp\\bmp_test\\bmp_test_out.bmp";
    BmpFileWriter bmpWriter = new BmpFileWriter(outP);
    bmpWriter.write(img);

    String outRComponent = "C:\\endava\\temp\\bmp_test\\bmp_test_rcomponent_out.txt";
    int[][] component = writeRComponent(img.pixels, outRComponent);

    FourierCompressor fc = new FourierCompressor();
    String outUncompressedComponent = "C:\\endava\\temp\\bmp_test\\bmp_test_uncompressed_component_out.txt";
    int[][] dctCoeffs = fc.compress(component);
    
    String outDctComponent = "C:\\endava\\temp\\bmp_test\\bmp_test_dct_component_out.txt";
    toZero(dctCoeffs, 8010);
    printZeroStat(dctCoeffs);
    writeComponent(dctCoeffs, outDctComponent);
    
    int[][] uncompressedComponent = fc.uncompress(dctCoeffs);
    writeComponent(uncompressedComponent, outUncompressedComponent);
    replaceByUncompressedComponent(img, uncompressedComponent);

    String outUncompressedP = "C:\\endava\\temp\\bmp_test\\bmp_test_uncompressed_out.bmp";
    BmpFileWriter bmpWriter1 = new BmpFileWriter(outUncompressedP);
    bmpWriter1.write(img);
  }

  private static void eraseGBComponents(Pixel[][] canvas) {
    for (int i = 0; i < canvas.length; i++) {
      for (int j = 0; j < canvas[i].length; j++) {
//        canvas[i][j].r = 0;
        canvas[i][j].g = 0;
        canvas[i][j].b = 0;
      }
    }
  }

  private static int[][] writeRComponent(Pixel[][] canvas, String outRComponent) throws IOException {
    int[][] component = new int[canvas.length][canvas[0].length];

    Path path = Paths.get(outRComponent);
    try (PrintWriter pw = new PrintWriter(Files.newOutputStream(path))) {
      for (int i = 0; i < canvas.length; i++) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < canvas[i].length; j++) {
          sb.append(canvas[i][j].r);
          if (j < canvas[i].length - 1)
            sb.append(",");

          component[i][j] = canvas[i][j].r;
        }
        pw.write(sb.toString());
        pw.println();
      }
    }

    return component;
  }

  private static void writeComponent(int[][] component, String outRComponent) throws IOException {
    Path path = Paths.get(outRComponent);
    try (PrintWriter pw = new PrintWriter(Files.newOutputStream(path))) {
      for (int i = 0; i < component.length; i++) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < component[i].length; j++) {
          sb.append(component[i][j]);
          if (j < component[i].length - 1)
            sb.append(",");

        }
        pw.write(sb.toString());
        pw.println();
      }
    }
  }

  private static void replaceByUncompressedComponent(BmpFile img, int[][] uncompressedComponent) {
    Pixel[][] canvas = img.pixels;
    for (int i = 0; i < canvas.length; i++) {
      for (int j = 0; j < canvas[i].length; j++) {
        canvas[i][j].r = uncompressedComponent[i][j];
        canvas[i][j].g = 0;
        canvas[i][j].b = 0;
      }
    }
  }
  
  private static void toZero(int[][] dctComponent, int v) {
    for (int i = 0; i < dctComponent.length; i++) {
      for (int j = 0; j < dctComponent[i].length; j++) {
        if(Math.abs(dctComponent[i][j]) < v) dctComponent[i][j] = 0;
      }
    }
  }

  private static void printZeroStat(int[][] dctComponent) {
    int countZero = 0;
    for (int i = 0; i < dctComponent.length; i++) {
      for (int j = 0; j < dctComponent[i].length; j++) {
        if(dctComponent[i][j] == 0) countZero++;
      }
    }
    
    System.out.println("Total dct coeffs: " + dctComponent.length*dctComponent[0].length);
    System.out.println("Total zero coeffs: " + countZero);
    
    double rate = ((double)countZero/(dctComponent.length*dctComponent[0].length));
    System.out.println("Zero coeffs% " + (int)(rate*100));
  }

}
