package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import abstracts.Chunk;
import main.Game;

/**
 * 
 * @author tlee
 */
public class Header extends Chunk {
	private static final byte[] ChunkName = "HEAD".getBytes();
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_ZONED_DATE_TIME;

	private byte[] currentDate;
	private byte currentDateSize;

	public Header() {
		this.name = Header.ChunkName;
		this.currentDate = Header.dateFormat.format(ZonedDateTime.now()).getBytes();
		this.currentDateSize = (byte) this.currentDate.length;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		short rafSize = raf.readShort();

		for (byte b : Header.ChunkName) {
			if (b != raf.readByte()) {
				throw new IOException("Incorrect header chunk name.");
			}
		}

		// TODO(May 19, 2020): Do we have a use for current date and time? Perhaps look into "system time".
		this.currentDateSize = raf.readByte();
		this.currentDate = new byte[this.currentDateSize];
		raf.read(this.currentDate);

		try {
			String savedDate = new String(this.currentDate, StandardCharsets.UTF_8);
			ZonedDateTime.from(Header.dateFormat.parse(savedDate));
		}
		catch (DateTimeParseException e) {
			throw new IOException("Failed to parse and read system date time.", e);
		}

		// Easy authentication.
		int size = this.getSize();
		if (size != rafSize) {
			throw new IOException("Incorrect header chunk size.");
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		// Always obtain the current date and time before writing data.
		this.currentDate = Header.dateFormat.format(ZonedDateTime.now()).getBytes();

		raf.writeShort(this.getSize());
		raf.write(this.name);
		raf.writeByte(this.currentDate.length);
		raf.write(this.currentDate);
	}

	@Override
	public int getSize() {
		int size = this.name.length;
		size += this.currentDate.length;
		// Byte size of the current date length. (byte)
		size += 1;
		return size;
	}

}
