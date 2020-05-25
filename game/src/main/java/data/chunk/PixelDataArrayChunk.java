/**
 * 
 */
package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import abstracts.Chunk;
import level.Area;
import level.PixelData;
import main.Game;

/**
 * 
 * @author tlee
 */
public class PixelDataArrayChunk extends Chunk {

	// Area pixel data information
	public static final byte[] PIXELDATA = "PXDT".getBytes();

	// This tallies how many changed pixel data there is when storing them.
	private short areaDataSize;
	private List<Chunk> chunks = new ArrayList<>();

	public PixelDataArrayChunk() {
		this.name = PixelDataArrayChunk.PIXELDATA;
		this.areaDataSize = 0;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		int size = this.getSize();
		if (size != raf.readShort()) {
			throw new IOException("Incorrect pixel data array size.");
		}

		for (byte b : PixelDataArrayChunk.PIXELDATA) {
			if (b != raf.readByte()) {
				throw new IOException("Unmatched pixel data array chunk name.");
			}
		}

		this.areaDataSize = raf.readShort();

		for (short i = 0; i < this.areaDataSize; i++) {
			PixelDataChunk dataChunk = new PixelDataChunk();
			dataChunk.read(game, raf);
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		// Set how many changed pixel data info were created in the game.
		List<Area> areas = game.getWorld().getAllAreas();
		for (Area area : areas) {
			Set<PixelData> pixelDataSet = area.getModifiedPixelDataList();
			for (PixelData data : pixelDataSet) {
				PixelDataChunk dataChunk = new PixelDataChunk();
				dataChunk.setCurrentAreaId(area.getAreaID());
				dataChunk.setXPosition(data.xPosition);
				dataChunk.setYPosition(data.yPosition);
				dataChunk.setPixelColor(data.getColor());
				dataChunk.setHidden(data.isHidden());
				this.chunks.add(dataChunk);
			}
		}
		this.areaDataSize = (short) this.chunks.size();

		// We include the save chunk data size bytes count here. (short)
		raf.writeShort(this.getSize());
		raf.write(this.name);
		raf.writeShort(this.areaDataSize);
		for (Chunk chunk : this.chunks) {
			chunk.write(game, raf);
		}
	}

	@Override
	public int getSize() {
		int size = this.name.length;
		// Area data size bytes count. (short)
		size += 2;
		for (Chunk chunk : this.chunks) {
			// No need to add additional bytes count for each chunk's sizes.
			size += chunk.getSize();
		}
		return size;
	}

}
