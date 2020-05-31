/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package interfaces;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface RandomFileAccessible {
	public void read(RandomAccessFile raf) throws IOException;

	public void write(RandomAccessFile raf) throws IOException;
}
