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

import item.ItemText;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import level.Area;
import level.PixelData;
import level.WorldConstants;
import main.Game;
import submenu.DummyMenu;
import submenu.Inventory;
import submenu.Save;
import abstracts.Item;
import abstracts.SubMenu;
import dialogue.StartMenu;
import entity.Player;
import interfaces.Gender.GenderType;

public class GameSave {
	/*
	 * Refer to the documentation on the file format used for saving game data.
	 * 
	 * All saving/loading schemes use the documentation as guidelines. It will be
	 * changed in the future, as the development continues.
	 */
	public static final byte[] SIGNATURE = new byte[] { (byte) 137, 0x53, 0x41, 0x56, 0x20, 0x20, 0x20, 0x20 };

	public HeaderInfo headerInfo;
	public PlayerInfo playerInfo;
	public AreaInfo areaInfo;

	private GameSave() {
		// TODO (6/25/2015): Needs a way to load save data for modded map.
		headerInfo = new HeaderInfo();
		playerInfo = new PlayerInfo();
		areaInfo = new AreaInfo();
	}

	private void write(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		headerInfo.write(raf);
		playerInfo.write(raf);
		areaInfo.write(raf);
	}

	private void read(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		headerInfo.read(raf);
		playerInfo.read(raf);
		areaInfo.read(raf);
	}

	private void generateSaveData(Game game) {
		// TODO: Save player facing direction data into the game save.

		Player gamePlayer = game.getPlayer();
		if (this.playerInfo != null)
			this.playerInfo.reset();

		// Name of the player.
		System.arraycopy(gamePlayer.getByteName(), 0, this.playerInfo.player_name, 0, 16); // 16 is the name length
																							// limit.
		byte[] byteArray = concatenate(PlayerInfo.NAME, gamePlayer.getByteName());
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, byteArray));

		// Gender of the player. Size of data is 1 byte.
		this.playerInfo.player_gender[this.playerInfo.player_gender.length - 1] = gamePlayer.getGender().getByte();

		byteArray = concatenate(PlayerInfo.GNDR, gamePlayer.getGender().getByte());
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, byteArray));

		// All active submenus the player has unlocked.
		byteArray = new byte[] {};
		List<Map.Entry<Integer, SubMenu>> startMenuList = game.getStartMenu().getSubMenusList();
		for (Map.Entry<Integer, SubMenu> entry : startMenuList) {
			SubMenu sub = entry.getValue();
			byteArray = concatenate(byteArray, concatenate(new byte[] { 0x0 }, sub.getSubMenuData()));
			this.playerInfo.startMenu.add(sub.getSubMenuData());
		}
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, concatenate(PlayerInfo.MENU, byteArray)));

		// Inventory
		byte listType = 0x1;
		this.playerInfo.increment(concatenate(new byte[] { 0x0 }, PlayerInfo.ITEM));
		for (List<Map.Entry<Item, Integer>> itemList : game.getStartMenu().getInventory().getAllItemsList()) {
			if (itemList.size() - 1 > 0) {
				byteArray = new byte[] { listType }; // Type of list (Potions/KeyItems/Pokeball/TMHM)
				byteArray = concatenate(byteArray,
						ByteBuffer.allocate(2).putChar((char) ((itemList.size() - 1) & 0xFFFF)).array()); // How many
																											// items in
																											// a list?
				byteArray = concatenate(byteArray, ByteBuffer.allocate(1).put((byte) itemList.size()).array()); // ItemInfo
																												// size.
				byte[] itemInfo = null;
				for (int i = 0; i < itemList.size() - 1; i++) {
					Map.Entry<Item, Integer> entry = itemList.get(i);
					ByteBuffer buffer = ByteBuffer.allocate(1);
					buffer.put((byte) (entry.getKey().getName().getBytes().length & 0xFF));
					itemInfo = buffer.array();
					buffer = ByteBuffer.allocate(4 * 2);
					buffer.putInt(entry.getKey().getID()); // Item ID
					buffer.putInt(entry.getValue()); // Item quantity
					itemInfo = concatenate(itemInfo, entry.getKey().getName().getBytes());
					itemInfo = concatenate(itemInfo, buffer.array());
					byteArray = concatenate(byteArray, itemInfo); // Item Name Length <- Item Name <- ID <- Quantity
				}
				this.playerInfo.getAllItemsList().get(listType - 0x1).add(itemInfo);
				this.playerInfo.increment(byteArray);
				listType++;
			}
		}

		// Current Area & Player State
		Area currentArea = game.getWorld().getCurrentArea();
		byte[] bufArea = ByteBuffer.allocate(4).putInt(currentArea.getAreaID()).array();
		byte[] bufSector = ByteBuffer.allocate(4).putInt(currentArea.getSectorID()).array();
		byte[] bufX = ByteBuffer.allocate(4).putInt(currentArea.getPlayerXInArea()).array();
		byte[] bufY = ByteBuffer.allocate(4).putInt(currentArea.getPlayerYInArea()).array();
		byte[] bufFacing = ByteBuffer.allocate(4).putInt(gamePlayer.getFacing()).array();
		for (int i = 0; i < 4; i++) {
			this.areaInfo.current_area_id[i] = bufArea[i];
			this.areaInfo.current_area_sector_id[i] = bufSector[i];
			this.playerInfo.player_x[i] = bufX[i];
			this.playerInfo.player_y[i] = bufY[i];
			this.playerInfo.player_facing[i] = bufFacing[i];
		}
		byteArray = new byte[] {};
		byteArray = concatenate(byteArray, bufArea);
		byteArray = concatenate(byteArray, bufSector);
		// Size of total AreaInfo chunk + size of AreaInfo header chunk for the two
		// 0x0s.
		this.areaInfo.increment(concatenate(concatenate(new byte[] { 0x0, 0x0 }, AreaInfo.AREA), byteArray));
		bufArea = bufSector = null;

		byteArray = new byte[] {};
		byteArray = concatenate(byteArray, bufX);
		byteArray = concatenate(byteArray, bufY);
		this.playerInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, PlayerInfo.AXIS), byteArray));
		bufX = bufY = null;

		// Player State
		this.playerInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, PlayerInfo.TURN), bufFacing));
		bufFacing = null;

		// Area Data
		byteArray = new byte[] {};
		List<Area> areaList = game.getWorld().getAllAreas();
		for (Area area : areaList) {
			ArrayList<PixelData> pixelList = area.getModifiedPixelDataList();
			if (!pixelList.isEmpty()) {
				for (PixelData px : pixelList) {
					byte[] pixelArray = ByteBuffer.allocate(4 * 5).putInt(area.getAreaID()).putInt(area.getSectorID())
							.putInt(px.xPosition).putInt(px.yPosition).putInt(px.getColor()).array();
					this.areaInfo.changedPixelData.add(pixelArray);
					byteArray = concatenate(byteArray, pixelArray);
				}
				this.areaInfo.increment(concatenate(concatenate(new byte[] { 0x0 }, AreaInfo.PIXELDATA), byteArray));
			}
		}
	}

	private void generateLoadData(Game game) throws Exception {
		// TODO: Load player facing direction data from the game save.

		// Get Player
		Player gamePlayer = game.getPlayer();

		// Get name
		gamePlayer.setName(new String(this.playerInfo.player_name));

		// Get gender
		byte genderByte = this.playerInfo.player_gender[0];
		gamePlayer.setGender(GenderType.determineGender(genderByte));

		// Get menu options.
		if (!game.getStartMenu().getSubMenusList().isEmpty())
			game.getStartMenu().getSubMenusList().clear();
		for (int i = 0; i < this.playerInfo.startMenu.size(); i++) {
			byte[] data = this.playerInfo.startMenu.get(i);
			switch (new String(data)) {
			case StartMenu.ITEM_NAME_EXIT:
				game.getStartMenu().addMenuItem(
						new DummyMenu(StartMenu.ITEM_NAME_EXIT, "Close this menu", "Close this menu", game));
				break;
			case StartMenu.ITEM_NAME_INVENTORY:
				game.getStartMenu().addMenuItem(
						new Inventory(StartMenu.ITEM_NAME_INVENTORY, "Open the bag.", "Open the bag.", game)
								.initialize(game.getPlayer().keys));
				break;
			case StartMenu.ITEM_NAME_SAVE:
				game.getStartMenu()
						.addMenuItem(new Save(StartMenu.ITEM_NAME_SAVE, "Save the game.", "Save the game.", game)
								.initialize(game.getPlayer().keys));
				break;
			}
		}

		// Get inventory items
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
				int id = (data[offset] << 24) | (data[offset + 1] << 16) | (data[offset + 2] << 8) | data[offset + 3];
				offset += 4;
				int quantity = (data[offset] << 24) | (data[offset + 1] << 16) | (data[offset + 2] << 8)
						| data[offset + 3];

				ItemText itemText = null;
				ArrayList<Map.Entry<ItemText, Item>> itemList = WorldConstants.items;
				for (Map.Entry<ItemText, Item> e : itemList) {
					if (e.getKey().id == id) {
						itemText = e.getKey();
						break;
					}
				}
				for (; quantity > 0; quantity--) {
					inventory.addItem(itemText);
				}
			}
		}

		// Get current area
		// TODO: Probably need to set world ID first before setting the current area ID
		// and SECTOR.
		int currentAreaID = (this.areaInfo.current_area_id[0] & 0xFF) << 24
				| (this.areaInfo.current_area_id[1] & 0xFF) << 16 | (this.areaInfo.current_area_id[2] & 0xFF) << 8
				| (this.areaInfo.current_area_id[3] & 0xFF);
		game.getWorld().setCurrentArea(WorldConstants.convertToArea(game.getWorld().getAllAreas(), currentAreaID));
		if (game.getWorld().getCurrentArea() == null)
			throw new Exception("There is no area set.");
		int currentSectorID = (this.areaInfo.current_area_sector_id[0] & 0xFF) << 24
				| (this.areaInfo.current_area_sector_id[1] & 0xFF) << 16
				| (this.areaInfo.current_area_sector_id[2] & 0xFF) << 8
				| this.areaInfo.current_area_sector_id[3] & 0xFF;
		game.getWorld().getCurrentArea().setSectorID(currentSectorID);

		// Get Player Position
		int x = (this.playerInfo.player_x[0] << 24) | (this.playerInfo.player_x[1] << 16)
				| (this.playerInfo.player_x[2] << 8) | this.playerInfo.player_x[3];
		int y = (this.playerInfo.player_y[0] << 24) | (this.playerInfo.player_y[1] << 16)
				| (this.playerInfo.player_y[2] << 8) | this.playerInfo.player_y[3];
		game.getPlayer().setAreaPosition(x, y);
		game.getWorld().getCurrentArea().setPlayerX(x);
		game.getWorld().getCurrentArea().setPlayerY(y);
		game.getWorld().getCurrentArea().setPlayer(game.getPlayer());

		// Get Player direction facing.
		int facing = (this.playerInfo.player_facing[0] & 0xFF) << 24 | (this.playerInfo.player_facing[1] & 0xFF) << 16
				| (this.playerInfo.player_facing[2] & 0xFF) << 8 | this.playerInfo.player_facing[3] & 0xFF;
		game.getPlayer().setFacing(facing);

		// Get modified pixel data for all areas.
		if (this.areaInfo.changedPixelData.size() > 0) {
			List<Area> loadedAreas = game.getWorld().getAllAreas();
			for (Iterator<byte[]> it = this.areaInfo.changedPixelData.iterator(); it.hasNext();) {
				byte[] data = it.next();
				int offset = 0;
				int areaID = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
						| (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
				offset += 4;

				// Currently, unknown use at the moment.
				@SuppressWarnings("unused")
				int sectorID = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
						| (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
				offset += 4;

				LOADED_AREA: for (Area area : loadedAreas) {
					if (areaID == area.getAreaID()) {
						int xPixelData = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
								| (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
						offset += 4;
						int yPixelData = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
								| (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
						offset += 4;
						int color = (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16
								| (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;

						PixelData pxData = new PixelData(color, xPixelData, yPixelData);
						area.getModifiedPixelDataList().add(pxData);
						area.loadModifiedPixelDataList();
						break LOADED_AREA;
					}
				}
			}
			game.getWorld().refresh();
		}

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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void load(Game game, String filename) {
		File load = null;
		if (WorldConstants.isModsEnabled.booleanValue()) {
			File modDirectory = new File("mod");
			if (modDirectory.exists() && modDirectory.isDirectory()) {
				File[] files = modDirectory.listFiles();
				NESTED_LOOP: for (File f : files) {
					if (f.getName().equals("area")) {
						File[] saves = f.listFiles();
						for (File saveFile : saves) {
							if (saveFile.getName().equals(filename)) {
								load = saveFile;
								break NESTED_LOOP;
							}
						}
					}
				}
			}
		} else
			load = new File(filename);
		if (load != null && load.isFile()) {
			GameSave data = new GameSave();
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(load, "r");
				data.read(raf);
				data.generateLoadData(game);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks to see if the game saved data exists in relative to where the game is
	 * located. The save data is a SAV binary format file.
	 * 
	 * <p>
	 * Note that {@link java.io.File#isFile() File.isFile()} is used to check for
	 * saved data that is generated by the game. This is because the saved data file
	 * generated by the game is a <i>normal</i> file.
	 * 
	 * <p>
	 * For more information on the file format, please read the documentation
	 * provided.
	 * 
	 * @param filename The file name of the saved data.
	 * @return True, if the saved data file exists. False, if otherwise.
	 */
	public static boolean check(String filename) {
		File save = new File(filename);
		boolean fileIsValid = true;
		DataInputStream dataInputStream = null;
		try {
			dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(save)));
			byte sizeOfChunk = dataInputStream.readByte();
			for (int i = 0; i < sizeOfChunk / 4; i++) {
				switch (i) {
				case 0: {
					// Identification Code
					byte identity = '1' & 0xFF;
					for (int j = 0; j < 4; j++) {
						byte byteData = dataInputStream.readByte();
						if (j < 3)
							continue;
						if (byteData != identity)
							fileIsValid = false;
					}
					break;
				}
				case 1: {
					// Header Code
					byte[] header = { 0x48, 0x45, 0x41, 0x44 };
					for (int j = 0; j < 4; j++) {
						byte byteData = dataInputStream.readByte();
						if (byteData != header[j])
							fileIsValid = false;
					}
					break;
				}
				case 2: {
					// File format extension
					byte[] format = { 0x2E, 0x53, 0x41, 0x56 };
					for (int j = 0; j < 4; j++) {
						byte byteData = dataInputStream.readByte();
						if (byteData != format[j])
							fileIsValid = false;
					}
					break;
				}
				default: {
					fileIsValid = false;
					break;
				}
				}
			}
			if (!fileIsValid)
				throw new IOException("Save file is invalid.");
		} catch (FileNotFoundException e) {
			// Expected, when no game save file is found.
			// e.printStackTrace();
			fileIsValid = false;
		} catch (IOException e) {
			// Expected, when no game save file is found.
			// e.printStackTrace();
			fileIsValid = false;
		} finally {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return (fileIsValid ? save.isFile() : false);
	}

	public static byte[] concatenate(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static byte[] concatenate(byte[] first, byte second) {
		byte[] result = new byte[first.length + 1];
		System.arraycopy(first, 0, result, 0, first.length);
		result[result.length - 1] = second;
		return result;
	}
}
