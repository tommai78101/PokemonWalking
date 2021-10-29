package abstracts;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import main.Game;

public abstract class Chunk {
	protected byte[] name;

	public Chunk() {
		this.name = null;
	}

	public static byte[] combine(byte[] first, byte second) {
		byte[] result = new byte[first.length + 1];
		System.arraycopy(first, 0, result, 0, first.length);
		result[result.length - 1] = second;
		return result;
	}

	public static byte[] combine(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public String getDisplayName() {
		return Arrays.toString(this.name);
	}

	/**
	 * Calculates the size needed for this chunk. Must be positive, and must be less than or equal to
	 * Short.MAX_SIZE.
	 * 
	 * @return An integer. This data type is accepted for RandomAccessFile to write as short data.
	 */
	public abstract int getSize(Game game);

	/**
	 * This reads the byte data from the RandomAccessFile and store the data into the chunk.
	 * 
	 * @param raf
	 *            - The file to be read from.
	 */
	public abstract void read(Game game, RandomAccessFile raf) throws IOException;

	/**
	 * This prepares the data stored in the Chunk class and writes the data to the RandomAccessFile.
	 * 
	 * @param raf
	 *            - The file to write to.
	 */
	public abstract void write(Game game, RandomAccessFile raf) throws IOException;
}
