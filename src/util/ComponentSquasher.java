package util;

import decoder.DecoderContext;

public class ComponentSquasher {

	public void squash(DecoderContext dc) throws Exception {
		for(int i=0; i<3; i++) squashComponent(i, dc);
	}
	
	private void squashComponent(int index, DecoderContext dc) throws Exception {
		int x = dc.frameHeader.X;
		int y = dc.frameHeader.Y;
		//target width of the image preview 
		int targetX = 200;
		//number of rows that would be substituted by a single(first) row
		//of the consecutive series of rows. In the same time it's number of
		//consecutive samples(series of samples) that would be substituted by 
		//a single sample(first from the series).
		int block = x/targetX;
		int targetY = y/block;
		
		int realX = targetX*block;
		int realY = targetY*block;
		
		//serial number of the sample from the series of samples
		int countX = 0; 
		//serial number of the row from the series of rows
		int countY = 0;
		
		try(FileSystemSquashedComponentWriter fsscw = new FileSystemSquashedComponentWriter(index)) {
			for(int i=realY-1; i>=0; i--) {
				if(countY == 0) {
					countX = 0;
					//open file and write samples
					try(FileSystemReverseOrderRowReader fsrorr = new FileSystemReverseOrderRowReader(index, i)) {
						for(int j=0; j<realX; j++) {
							int sample = fsrorr.read();
							if(countX == 0) {
								//write sample
								fsscw.write(sample);
							}
							countX++;
							if(countX == block) countX = 0;
						}
					}
				}
				countY++;
				if(countY == block) countY = 0;
			}
		}
		
		dc.minX = targetX;
		dc.minY = targetY;
	}
	
}
