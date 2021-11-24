package poc;

//http://fourier.eng.hmc.edu/e161/lectures/dct/node1.html
public class FourierCompressor {

  private FourierDFTCompressor dftCompressor = new FourierDFTCompressor();

  public int[][] compressUncompress(int[][] component) {
    BmpBlockSplitter bs = new BmpBlockSplitter(component);
    BmpBlockAssembler ba = new BmpBlockAssembler(component.length, component[0].length);
    while (bs.hasNext()) {
      int[][] block = bs.next();
      int[][] dctCoeffs = dct0(block);
      int[][] restoredBlock = idct0(dctCoeffs);
      ba.add(restoredBlock);
    }
    return ba.getComponent();
  }

  public int[][] uncompress(int[][] coeffs) {
    BmpBlockSplitter bs = new BmpBlockSplitter(coeffs);
    BmpBlockAssembler ba = new BmpBlockAssembler(coeffs.length, coeffs[0].length);
    while (bs.hasNext()) {
      int[][] blockCoeffs = bs.next();
      int[][] restoredBlock = idct0(blockCoeffs);
      ba.add(restoredBlock);
    }
    return ba.getComponent();
  }
  
  public int[][] compress(int[][] component) {
    BmpBlockSplitter bs = new BmpBlockSplitter(component);
    BmpBlockAssembler ba = new BmpBlockAssembler(component.length, component[0].length);
    while (bs.hasNext()) {
      int[][] block = bs.next();

      int[][] dctCoeffs = dct0(block);
      ba.add(dctCoeffs);
    }
    
    return ba.getComponent();
  }

  public int[][] dct(int[][] block) {
    int[][] coeffs = new int[8][8];
    for (int u = 0; u < 8; u++) {
      for (int v = 0; v < 8; v++) {
        for (int i = 0; i < 8; i++) {
          for (int j = 0; j < 8; j++) {
            coeffs[u][v] = coeffs[u][v]
                + (int) (block[i][j] * Math.cos((2 * i + 1) * u * 3.14 / 16) * Math.cos((2 * j + 1) * v * 3.14 / 16));
          }
        }
        coeffs[u][v] = coeffs[u][v] / 4;
        if (u == 0 && v == 0)
          coeffs[u][v] = coeffs[u][v] / 2;
      }
    }
    return coeffs;
  }

  public int[][] idct(int[][] dctCoeffs) {
    int[][] block = new int[8][8];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        for (int u = 0; u < 8; u++) {
          for (int v = 0; v < 8; v++) {
            if (u == 0 && v == 0)
              block[i][j] = block[i][j] + (int) (dctCoeffs[u][v] / 2 * Math.cos((2 * i + 1) * u * 3.14 / 16)
                  * Math.cos((2 * j + 1) * v * 3.14 / 16));
            else
              block[i][j] = block[i][j] + (int) (dctCoeffs[u][v] * Math.cos((2 * i + 1) * u * 3.14 / 16)
                  * Math.cos((2 * j + 1) * v * 3.14 / 16));
          }
        }
        block[i][j] = block[i][j] / 4;
      }
    }
    return block;
  }

  public int[][] dct0(int[][] block) {
    int n = block.length;
    int m = block[0].length;
    int[][] coeffs = new int[n][m];

    for (int u = 0; u < n; u++) {
      for (int v = 0; v < m; v++) {
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < m; j++) {
            coeffs[u][v] = coeffs[u][v] + (int) (block[i][j] * Math.cos((3.14 / 16) * (2 * i + 1) * u)
                * Math.cos((3.14 / 16) * (2 * j + 1) * v));
          }
        }
      }
    }

    return coeffs;
  }

  public int[][] idct0(int[][] coeffs) {
    int[][] block = new int[8][8];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        for (int u = 0; u < 8; u++) {
          for (int v = 0; v < 8; v++) {
            block[i][j] = block[i][j] + (int) (coeffs[u][v] * Math.cos((3.14 / 16) * (2 * i + 1) * u)
                * Math.cos((3.14 / 16) * (2 * j + 1) * v));
          }
        }
        block[i][j] = block[i][j] / 64;
      }
    }

    return block;
  }

  public int[] dctRow(int[] block) {
    int[] coeffs = new int[8];

    for (int u = 0; u < 8; u++) {
      for (int i = 0; i < 8; i++) {
        coeffs[u] = coeffs[u] + (int) (block[i] * Math.cos((3.14 / 16) * (2 * i + 1) * u));
      }
    }

    return coeffs;
  }

  public int[] idctRow(int[] coeffs) {
    int[] block = new int[8];

    for (int i = 0; i < 8; i++) {
      for (int u = 0; u < 8; u++) {
        block[i] = block[i] + (int) (coeffs[u] * Math.cos((3.14 / 16) * (2 * i + 1) * u));
      }
      block[i] = block[i] / 8;
    }

    return block;
  }

}
