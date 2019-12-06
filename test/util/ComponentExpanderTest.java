package util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import decoder.DecoderContext;
import decoder.DimensionsContext;
import markers.FrameHeader;

public class ComponentExpanderTest {

	private ComponentExpander componentExpander = new ComponentExpander();
	
	/**
	 * Input(3 components) before extension:
	 * ------------------------------------
	 * component1 | component2 | component3
	 * ------------------------------------
	 * 1 2        | 1          | 2 
	 * 3 4		  |  		   |
	 * 
	 * Output(3 components) after extension:
	 * ------------------------------------
	 * component1 | component2 | component3
	 * ------------------------------------
	 * 1 2        | 1 1        | 2 2 
	 * 3 4		  | 1 1 	   | 2 2
	 * @throws Exception 
	 */
	@Test
	public void testExtend() throws Exception {
		//////// Input components together with decoder context ///////////
		FileSystemComponentWriter fscw0 = new FileSystemComponentWriter(0);
		fscw0.write(1); fscw0.write(2); fscw0.write(3); fscw0.write(4);
		fscw0.close();
		
		FileSystemComponentWriter fscw1 = new FileSystemComponentWriter(1);
		fscw1.write(1);
		fscw1.close();
		
		FileSystemComponentWriter fscw2 = new FileSystemComponentWriter(2);
		fscw2.write(2);
		fscw2.close();
		
		DecoderContext dc = new DecoderContext();
		dc.frameHeader = new FrameHeader();
		dc.frameHeader.Vs = new int[] {2, 1, 1};
		dc.frameHeader.Hs = new int[] {2, 1, 1};
		dc.frameHeader.Nf = 3;
		DimensionsContext dimc = new DimensionsContext();
		dimc.Xs = new int[] {2,1,1};
		dimc.Ys = new int[] {2,1,1};
		dc.dimensionsContext = dimc;
		///////////////////////////////////////////////////////////////////
		
		componentExpander.extend(dc);
		
		//////////////////////// Asserts output ///////////////////////////
		FileSystemExtendedComponentReader fsecr0 = new FileSystemExtendedComponentReader(0);
		//row1
		assertEquals(1, fsecr0.read()); assertEquals(2, fsecr0.read());
		//row2
		assertEquals(3, fsecr0.read()); assertEquals(4, fsecr0.read());
		//EOF
		assertEquals(-1, fsecr0.read());
		fsecr0.close();
		
		FileSystemExtendedComponentReader fsecr1 = new FileSystemExtendedComponentReader(1);
		//row1
		assertEquals(1, fsecr1.read()); assertEquals(1, fsecr1.read());
		//row2
		assertEquals(1, fsecr1.read()); assertEquals(1, fsecr1.read());
		//EOF
		assertEquals(-1, fsecr1.read());
		fsecr1.close();
		
		FileSystemExtendedComponentReader fsecr2 = new FileSystemExtendedComponentReader(2);
		//row1
		assertEquals(2, fsecr2.read()); assertEquals(2, fsecr2.read());
		//row2
		assertEquals(2, fsecr2.read()); assertEquals(2, fsecr2.read());
		//EOF
		assertEquals(-1, fsecr2.read());
		fsecr2.close();
		///////////////////////////////////////////////////////////////////
	}
	
}
