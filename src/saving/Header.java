package saving;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Header extends Chunk {
	private long fileTypeName;		//8 bytes containing the name of the program that reads this file.
	private int fileFormat;			//1 byte containing a period. 3 bytes containing the format name.
	
	public Header() {
		super();
		this.signature = HEADER_SIGNATURE;
		this.fileTypeName = 0L;
		this.fileFormat = 0;
	}
	
	public Header(Header header) {
		super(header);
		this.signature = HEADER_SIGNATURE;
		this.fileTypeName = header.getFileTypeName();
		this.fileFormat = header.getFileFormat();
		this.chunkSignatureList = header.getChunkSignatureList();
	}
	
	@Override
	public void read(DataInputStream input) {
		super.read(input);
		try {
			this.fileTypeName = input.readLong();
			this.fileFormat = input.readInt();
		}
		catch (IOException e) {
			throw new RuntimeException("Can't read header.", e);
		}
	}
	
	@Override
	public void write(DataOutputStream output) {
		super.write(output);
		try {
			output.writeLong(this.fileTypeName);
			output.writeInt(this.fileFormat);
		}
		catch (IOException e) {
			throw new RuntimeException("Can't write header.", e);
		}
	}
	
	public long getFileTypeName() {
		return this.fileTypeName;
	}
	
	public int getFileFormat() {
		return this.fileFormat;
	}
	
	public void setFileTypeName(long name) {
		this.fileTypeName = name;
	}
	
	public void setFileFormat(int format) {
		this.fileFormat = format;
	}
}