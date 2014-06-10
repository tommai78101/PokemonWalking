/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package saving;

import java.io.IOException;
import java.io.RandomAccessFile;

import abstracts.ChunkInfo;

public class HeaderInfo extends ChunkInfo {
	public final byte[] header_id = "HEAD".getBytes();
	public final byte[] header_version = "0001".getBytes();
	public final byte[] header_format = ".SAV".getBytes();
	public final int size;
	
	public HeaderInfo() {
		size = header_id.length + header_version.length + header_format.length;
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
	
	@Override
	public void read(RandomAccessFile raf) throws IOException {
		int size = raf.read();
		byte[] info = new byte[size];
		boolean olderVersion = false;
		raf.read(info);
		try {
			VERSION_CHECK: for (int j = 0; j < header_version.length; j++) {
				if (header_version[j] != info[j]) {
					if (header_version[j] > info[j]) {
						olderVersion = true;
						new RuntimeException("Incorrect header version signature. Determining its version.").printStackTrace();
						break VERSION_CHECK;
					}
					else
						throw new IOException("Unable to determine version.");
				}
			}
			ID_CHECK: for (int i = 0; i < header_id.length; i++) {
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
}
