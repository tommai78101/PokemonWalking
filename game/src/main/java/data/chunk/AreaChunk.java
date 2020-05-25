package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;

import abstracts.Chunk;
import level.Area;
import level.OverWorld;
import level.WorldConstants;
import main.Game;

/**
 * 
 * @author tlee
 */
public class AreaChunk extends Chunk {
	// Current area data
	public static final byte[] AREA = "AREA".getBytes();

	private int currentAreaID;

	public AreaChunk() {
		this.name = AreaChunk.AREA;
	}

	public int getCurrentAreaId() {
		return this.currentAreaID;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		int size = this.getSize();
		if (size != raf.readShort()) {
			throw new IOException("Unmatched area chunk size.");
		}

		for (byte b : AreaChunk.AREA) {
			if (b != raf.readByte()) {
				throw new IOException("Incorrect area chunk name.");
			}
		}

		this.currentAreaID = raf.readInt();
		OverWorld gameWorld = game.getWorld();
		Area area = WorldConstants.convertToArea(gameWorld.getAllAreas(), this.currentAreaID);
		if (area == null) {
			throw new IOException("Unmatched area data.");
		}
		gameWorld.setCurrentArea(area);
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		OverWorld gameWorld = game.getWorld();
		this.currentAreaID = gameWorld.getCurrentArea().getAreaID();

		raf.writeShort(this.getSize());
		raf.write(AreaChunk.AREA);
		raf.writeInt(this.currentAreaID);
	}

	@Override
	public int getSize() {
		int size = this.name.length;
		// Current area ID bytes count. (int)
		size += 4;
		return size;
	}

}
