package decoder;

import java.io.IOException;

import util.FileSystemComponentWriter;
import util.FileSystemDUReader;
import util.FileSystemDUWriter;
import util.FileSystemRowReader;
import util.FileSystemRowWriter;

/**
 * Accumulates all MCUs of a specific component and transforms them into two dimensional 
 * array of samples(position of samples corresponds to final pixel positions with respect 
 * to vertical and horizontal scaling factors).
 * 
 * During transformation series of temporary files is used to minimize memory(RAM) consumption.
 * 
 * Note: Here by MCU is defined component related part of decoded MCU(for example MCU could contain 
 * 6 DUs: 4DUs from component1(would be named as MCU, however not the entire, but just component1 specific part), 
 * 1DU from component2, 1DU from component3) 
 */
public class ComponentInFileSystemAssembler implements AutoCloseable {

	//assembler id, used for temporary file names generation(is equal to component id {0,1,2})
	private int id;

	//original(no extensions) number of samples in a row per component 
	private int xs;
	
	//original(no extensions) number of samples in a column per component
	private int ys;
	
    //horizontal sampling factor(number of DU columns in MCUs part of this component)
    private int hs;
    
    //vertical sampling factor(number of DU rows in MCUs part of this component)
    private int vs;
    
    //number of DUs of current MCU(this is important) that has already been accumulated
    //This is a counter of already read DU within a single MCU
    private int duRead;
	
    //number of already read MCUs 
    private int totalMcuRead;
    
    //number of MCUs(number of MCU columns) in a row
    private int mcuHs;
    
    //number of DUs in a DU row including padding
	private int extDuCount;
	
	//number of samples in a component row including padding
	private int extSamplesCount;
	
	//number of entire rows(the whole row of the component) that are already written
	//in the component/result file
	private int rowCount;
	
    private FileSystemDUWriter[] fsduws;
    
    private FileSystemComponentWriter fscw;
    
    public ComponentInFileSystemAssembler(int id, int hs, int vs, int xs, int ys, int mcuHs) throws IOException {
        this.id = id;
        this.xs = xs;
        this.ys = ys;
    	this.hs = hs;
        this.vs = vs;
        
        this.duRead = 0;
        this.totalMcuRead = 0;
        this.mcuHs = mcuHs;
        this.extDuCount = mcuHs*hs;
        this.extSamplesCount = extDuCount*8;
        this.rowCount = 0;
        
        this.fsduws = new FileSystemDUWriter[vs];
        for(int i=0; i<vs; i++) 
        	this.fsduws[i] = new FileSystemDUWriter(id, i);
        
        this.fscw = new FileSystemComponentWriter(id);
    }
    
    /**
     * Consider two dimensional arrays:
     * 
     * arr1|arr2
     * ---------
     * 1 2 | 5 6
     * 3 4 | 7 8
     * 
     * There is an order arr1 comes first, arr2 comes second.
     * When arrays are written into the file rows ordering would be:
     * 
     * arr1-row1 | arr1-row2 | arr2-row1 | arr2-row2
     * ---------------------------------------------
     * 1 2       | 3 4       | 5 6       | 7 8
     * 
     * Let's assume arr1 and arr2 represent samples of the component(size 2x4 samples).
     * Knowing the number of columns(4) per row, its possible to read file row by row
     * 
     * row1 1 2 3 4
     * row2 5 6 7 8
     * 
     * This is the wrong order, caused by the component break up into two arrays and
     * arrays sequential entrance. Here is a contradiction: in order to get all samples of the
     * first row it's necessary to get both arrays at the same time, that is impossible by the
     * way arrays arrive.
     * 
     * Let's define reordering procedure.
     * Given arrays height(2 rows) create two files(one for the first row, one for the second).
     * Write row1 of arr1 into file1, and row2 of arr2 into file2. Repeat the same for arr2.
     * 
     * In the output would have(what is necessary to achieve):
     * file1 1 2 5 6
     * file2 3 4 7 8
     * 
     * Merge file1 and file2 into a single file: 1 2 5 6 3 4 7 8
     * ========================================================================================
     * 
     * Application to the real life example:
     * 
     * Consider MCU row:
     * row1              | row2
     * ----------------------------------------- 
     * MCU1    | MCU2    | MCU3      | MCU4
     * -----------------------------------------
     * DU1 DU2 | DU5 DU6 | DU9  DU10 | DU13 DU14
     * DU3 DU4 | DU7 DU8 | DU11 DU12 | DU15 DU16
     * 
     * There is a correspondence MCU row1 <-> 2 DU rows <-> 16 samples rows (IMPORTANT: 16 full samples row
     * of the component, MCU row2 would represent next 16 full samples row that go immediately below first
     * 16 full samples row).
     * 
     * Reordering procedure would have the following view:
     * 
     * Step1: 
     * Given MCU row break it up in DU rows:
     * 
     * MCU row1 | MCU1    | MCU2
     * ---------------------------
     * DU row1 | DU1 DU2 | DU5 DU6
     * DU row2 | DU3 DU4 | DU7 DU8
     * 
     * Create 2 files, write DU row1 into the first file, DU row2 into the second
     * 
     * Step2:
     * Open up file1(for DU row1), create 8 files(each file for a single DU row), read
     * DU1, write row1 of DU1 to file1, ... , write row8 of DU1 to file8. Take DU2,
     * write row1 of DU2 to file1, ... , write row8 of DU1 to file8(file1,...,file8) 
     * already opened at the beginning.
     * 
     * Step3:
     * Merge file1, ... , file8 into final output file of the component samples.
     * Files ordering from 1 to 8.
     * 
     * @param du
     * @throws Exception 
     */
    public void add(int[][] du) throws Exception {
    	int fileNumber = duRead/hs; 
    	//save du into fileNumber file
    	fsduws[fileNumber].write(du);
    	
    	duRead++;
    	
    	//move to next MCU
    	if(duRead == vs*hs) {
    		duRead = 0; 
    		totalMcuRead++;
    		//end of MCU row is reached
    		if(mcuHs == totalMcuRead) {
    			//merge DU rows
    			unifyComponents();
    			
    			totalMcuRead = 0;
    		}
    	}
    }
    
    private void unifyComponents() throws Exception {
    	//end write process
    	for(FileSystemDUWriter fsduw: fsduws) fsduw.close();
    	
    	//read DU rows(DU row by DU row), break up each DU into a separate line,
    	//save each line into corresponding file. When DU row end is reached, 
    	//merge each line into the result file(corresponds to component)
    	for(int i=0; i<vs; i++) {
    		FileSystemDUReader fsdur = new FileSystemDUReader(id, i);
    		
    		FileSystemRowWriter[] fsrws = new FileSystemRowWriter[8];
    		for(int l=0; l<8; l++) 
    			fsrws[l] = new FileSystemRowWriter(id, i, l, extSamplesCount);
    		
    		for(int j=0; j<extDuCount; j++) {
    			int[][] du = fsdur.read();
    			for(int k=0; k<8; k++)
    				fsrws[k].write(du[k]);
    		}
    		
    		for(int l=0; l<8; l++) fsrws[l].close();
    		fsdur.close();
    		
    		//at this moment 8 files, correspondent to each DU line, are created
    		//need to be merged into the result file
    		FileSystemRowReader[] fsrrs = new FileSystemRowReader[8];
    		for(int l=0; l<8; l++) 
    			fsrrs[l] = new FileSystemRowReader(id, i, l, this.xs);
    		
    		for(int l=0; l<8; l++) {
    			if(rowCount < ys) {
	    			//samples of the current row already written so far 
	    			int sCount = 0;
	    			while(sCount<xs) {
	    				this.fscw.write(fsrrs[l].read());
	    				sCount++;
	    			}
	    			rowCount++;
    			}
    		} 
    		
    		for(int l=0; l<8; l++) fsrrs[l].close();
    	}
    	
    	//resume write process
    	for(int i=0; i<vs; i++) 
        	this.fsduws[i] = new FileSystemDUWriter(id, i);
    }

	@Override
	public void close() throws Exception {
		for(FileSystemDUWriter fsduw: this.fsduws) fsduw.close();
		this.fscw.close();
	}
    
}
