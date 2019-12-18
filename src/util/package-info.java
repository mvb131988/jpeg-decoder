/**
 * There is a series of file system readers and writers used at different stages
 * of image processing:
 * 
 * (1) FileSytemMCUWriter, 
 * 	   FileSytemMCUReader 
 * 
 * 	   Reads/writes all decoded MCUs
 * 
 * (2) FileSytemDUWriter, 
 * 	   FileSytemDUReader
 * 
 *     For each image component series of du writers is created.
 * 	   Each series of DU writers saves DUs of only one specific component. Each DU writer from the
 * 	   series of DU writers saves DUs of only one DU row(image could contain more than one DU row).
 * 
 * 	   In:		
 * 	   Component1(with 2 DU rows) - DU1 DU2 DU3 DU4
 * 	   
 * 	   Out:
 * 	   file1 - DU1 DU2
 * 	   file2 - DU3 DU4		
 *     				
 * 
 * (3) FileSytemRowWriter, 
 * 	   FileSytemRowReader 
 * 
 * 	   Transforms DU rows into image rows(where each sample is placed in the correct position)
 *     
 *     In: DU1 DU2
 *     
 *     Out: row11 row12 ... row1n
 *     		.....................
 *     		rowm1 rowm2 ... rowmn
 *     
 * (4) FileSystemComponentReader, 
 * 	   FileSystemExtendedRowWriter, 
 * 	   FileSystemExtendedRowReader,
 *     FileSystemExtendedComponentWriter 
 *     
 *     Extends all components to the size of the biggest one
 *     
 * (5) FileSystemExtendedComponentReader 
 * 
 * 	   Reads extended components    
 *  
 */

package util;