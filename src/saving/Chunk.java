package saving;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Chunk {
	public static final long HEADER_SIGNATURE = 0x4845414445520000L; //ASCII code for HEADER00 (2 zeros at end)
	public static final int SIGNATURE_COUNT = 1;
	
	protected long signature; 	//8 bytes of readable tag label.
	protected int size;			//Size of chunk.
	protected long[] chunkSignatureList; //Array of signatures that the file has saved.
	
	public Chunk() {
		this.signature = 0L;
		this.size = 0;
		this.chunkSignatureList = new long[SIGNATURE_COUNT]; //This number is the total number of signatures.
	}
	
	public Chunk(Chunk chunk) {
		this.signature = chunk.getSignature();
		this.size = chunk.getSize();
	}
	
	public void read(DataInputStream input) {
		try {
			this.signature = input.readLong();
			this.size = input.readInt();
		}
		catch (IOException e) {
			throw new RuntimeException("Error in chunk reading the input", e);
		}
	}
	
	public void write(DataOutputStream output) {
		try {
			output.writeLong(signature);
			output.writeInt(size);
		}
		catch (IOException e) {
			throw new RuntimeException("Error in chunk writing the input", e);
		}
		
	}
	
	public long[] getChunkSignatureList() {
		return chunkSignatureList;
	}
	
	public long getSignature() {
		return signature;
	}
	
	public void setSignature(long sig) {
		this.signature = sig;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setChunkSignatureList(long[] list) {
		this.chunkSignatureList = list;
	}
	
	public static Chunk convert(Chunk chunk) {
		long sig = chunk.getSignature();
		if (sig == HEADER_SIGNATURE)
			return new Header((Header) chunk);
		return null;
	}
}
