/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package saving;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import abstracts.ChunkInfo;

public class AreaInfo extends ChunkInfo {
	public static final byte[] AREA = "AREA".getBytes(); // Current Area.
	public static final byte[] PIXELDATA = "PXDT".getBytes(); // Changed pixels data.
	
	public final byte[] current_area_id = new byte[4];
	public final byte[] current_area_sector_id = new byte[4];
	public final ArrayList<byte[]> changedPixelData = new ArrayList<byte[]>();
	public int size;
	
	public AreaInfo() {
		size = 0;
	}
	
	@Override
	public void read(RandomAccessFile raf) throws IOException {
		// Area Info chunk total size
		this.size = raf.readByte();
		
		// Area Info
		int offset = 0;
		byte[] data = new byte[raf.readByte()];
		raf.read(data);
		
		for (int i = 0; i < AREA.length; offset++, i++, size--) {
			if (data[offset] != AREA[i])
				throw new IOException("Incorrect Area Info AREA chunk.");
		}
		for (int i = 0; i < this.current_area_id.length; i++, offset++, size--) {
			this.current_area_id[i] = data[offset];
		}
		for (int i = 0; i < this.current_area_sector_id.length; i++, offset++, size--) {
			this.current_area_sector_id[i] = data[offset];
		}
		
		// Changed pixel data
		
		int arraySize = raf.readChar();
		int pixelSize = (arraySize - 4) / 20;
		
		if (pixelSize > 0) {
			data = new byte[arraySize];
			raf.read(data);
			offset = 0;
			for (int i = 0; i < PIXELDATA.length; i++, offset++)
				if (data[offset] != PIXELDATA[i])
					throw new IOException("Incorrect Area Info PIXELDATA chunk.");
			for (; pixelSize > 0; pixelSize--) {
				byte[] px = new byte[4 * 5];
				for (int i = 0; i < px.length; i++, offset++)
					px[i] = data[offset];
				this.changedPixelData.add(px);
			}
		}
	}
	
	@Override
	public void write(RandomAccessFile raf) throws IOException {
		// Total chunk size.
		raf.writeByte(size);
		
		// Current Area Info
		raf.write(AREA.length + current_area_id.length + current_area_sector_id.length);
		raf.write(AREA);
		raf.write(current_area_id);
		raf.write(current_area_sector_id);
		
		// Changed pixel data
		raf.writeChar(this.changedPixelData.size() * 4 * 5 + PIXELDATA.length);
		raf.write(PIXELDATA);
		for (byte[] b : this.changedPixelData)
			raf.write(b);
	}
	
	public void increment(byte[] concatenate) {
		this.size += concatenate.length;
	}
	
	public void reset() {
		this.size = 0;
	}
}
