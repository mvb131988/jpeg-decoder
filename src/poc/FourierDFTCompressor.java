package poc;

public class FourierDFTCompressor {

  public FourierCoeff[] dftRow(int[] block) {
    FourierCoeff[] coeffs = new FourierCoeff[8];
    for (int i = 0; i < 8; i++)
      coeffs[i] = new FourierCoeff();

    for (int u = 0; u < 8; u++) {
      double w = (2 * 3.14 * u) / 8;
      for (int i = 0; i < 8; i++) {
        int real = (int) (block[i] * Math.cos(w * i));
        int img = (int) (block[i] * Math.sin(w * i));
        coeffs[u] = coeffs[u].add(new FourierCoeff(real, img));
      }
    }

    return coeffs;
  }

  public int[] idftRow(FourierCoeff[] coeffs) {
    int[] block = new int[8];
    int[] imgBlock = new int[8];

    for (int i = 0; i < 8; i++) {
      for (int u = 0; u < 8; u++) {
        double w = (2 * 3.14 * u) / 8;
        int real = (int) (coeffs[u].r * Math.cos(w * i) - coeffs[u].i * Math.sin(w * i));
        int img = (int) (coeffs[u].r * Math.sin(w * i) + coeffs[u].i * Math.cos(w * i));
        block[i] = block[i] + real;
        imgBlock[i] = imgBlock[i] + img;
      }
      block[i] = block[i] / 8;
      imgBlock[i] = imgBlock[i] / 8;
    }

    return block;
  }

}
