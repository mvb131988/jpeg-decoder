package poc;

public class BmpBlockSplitter {

  private int row;
  private int column;
  private int[][] component;
  
  public BmpBlockSplitter(int[][] component) {
    this.component = component;
    this.row = 0;
    this.column = 0;
  }
  
  public int[][] next() {
    int[][] block = new int[8][8];
    
    for(int i=row, i0=0; i<row+8; i++,i0++) {
      for(int j=column, j0=0; j<column+8; j++,j0++) {
        if(i<component.length && j<component[0].length) {
          block[i0][j0] = component[i][j];
        }
      }
    }
    
    if(column < component[0].length) column+=8;
    else {
      column = 0;
      row += 8;
    }
    
    return block;
  }
  
  public boolean hasNext() {
    return row<component.length;
  }
  
}
