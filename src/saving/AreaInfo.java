package saving;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import abstracts.ChunkInfo;

public class AreaInfo extends ChunkInfo {
	public static final byte[] AREA = "AREA".getBytes(); //Current Area.
	public static final byte[] PIXELDATA = "PXDT".getBytes(); //Changed pixels data.
	
	public final byte[] current_area_id = new byte[4];
	public final byte[] current_area_sector_id = new byte[4];
	public final ArrayList<byte[]> changedPixelData = new ArrayList<byte[]>();
	public int size;
	
	public AreaInfo() {
		size = 0;
	}
	
	@Override
	public void read(RandomAccessFile raf) throws IOException {
		//Area Info chunk total size
		size = raf.readByte();
		
		
		//Area Info
		int offset = 0;
		byte[] data = new byte[size];
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
		
		
		//Changed pixel data
		int pixelSize = ((((data[offset] << 8) | data[offset+1]) & 0xFFFF) - PIXELDATA.length) / (4*5);
		offset+=2;
		for (int i=0; i< PIXELDATA.length;i++, offset++)
			if (data[offset] != PIXELDATA[i])
				throw new IOException("Incorrect Area Info PIXELDATA chunk.");
		for (; pixelSize > 0; pixelSize--){
			byte[] px = new byte[4*5];
			for (int i = 0; i<px.length; i++, offset++)
				px[i] = data[offset];
			this.changedPixelData.add(px);
		}
	}
	
	@Override
	public void write(RandomAccessFile raf) throws IOException {
		//Total chunk size.
		raf.writeByte(size);
		
		//Current Area Info
		raf.write(AREA);
		raf.write(current_area_id);
		raf.write(current_area_sector_id);
		
		//Changed pixel data
		raf.writeChar(this.changedPixelData.size() * 4 * 5 + PIXELDATA.length);
		raf.write(PIXELDATA);
		for (byte[] b: this.changedPixelData)
			raf.write(b);
	}
	
	public void increment(byte[] concatenate) {
		this.size += concatenate.length;
	}
	
	public void reset() {
		this.size = 0;
	}
}
