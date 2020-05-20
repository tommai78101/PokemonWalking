package data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		int size = this.getSize();
		if (size != raf.readShort()) {
			throw new IOException("Incorrect save chunk size.");
		}

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
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		raf.writeShort(this.getSize());
		raf.write(this.name);
		raf.write(this.version);
		for (Chunk chunk : this.chunks) {
			chunk.write(game, raf);
		}
	}

	@Override
	public int getSize() {
		int size = this.name.length;
		size += this.version.length;
		for (Chunk chunk : this.chunks) {
			size += chunk.getSize();
		}
		return size;
	}
}
