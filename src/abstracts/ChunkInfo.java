package abstracts;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class ChunkInfo {
	public abstract void read(RandomAccessFile raf) throws IOException;
	
	public abstract void write(RandomAccessFile raf) throws IOException;
}
