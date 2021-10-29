/**
 * 
 */
package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import abstracts.Chunk;
import level.Area;
import level.OverWorld;
import level.PixelData;
import main.Game;

/**
 * 
 * @author tlee
 */
public class PixelDataChunk extends Chunk {

	private int currentAreaId;
	private int xPosition;
	private int yPosition;
	private int pixelColor;
	private boolean isHidden;

	public PixelDataChunk() {
		// Leave blank.
	}

	public int getCurrentAreaId() {
		return this.currentAreaId;
	}

	public int getPixelColor() {
		return this.pixelColor;
	}

	@Override
	public int getSize(Game game) {
		// This chunk has a fixed data size.
		return (4 * 4) + 1;
	}

	public int getXPosition() {
		return this.xPosition;
	}

	public int getYPosition() {
		return this.yPosition;
	}

	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		// We do not read any chunk sizes here. We only read the pixel data properties, and load them into
		// the game.
		this.currentAreaId = raf.readInt();
		this.xPosition = raf.readInt();
		this.yPosition = raf.readInt();
		this.pixelColor = raf.readInt();
		this.isHidden = raf.readBoolean();

		OverWorld world = game.getWorld();
		List<Area> areas = world.getAllAreas();
		for (Area area : areas) {
			if (area.getAreaID() == this.currentAreaId) {
				PixelData data = new PixelData(this.pixelColor, this.xPosition, this.yPosition);
				if (this.isHidden)
					data.hide();
				area.setPixelData(data, this.xPosition, this.yPosition);
				break;
			}
		}
	}

	public void setCurrentAreaId(int currentAreaId) {
		this.currentAreaId = currentAreaId;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public void setPixelColor(int pixelColor) {
		this.pixelColor = pixelColor;
	}

	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		// We do not write any chunk size here. Only write the properties of the pixel data.
		raf.writeInt(this.currentAreaId);
		raf.writeInt(this.xPosition);
		raf.writeInt(this.yPosition);
		raf.writeInt(this.pixelColor);
		raf.writeBoolean(this.isHidden);
	}
}
