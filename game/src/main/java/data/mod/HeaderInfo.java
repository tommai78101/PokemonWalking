/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package data.mod;

import java.io.IOException;
import java.io.RandomAccessFile;

import interfaces.RandomFileAccessible;

public class HeaderInfo implements RandomFileAccessible {
	public final byte[] header_id = "HEAD".getBytes();
	public final byte[] header_version = "0001".getBytes();
	public final byte[] header_format = ".SAV".getBytes();
	public final int size;

	public HeaderInfo() {
		size = header_id.length + header_version.length + header_format.length;
	}

	@Override
	public void read(RandomAccessFile raf) throws IOException {
		int size = raf.read();
		byte[] info = new byte[size];
		boolean olderVersion = false;
		raf.read(info);
		try {
			VERSION_CHECK:
			for (int j = 0; j < header_version.length; j++) {
				if (header_version[j] != info[j]) {
					if (header_version[j] > info[j]) {
						olderVersion = true;
						new RuntimeException("Incorrect header version signature. Determining its version.")
							.printStackTrace();
						break VERSION_CHECK;
					}
					else
						throw new IOException("Unable to determine version.");
				}
			}
			ID_CHECK:
			for (int i = 0; i < header_id.length; i++) {
				if (header_id[i] != info[i + header_version.length]) {
					if (olderVersion) {
						new RuntimeException("Incorrect header id signature. Attempting to update.").printStackTrace();
						break ID_CHECK;
					}
					else
						throw new RuntimeException("Unknown header id signature.");
				}
			}
			for (int k = 0; k < header_format.length; k++) {
				if (header_format[k] != info[k + header_id.length + header_version.length]) {
					throw new RuntimeException("Incorrect header code signature.");
				}
			}
		}
		catch (Exception e) {
			raf.close();
			throw new IOException("Error in reading the data file.", e);
		}
	}

	@Override
	public void write(RandomAccessFile raf) throws IOException {
		try {
			raf.write(size);
			raf.write(header_version);
			raf.write(header_id);
			raf.write(header_format);
		}
		catch (Exception e) {
			throw new IOException("Error in writing file.", e);
		}
	}
}
