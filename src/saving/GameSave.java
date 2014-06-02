package saving;

import item.ActionItem;
import item.DummyItem;
import item.ItemText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import level.Area;
import level.WorldConstants;
import main.Game;
import submenu.DummyMenu;
import submenu.Inventory;
import submenu.Save;
import abstracts.Item;
import abstracts.SubMenu;
import dialogue.StartMenu;
import entity.Player;

public class GameSave {
	/*
	 * Refer to the documentation on the file format used for saving game data.
	 * 
	 * All saving/loading schemes use the documentation as guidelines. It will be changed in the future, as the development continues.
	 */
	public static class HeaderInfo {
		public final byte[] header_id = "IDNO".getBytes();
		public final byte[] header_code = "HEAD".getBytes();
		public final byte[] header_format = ".SAV".getBytes();
		public final int size;

		public HeaderInfo() {
			size = header_id.length + header_code.length + header_format.length;
		}

		public void write(RandomAccessFile raf) throws IOException {
			raf.write(size);
			raf.write(header_id);
			raf.write(header_code);
			raf.write(header_format);
		}

		public void read(RandomAccessFile raf) throws IOException {
			int size = raf.read();
			byte[] info = new byte[size];
			raf.read(info);
			try {
				for (int i = 0; i < header_id.length; i++) {
					if (header_id[i] != info[i]) {
						throw new RuntimeException("Incorrect header id signature.");
					}
				}
				for (int j = 0; j < header_code.length; j++) {
					if (header_code[j] != info[j + header_id.length]) {
						throw new RuntimeException("Incorrect header code signature.");
					}
				}
				for (int k = 0; k < header_format.length; k++) {
					if (header_format[k] != info[k + header_id.length + header_code.length]) {
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

	public static class PlayerInfo {
		public final byte[] player_name = new byte[16];
		public final byte[] player_gender = new byte[1];
		public final ArrayList<byte[]> startMenu = new ArrayList<byte[]>();
		public final ArrayList<byte[]> potions = new ArrayList<byte[]>();
		public final ArrayList<byte[]> keyItems = new ArrayList<byte[]>();
		public final ArrayList<byte[]> pokéballs = new ArrayList<byte[]>();
		public final ArrayList<byte[]> tm_hm = new ArrayList<byte[]>();
		public final byte[] player_current_area_id = new byte[4];
		public final byte[] player_current_area_sector_id = new byte[4];
		public final byte[] player_x = new byte[4];
		public final byte[] player_y = new byte[4];
		public final byte[] player_facing = new byte[4];

		public static final byte[] PLAY = "PLAY".getBytes(); //Player Info
		public static final byte[] NAME = "NAME".getBytes(); //Player name.
		public static final byte[] GNDR = "GNDR".getBytes(); //Player gender.
		public static final byte[] MENU = "MENU".getBytes(); //Start menu options.
		public static final byte[] ITEM = "ITEM".getBytes(); //Player Inventory.
		public static final byte[] AREA = "AREA".getBytes(); //Current Area.
		public static final byte[] AXIS = "AXIS".getBytes(); //Current Position.
		public static final byte[] TURN = "TURN".getBytes(); //Current Direction.

		private int byteSize = 0;

		public void write(RandomAccessFile raf) throws IOException {
			//Sizes are added at the beginning of every little bit of data.
			//Player Info Header
			raf.writeShort(this.byteSize + PLAY.length);
			raf.write(PLAY);

			//Player name.
			raf.writeByte(player_name.length + NAME.length);
			raf.write(NAME);
			raf.write(player_name);

			//Player gender.
			raf.writeByte(player_gender.length + GNDR.length);
			raf.write(GNDR);
			raf.write(player_gender);

			//Unlocked start menu options.
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

			//Inventory
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

			//Current Area Info
			raf.writeByte(AREA.length + player_current_area_id.length + player_current_area_sector_id.length);
			raf.write(AREA);
			raf.write(player_current_area_id);
			raf.write(player_current_area_sector_id);

			//Current Position.
			raf.writeByte(AXIS.length + player_x.length + player_y.length);
			raf.write(AXIS);
			raf.write(player_x);
			raf.write(player_y);

			//Current Player Direction Facing.
			raf.writeByte(TURN.length + player_facing.length);
			raf.write(TURN);
			raf.write(player_facing);
		}

		public void increment(byte[] list) {
			this.byteSize += list.length;
		}

		public void reset() {
			this.byteSize = 0;
		}

		public int getByteSize() {
			return this.byteSize;
		}

		public List<ArrayList<byte[]>> getAllItemsList() {
			List<ArrayList<byte[]>> result = new ArrayList<ArrayList<byte[]>>();
			result.add(this.potions);
			result.add(this.keyItems);
			result.add(this.pokéballs);
			result.add(this.tm_hm);
			return result;
		}

		public void read(RandomAccessFile raf) throws IOException {
			this.byteSize = raf.readShort();
			byte[] data = new byte[byteSize];
			int readBytes;
			int offset = 0;
			while ((readBytes = raf.read(data)) != -1) {
				//Checks if "PLAY" tag is set.
				for (; offset < PLAY.length; offset++) {
					if (data[offset] != PLAY[offset])
						throw new IOException("Incorrect Player Info Header chunk.");
				}

				//Checks for "NAME" tag.
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

				//Checks for "Gender" tag.
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

				//Menu tag.
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

				//Inventory tag
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

				//Current Area tag
				size = data[offset++];
				for (int i = 0; i < AREA.length; offset++, i++, size--) {
					if (data[offset] != AREA[i])
						throw new IOException("Incorrect Player Info AREA chunk.");
				}
				for (int i = 0; i < this.player_current_area_id.length; i++, offset++, size--) {
					this.player_current_area_id[i] = data[offset];
				}
				for (int i = 0; i < this.player_current_area_sector_id.length; i++, offset++, size--) {
					this.player_current_area_sector_id[i] = data[offset];
				}

				//Current Position tag
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

				//Current Direction Facing tag
				size = data[offset++];
				for (int i = 0; i < TURN.length; offset++, i++, size--) {
					if (data[offset] != TURN[i])
						throw new IOException("Incorrect Player Info TURN chunk.");
				}
				for (int i = 0; i < this.player_facing.length; i++, offset++, size--) {
					this.player_facing[i] = data[offset];
				}
			}
		}
	}

	public static final byte[] SIGNATURE = new byte[] { (byte) 137, 0x53, 0x41, 0x56, 0x20, 0x20, 0x20, 0x20 };

	public HeaderInfo headerInfo;
	public PlayerInfo playerInfo;

	private GameSave() {
		headerInfo = new HeaderInfo();
		playerInfo = new PlayerInfo();
	}

	private void write(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		headerInfo.write(raf);
		playerInfo.write(raf);
	}

	private void read(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		headerInfo.read(raf);
		playerInfo.read(raf);
	}

	private void generateSaveData(Game game) {
		Player gamePlayer = game.getPlayer();
		if (this.playerInfo != null)
			this.playerInfo.reset();

		//Name of the player.
		System.arraycopy(gamePlayer.getByteName(), 0, this.playerInfo.player_name, 0, 16); //16 is the name length limit.
		byte[] byteArray = concatenate(PlayerInfo.NAME, gamePlayer.getByteName());
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, byteArray));

		//Gender of the player.
		System.arraycopy(gamePlayer.getByteGender(), 0, this.playerInfo.player_gender, 0, 1); //1 is the gender boolean size.
		byteArray = concatenate(PlayerInfo.GNDR, gamePlayer.getByteGender());
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, byteArray));

		//All active submenus the player has unlocked.
		byteArray = new byte[] {};
		List<Map.Entry<Integer, SubMenu>> startMenuList = game.getStartMenu().getSubMenusList();
		for (Map.Entry<Integer, SubMenu> entry : startMenuList) {
			SubMenu sub = entry.getValue();
			byteArray = concatenate(byteArray, concatenate(new byte[] { 0x0 }, sub.getSubMenuData()));
			this.playerInfo.startMenu.add(sub.getSubMenuData());
		}
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, concatenate(PlayerInfo.MENU, byteArray)));

		//Inventory
		byte listType = 0x1;
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, PlayerInfo.ITEM));
		for (List<Map.Entry<Item, Integer>> itemList : game.getStartMenu().getInventory().getAllItemsList()) {
			if (itemList.size() - 1 > 0) {
				byteArray = new byte[] { listType }; //Type of list (Potions/KeyItems/Pokeball/TMHM)
				byteArray = concatenate(byteArray, ByteBuffer.allocate(2).putChar((char) ((itemList.size() - 1) & 0xFFFF)).array()); //How many items in a list?
				byteArray = concatenate(byteArray, ByteBuffer.allocate(1).put((byte) itemList.size()).array()); //ItemInfo size.
				byte[] itemInfo = null;
				for (int i = 0; i < itemList.size() - 1; i++) {
					Map.Entry<Item, Integer> entry = itemList.get(i);
					ByteBuffer buffer = ByteBuffer.allocate(1);
					buffer.put((byte) (entry.getKey().getName().getBytes().length & 0xFF));
					itemInfo = buffer.array();
					buffer = ByteBuffer.allocate(4 * 2);
					buffer.putInt(entry.getKey().getID()); //Item ID
					buffer.putInt(entry.getValue()); //Item quantity
					itemInfo = concatenate(itemInfo, entry.getKey().getName().getBytes());
					itemInfo = concatenate(itemInfo, buffer.array());
					byteArray = concatenate(byteArray, itemInfo); //Item Name Length <- Item Name <- ID <- Quantity
				}
				this.playerInfo.getAllItemsList().get(listType - 0x1).add(itemInfo);
				this.playerInfo.increment(byteArray);
				listType++;
			}
		}

		//Current Area & Player State
		Area currentArea = game.getWorld().getCurrentArea();
		byte[] bufArea = ByteBuffer.allocate(4).putInt(currentArea.getAreaID()).array();
		byte[] bufSector = ByteBuffer.allocate(4).putInt(currentArea.getSectorID()).array();
		byte[] bufX = ByteBuffer.allocate(4).putInt(currentArea.getPlayerXInArea()).array();
		byte[] bufY = ByteBuffer.allocate(4).putInt(currentArea.getPlayerYInArea()).array();
		byte[] bufFacing = ByteBuffer.allocate(4).putInt(gamePlayer.getFacing()).array();
		for (int i = 0; i < 4; i++) {
			this.playerInfo.player_current_area_id[i] = bufArea[i];
			this.playerInfo.player_current_area_sector_id[i] = bufSector[i];
			this.playerInfo.player_x[i] = bufX[i];
			this.playerInfo.player_y[i] = bufY[i];
			this.playerInfo.player_facing[i] = bufFacing[i];
		}
		byteArray = new byte[] {};
		byteArray = concatenate(byteArray, bufArea);
		byteArray = concatenate(byteArray, bufSector);
		this.playerInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, PlayerInfo.AREA), byteArray));

		byteArray = new byte[] {};
		byteArray = concatenate(byteArray, bufX);
		byteArray = concatenate(byteArray, bufY);
		this.playerInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, PlayerInfo.AXIS), byteArray));

		//Player State
		this.playerInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, PlayerInfo.TURN), bufFacing));
	}

	private void generateLoadData(Game game) {
		Player gamePlayer = game.getPlayer();
		//Get name
		gamePlayer.setName(new String(this.playerInfo.player_name));
		//Get gender
		gamePlayer.setGender(this.playerInfo.player_gender[0] == 0x1 ? Boolean.TRUE.booleanValue() : Boolean.FALSE.booleanValue());
		//Get menu options.
		if (!game.getStartMenu().getSubMenusList().isEmpty())
			game.getStartMenu().getSubMenusList().clear();
		for (int i = 0; i < this.playerInfo.startMenu.size(); i++) {
			byte[] data = this.playerInfo.startMenu.get(i);
			switch (new String(data)) {
				case StartMenu.ITEM_NAME_EXIT:
					game.getStartMenu().addMenuItem(new DummyMenu(StartMenu.ITEM_NAME_EXIT, "Close this menu", "Close this menu", game));
					break;
				case StartMenu.ITEM_NAME_INVENTORY:
					game.getStartMenu().addMenuItem(new Inventory(StartMenu.ITEM_NAME_INVENTORY, "Open the bag.", "Open the bag.", game).initialize(game.getPlayer().keys));
					break;
				case StartMenu.ITEM_NAME_SAVE:
					game.getStartMenu().addMenuItem(new Save(StartMenu.ITEM_NAME_SAVE, "Save the game.", "Save the game.", game).initialize(game.getPlayer().keys));
					break;
			}
		}
		//Get inventory items
		Inventory inventory = game.getStartMenu().getInventory();
		for (int k = 0; k < this.playerInfo.getAllItemsList().size(); k++) {
			ArrayList<byte[]> list = this.playerInfo.getAllItemsList().get(k);
			for (int i = 0; i < list.size(); i++) {
				byte[] data = list.get(i);
				byte[] name = new byte[data[0]];
				byte offset = 0x0;
				for (; offset < data[0]; offset++)
					name[offset] = data[offset + 0x1];
				offset++;
				int id = data[offset] | data[offset + 1] | data[offset + 2] | data[offset + 3];
				offset += 4;
				int quantity = data[offset] | data[offset + 1] | data[offset + 2] | data[offset + 3];

				ItemText itemText = WorldConstants.items.get(id);
				Item item = null;
				switch (itemText.type) {
					case DUMMY:
						item = new DummyItem(game, itemText.itemName, itemText.description, itemText.category, itemText.id);
						break;
					case ACTION:
						item = new ActionItem(game, itemText.itemName, itemText.description, itemText.category, itemText.id);
						break;
					default:
						item = null;
						break;
				}
				if (item != null) {
					for (; quantity > 0; quantity--)
						inventory.addItem(itemText, item);
				}
			}
		}
		//Get current area
		//TODO: Probably need to set world ID first before setting the current area ID and SECTOR.
		int value = this.playerInfo.player_current_area_id[0] | this.playerInfo.player_current_area_id[1] | this.playerInfo.player_current_area_id[2] | this.playerInfo.player_current_area_id[3];
		game.getWorld().setCurrentArea(WorldConstants.convertToArea(WorldConstants.getAllAreas(), value));
		value = this.playerInfo.player_current_area_sector_id[0] | this.playerInfo.player_current_area_sector_id[1] | this.playerInfo.player_current_area_sector_id[2] | this.playerInfo.player_current_area_sector_id[3];
		game.getWorld().getCurrentArea().setSectorID(value);
		//Get Player Position
		int x = this.playerInfo.player_x[0] | this.playerInfo.player_x[1] | this.playerInfo.player_x[2] | this.playerInfo.player_x[3];
		int y = this.playerInfo.player_y[0] | this.playerInfo.player_y[1] | this.playerInfo.player_y[2] | this.playerInfo.player_y[3];
		game.getPlayer().setAreaPosition(x, y);
		game.getWorld().getCurrentArea().setPlayerX(x);
		game.getWorld().getCurrentArea().setPlayerY(y);
		game.getWorld().getCurrentArea().setPlayer(game.getPlayer());

		//Get Player direction facing.
		value = this.playerInfo.player_facing[0] | this.playerInfo.player_facing[1] | this.playerInfo.player_facing[2] | this.playerInfo.player_facing[3];
		game.getPlayer().setFacing(value);

	}

	public static void save(Game game, String filename) {
		GameSave data = new GameSave();
		data.generateSaveData(game);
		File save = new File(filename);
		if (save.isFile())
			save.delete();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(save, "rw");
			data.write(raf);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				raf.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void load(Game game, String filename) {
		File load = new File(filename);
		if (load.isFile()) {
			GameSave data = new GameSave();
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(load, "r");
				data.read(raf);
				data.generateLoadData(game);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					raf.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks to see if the game saved data exists in relative to where the game is located.
	 * 
	 * <p>
	 * Note that {@link java.io.File#isFile() File.isFile()} is used to check for saved data that is generated by the game. This is because the saved data file generated by the game is a <i>normal</i> file.
	 * 
	 * @param filename
	 *            The file name of the saved data.
	 * @return True, if the saved data file exists. False, if otherwise.
	 * */
	public static boolean check(String filename) {
		File save = new File(filename);
		return save.isFile();
	}

	public static byte[] concatenate(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
