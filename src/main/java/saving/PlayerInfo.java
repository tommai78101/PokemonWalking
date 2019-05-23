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
import java.util.ArrayList;
import java.util.List;

import abstracts.ChunkInfo;

public class PlayerInfo extends ChunkInfo {
	public final byte[] player_name = new byte[16];
	public final byte[] player_gender = new byte[1];
	public final ArrayList<byte[]> startMenu = new ArrayList<byte[]>();
	public final ArrayList<byte[]> potions = new ArrayList<byte[]>();
	public final ArrayList<byte[]> keyItems = new ArrayList<byte[]>();
	public final ArrayList<byte[]> pokéballs = new ArrayList<byte[]>();
	public final ArrayList<byte[]> tm_hm = new ArrayList<byte[]>();
	public final byte[] player_x = new byte[4];
	public final byte[] player_y = new byte[4];
	public final byte[] player_facing = new byte[4];
	
	public static final byte[] PLAY = "PLAY".getBytes(); // Player Info
	public static final byte[] NAME = "NAME".getBytes(); // Player name.
	public static final byte[] GNDR = "GNDR".getBytes(); // Player gender.
	public static final byte[] MENU = "MENU".getBytes(); // Start menu options.
	public static final byte[] ITEM = "ITEM".getBytes(); // Player Inventory.
	// public static final byte[] AREA = "AREA".getBytes(); //Current Area.
	public static final byte[] AXIS = "AXIS".getBytes(); // Current Position.
	public static final byte[] TURN = "TURN".getBytes(); // Current Direction.
	
	private int byteSize = 0;
	
	public List<ArrayList<byte[]>> getAllItemsList() {
		List<ArrayList<byte[]>> result = new ArrayList<ArrayList<byte[]>>();
		result.add(this.potions);
		result.add(this.keyItems);
		result.add(this.pokéballs);
		result.add(this.tm_hm);
		return result;
	}
	
	public int getByteSize() {
		return this.byteSize;
	}
	
	public void increment(byte[] list) {
		this.byteSize += list.length;
	}
	
	@Override
	public void read(RandomAccessFile raf) throws IOException {
		this.byteSize = raf.readShort();
		byte[] data = new byte[byteSize];
		int offset = 0;
		raf.read(data);
		// Checks if "PLAY" tag is set.
		for (int i = 0; i < PLAY.length; i++, offset++) {
			if (data[i] != PLAY[i])
				throw new IOException("Incorrect Player Info Header chunk.");
		}
		
		// Checks for "NAME" tag.
		byte size = data[offset++];
		for (int i = 0; i < NAME.length; offset++, i++, size--) {
			if (data[offset] != NAME[i])
				throw new IOException("Incorrect Player Info NAME chunk.");
		}
		for (int i = 0; i < this.player_name.length; offset++, i++, size--) {
			if (size < 0)
				throw new IOException("Something is wrong with the size in NAME chunk.");
			this.player_name[i] = data[offset];
		}
		
		// Checks for "Gender" tag.
		size = data[offset++];
		for (int i = 0; i < GNDR.length; offset++, i++, size--) {
			if (data[offset] != GNDR[i])
				throw new IOException("Incorrect Player Info GNDR chunk.");
		}
		for (int i = 0; i < this.player_gender.length; offset++, i++, size--) {
			if (size < 0)
				throw new IOException("Something is wrong with the size in GNDR chunk.");
			this.player_gender[i] = data[offset];
		}
		
		// Menu tag.
		size = data[offset++];
		for (int i = 0; i < MENU.length; offset++, i++, size--) {
			if (data[offset] != MENU[i])
				throw new IOException("Incorrect Player Info MENU chunk.");
		}
		byte[] menuName = null;
		byte count = 0;
		for (; size > 0; size--, offset++) {
			if (menuName == null) {
				menuName = new byte[data[offset++]];
				size--;
			}
			menuName[count++] = data[offset];
			if (count > menuName.length - 1) {
				count = 0x0;
				this.startMenu.add(menuName);
				menuName = null;
			}
		}
		
		// Inventory tag
		size = data[offset++];
		for (int i = 0; i < ITEM.length; i++, offset++, size--) {
			if (data[offset] != ITEM[i])
				throw new IOException("Incorrect Player Info ITEM chunk.");
		}
		for (int i = 0; i < this.getAllItemsList().size(); i++) {
			if (size > 0) {
				byte listType = data[offset++];
				size--;
				
				int listSize = ((data[offset] << 8) | data[offset + 1]) & 0xFFFF;
				offset += 2;
				size -= 2;
				
				if (listSize > 0) {
					List<byte[]> list = this.getAllItemsList().get(listType - 0x1);
					for (int k = 0; k < listSize; k++) {
						byte listElementSize = data[offset++];
						size--;
						
						byte nameSize = data[offset];
						byte[] entry = new byte[1 + nameSize + 4 + 4];
						for (int j = 0; j < entry.length; j++, offset++, size--, listElementSize--) {
							if (listElementSize < 0)
								throw new IOException("Something is wrong with the element item size in ITEM chunk.");
							entry[j] = data[offset];
						}
						
						list.add(entry);
					}
				}
			}
			else
				break;
		}
		
		// //Current Area tag
		// size = data[offset++];
		// for (int i = 0; i < AREA.length; offset++, i++, size--) {
		// if (data[offset] != AREA[i])
		// throw new IOException("Incorrect Player Info AREA chunk.");
		// }
		// for (int i = 0; i < this.player_current_area_id.length; i++, offset++, size--) {
		// this.player_current_area_id[i] = data[offset];
		// }
		// for (int i = 0; i < this.player_current_area_sector_id.length; i++, offset++, size--) {
		// this.player_current_area_sector_id[i] = data[offset];
		// }
		
		// Current Position tag
		size = data[offset++];
		for (int i = 0; i < AXIS.length; offset++, i++, size--) {
			if (data[offset] != AXIS[i])
				throw new IOException("Incorrect Player Info AXIS chunk.");
		}
		for (int i = 0; i < this.player_x.length; i++, offset++, size--) {
			this.player_x[i] = data[offset];
		}
		for (int i = 0; i < this.player_y.length; i++, offset++, size--) {
			this.player_y[i] = data[offset];
		}
		
		// Current Direction Facing tag
		size = data[offset++];
		for (int i = 0; i < TURN.length; offset++, i++, size--) {
			if (data[offset] != TURN[i])
				throw new IOException("Incorrect Player Info TURN chunk.");
		}
		for (int i = 0; i < this.player_facing.length; i++, offset++, size--) {
			this.player_facing[i] = data[offset];
		}
	}
	
	public void reset() {
		this.byteSize = 0;
	}
	
	@Override
	public void write(RandomAccessFile raf) throws IOException {
		// Sizes are added at the beginning of every little bit of data.
		// Player Info Header
		raf.writeShort(this.byteSize + PLAY.length);
		raf.write(PLAY);
		
		// Player name.
		raf.writeByte(player_name.length + NAME.length);
		raf.write(NAME);
		raf.write(player_name);
		
		// Player gender.
		raf.writeByte(player_gender.length + GNDR.length);
		raf.write(GNDR);
		raf.write(player_gender);
		
		// Unlocked start menu options.
		int size = 0;
		for (int i = 0; i < this.startMenu.size(); i++)
			size += this.startMenu.get(i).length + 1;
		raf.writeByte(size + MENU.length);
		raf.write(MENU);
		for (int i = 0; i < startMenu.size(); i++) {
			byte[] buf = startMenu.get(i);
			raf.writeByte(buf.length);
			raf.write(buf);
		}
		
		// Inventory
		size = 0;
		for (int i = 0; i < 4; i++) {
			ArrayList<byte[]> list = this.getAllItemsList().get(i);
			if (list.size() > 0) {
				size += 3;
				for (int j = 0; j < this.getAllItemsList().get(i).size(); j++)
					size += this.getAllItemsList().get(i).get(j).length + 1;
			}
			
		}
		raf.writeByte(size + ITEM.length);
		raf.write(ITEM);
		for (byte listType = 0; listType < 4; listType++) {
			ArrayList<byte[]> list = this.getAllItemsList().get(listType);
			if (list.size() > 0) {
				raf.write(listType + 0x1);
				raf.writeChar(list.size());
				for (int j = 0; j < list.size(); j++) {
					raf.write(list.get(j).length);
					raf.write(list.get(j));
				}
			}
		}
		
		// //Current Area Info
		// raf.writeByte(AREA.length + player_current_area_id.length + player_current_area_sector_id.length);
		// raf.write(AREA);
		// raf.write(player_current_area_id);
		// raf.write(player_current_area_sector_id);
		
		// Current Position.
		raf.writeByte(AXIS.length + player_x.length + player_y.length);
		raf.write(AXIS);
		raf.write(player_x);
		raf.write(player_y);
		
		// Current Player Direction Facing.
		raf.writeByte(TURN.length + player_facing.length);
		raf.write(TURN);
		raf.write(player_facing);
	}
}
