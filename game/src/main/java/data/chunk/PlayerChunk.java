/**
 * 
 */
package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import abstracts.Character.GenderType;
import abstracts.Chunk;
import abstracts.Item;
import abstracts.Item.Category;
import entity.Player;
import level.Area;
import level.OverWorld;
import main.Game;
import menu.Inventory;

/**
 * 
 * @author tlee
 */
public class PlayerChunk extends Chunk {
	public enum WalkState {
		IDLE(0x00),
		SWIM(0x01),
		RIDE(0x02);

		private byte value;

		private WalkState(int value) {
			this.value = (byte) value;
		}

		public boolean equals(WalkState other) {
			return this.value == other.value;
		}

		public byte getByte() {
			return this.value;
		}

		public static WalkState convert(byte value) {
			switch (value) {
				case 0x00:
					return WalkState.IDLE;
				case 0x01:
					return WalkState.SWIM;
				case 0x02:
					return WalkState.RIDE;
				default:
					return null;
			}
		}
	}

	public static final byte[] ChunkTag = "PLAY".getBytes();

	private String playerName;

	// Can support 255 genders. Zero is reserved.
	private byte gender;
	private int xPosition;
	private int yPosition;
	private byte facingDirection;
	private byte walkingState;

	public PlayerChunk() {
		this.playerName = null;
		this.gender = (byte) 0x0;
		this.xPosition = -1;
		this.yPosition = -1;
		this.facingDirection = (byte) 0x0;
		this.walkingState = (byte) 0x0;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		// Prepare
		Player player = game.getPlayer();
		OverWorld world = game.getWorld();

		short rafSize = raf.readShort();

		for (byte b : PlayerChunk.ChunkTag) {
			if (b != raf.readByte()) {
				throw new IOException("Player chunk tag did not match.");
			}
		}

		this.gender = raf.readByte();
		player.setGender(GenderType.determineGender(this.gender));

		this.xPosition = raf.readInt();
		this.yPosition = raf.readInt();
		player.setAreaPosition(this.xPosition, this.yPosition);
		world.getCurrentArea().setPlayer(player);
		world.getCurrentArea().setPlayerX(this.xPosition);
		world.getCurrentArea().setPlayerY(this.yPosition);

		this.facingDirection = raf.readByte();
		player.setFacing(this.facingDirection);

		this.walkingState = raf.readByte();
		WalkState state = WalkState.convert(this.walkingState);
		switch (state) {
			case IDLE:
				player.startsWalking();
				break;
			case RIDE:
				player.hopsOnBike();
				break;
			case SWIM:
				player.goesInWater();
				break;
			default:
				throw new IOException("Unsupported player walking state.");
		}

		// Potions -> KeyItems -> Pokéballs -> TMs/HMs
		int categorySize = Category.values().length;
		for (int c = 0; c < categorySize; c++) {
			short listSize = raf.readShort();
			byte listCategory = raf.readByte();
			for (short i = 0; i < listSize; i++) {
				InventoryItemChunk chunk = new InventoryItemChunk();
				chunk.setCategory(Category.convert(listCategory));
				chunk.read(game, raf);
			}
		}

		// Check chunk size at the last step.
		int size = this.getSize(game);
		if (size != rafSize) {
			throw new IOException("Unmatched player chunk size.");
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		Player player = game.getPlayer();
		OverWorld world = game.getWorld();
		Area currentArea = world.getCurrentArea();
		Inventory inventory = game.getInventory();

		raf.writeShort(this.getSize(game));
		raf.write(PlayerChunk.ChunkTag);
		raf.writeByte(player.getGender().getByte());
		raf.writeInt(currentArea.getPlayerXInArea());
		raf.writeInt(currentArea.getPlayerYInArea());
		raf.writeByte(player.getFacing());

		WalkState state = WalkState.IDLE;
		if (player.isRidingBicycle())
			state = WalkState.RIDE;
		else if (player.isInWater())
			state = WalkState.SWIM;
		this.walkingState = state.getByte();
		raf.writeByte(this.walkingState);

		// Potions -> KeyItems -> Pokéballs -> TMs/HMs
		// We have to go through them in order. We cannot guarantee the category order is the same when
		// using inventory.getAllItemsList().
		Map<Category, List<Map.Entry<Item, Integer>>> itemLists = inventory.getAllMappedItemsList();
		Category[] values = Category.values();
		for (Category category : values) {
			List<Map.Entry<Item, Integer>> list = itemLists.get(category);
			list = list.stream().filter(e -> !e.getKey().isReturnMenu()).collect(Collectors.toList());

			// Write the size of the list into the data.
			int listSize = list.size();
			raf.writeShort(listSize);
			raf.writeByte(category.getByte());

			for (int i = 0; i < listSize; i++) {
				Map.Entry<Item, Integer> entry = list.get(i);
				InventoryItemChunk chunk = new InventoryItemChunk();
				chunk.setCategory(category);
				chunk.setItem(entry.getKey());
				chunk.setCount(entry.getValue());
				chunk.write(game, raf);
			}
		}
	}

	@Override
	public int getSize(Game game) {
		// Prepare
		Inventory inventory = game.getInventory();
		Map<Category, List<Map.Entry<Item, Integer>>> mappedItems = inventory.getAllMappedItemsList();

		// Chunk tag name
		int size = PlayerChunk.ChunkTag.length;
		// Gender (byte)
		size += 1;
		// X and Y positions (both int)
		size += 2 * 4;
		// Facing direction (byte)
		size += 1;
		// Walking state (byte)
		size += 1;

		Category[] values = Category.values();
		for (Category c : values) {
			// Item list size (short)
			size += 2;
			// Item list category type (byte)
			size += 1;

			// Item chunk size.
			List<Map.Entry<Item, Integer>> items = mappedItems.get(c);
			items = items.stream().filter(e -> !e.getKey().isReturnMenu()).collect(Collectors.toList());
			int listSize = items.size();
			for (int i = 0; i < listSize; i++) {
				InventoryItemChunk chunk = new InventoryItemChunk();
				size += chunk.getSize(game);
			}
		}

		return size;
	}

}
