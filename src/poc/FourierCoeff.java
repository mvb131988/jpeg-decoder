package poc;

public class FourierCoeff {

  //real part
  public int r;
  //imaginary part
  public int i;
  
  public FourierCoeff() {
    
  }
  
  public FourierCoeff(int r, int i) {
    this.r = r;
    this.i = i;
  }
  
  public FourierCoeff add(FourierCoeff fc) {
    return new FourierCoeff(this.r+fc.r, this.i+fc.i);
  }
  
}
