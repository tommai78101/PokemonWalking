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

	// Game related information. Not to be used in data chunks. Denoted with "p_".
	private List<Area> p_areas = new ArrayList<>();

	public PixelDataArrayChunk() {
		this.name = PixelDataArrayChunk.PIXELDATA;
		this.areaDataSize = 0;
	}

	@Override
	public int getSize(Game game) {
		int size = this.name.length;
		// Area data size bytes count. (short)
		size += 2;

		this.p_areas = game.getWorld().getAllAreas();
		for (Area area : this.p_areas) {
			int dataSetSize = area.getModifiedPixelDataList().size();
			for (int i = 0; i < dataSetSize; i++) {
				PixelDataChunk chunk = new PixelDataChunk();
				size += chunk.getSize(game);
			}
		}
		return size;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		// Prepare data
		this.p_areas = game.getWorld().getAllAreas();
		this.chunks.clear();

		short rafSize = raf.readShort();

		for (byte b : PixelDataArrayChunk.PIXELDATA) {
			if (b != raf.readByte()) {
				throw new IOException("Unmatched pixel data array chunk name.");
			}
		}

		this.areaDataSize = raf.readShort();

		for (short i = 0; i < this.areaDataSize; i++) {
			PixelDataChunk dataChunk = new PixelDataChunk();
			dataChunk.read(game, raf);
			this.chunks.add(dataChunk);
		}

		// Check the chunk size at the last step
		int size = this.getSize(game);
		if (size != rafSize) {
			throw new IOException("Incorrect pixel data array size.");
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		// Prepare
		this.p_areas = game.getWorld().getAllAreas();

		// Set how many changed pixel data info were created in the game.
		for (Area area : this.p_areas) {
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
		raf.writeShort(this.getSize(game));
		raf.write(this.name);
		raf.writeShort(this.areaDataSize);
		for (Chunk chunk : this.chunks) {
			chunk.write(game, raf);
		}
	}

}
