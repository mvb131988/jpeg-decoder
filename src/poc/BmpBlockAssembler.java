package poc;

public class BmpBlockAssembler {

  private int row;
  private int column;
  private int[][] component;
  
  public BmpBlockAssembler(int h, int w) {
    component = new int[h][w];
    this.row = 0;
    this.column = 0;
  }
  
  public void add(int[][] block) {
    for(int i=row, i0=0; i<row+8; i++,i0++) {
      for(int j=column, j0=0; j<column+8; j++,j0++) {
        if(i<component.length && j<component[0].length) {
          component[i][j] = block[i0][j0];
        }
      }
    }
    
    if(column < component[0].length) column+=8;
    else {
      column = 0;
      row += 8;
    }
  }
  
  public int[][] getComponent() {
    return component;
  }
  
}
