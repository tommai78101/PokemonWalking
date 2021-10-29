package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import abstracts.Chunk;
import main.Game;

/**
 * 
 * @author tlee
 */
public class SaveChunk extends Chunk {

	private static final byte[] SaveData = "SAVE".getBytes();
	private static final byte[] SaveVersion = "0002".getBytes();

	private List<Chunk> chunks = new ArrayList<>();
	private byte[] version;

	public SaveChunk() {
		this.name = SaveChunk.SaveData;
		this.version = SaveChunk.SaveVersion;
		this.chunks.add(new Header());
		this.chunks.add(new AreaChunk());
		this.chunks.add(new PixelDataArrayChunk());
		this.chunks.add(new PlayerChunk());
	}

	@Override
	public int getSize(Game game) {
		int size = this.name.length;
		size += this.version.length;
		for (Chunk chunk : this.chunks) {
			int chunkSize = chunk.getSize(game);
			size += chunkSize;
			// Chunk size bytes count. (short)
			size += 2;
		}
		return size;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		short rafSize = raf.readShort();

		for (byte b : SaveChunk.SaveData) {
			if (b != raf.readByte()) {
				throw new IOException("Incorrect save chunk name.");
			}
		}

		for (byte b : SaveChunk.SaveVersion) {
			if (b != raf.readByte()) {
				throw new IOException("Incorrect save chunk version.");
			}
		}

		for (Chunk chunk : this.chunks) {
			chunk.read(game, raf);
		}

		// Check size at the last step
		int size = this.getSize(game);
		if (size != rafSize) {
			throw new IOException("Incorrect save chunk size.");
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		// We include the save chunk data size bytes count here. (short)
		int size = this.getSize(game);

		raf.writeShort(size);
		raf.write(this.name);
		raf.write(this.version);
		for (Chunk chunk : this.chunks) {
			chunk.write(game, raf);
		}
	}
}
