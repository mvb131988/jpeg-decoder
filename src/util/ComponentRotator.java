package util;

import java.io.IOException;

import decoder.DecoderContext;

public class ComponentRotator {

	//use decoder context
	public void rotate(DecoderContext dc) throws IOException, Exception {
		//apply to ext_component_0, ext_component_1, ext_component_2
		for(int i=0; i<3; i++) rotateComponent(i, dc);
	}
	
	private void rotateComponent(int index, DecoderContext dc) throws IOException, Exception {
		//number of samples in line/row
		int x = dc.frameHeader.X;
		//number of lines/rows
		int y = dc.frameHeader.Y;
		
		//split image into y files (each file for a line)
		try(FileSystemExtendedComponentReader fsecr = new FileSystemExtendedComponentReader(index)) {
			for(int i=0; i<y;i++) {
				//create file for row i
				try(FileSystemReverseOrderRowWriter fsrorw = new FileSystemReverseOrderRowWriter(index, i)) {
					for(int j=0; j<x; j++) {
						int sample = fsecr.read();
						fsrorw.write(sample);
					}
				}
			}
		}
		
		//replace extended component file
		try(FileSystemExtendedComponentWriter fsecw = new FileSystemExtendedComponentWriter(index)) {
			for(int i=y-1; i>=0; i--) {
				//read lines in reverse order (lineN - line0)
				try(FileSystemReverseOrderRowReader fsrorr = new FileSystemReverseOrderRowReader(index, i)) {
					for(int j=0; j<x; j++) {
						int sample = fsrorr.read();
						fsecw.write(sample);
					}
				}
			}
		}
	}
	
}
