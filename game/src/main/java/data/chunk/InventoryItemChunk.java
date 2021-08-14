/**
 * 
 */
package data.chunk;

import java.io.IOException;
import java.io.RandomAccessFile;

import abstracts.Chunk;
import abstracts.Item;
import enums.ItemCategories;
import level.PixelData;
import main.Game;

/**
 * 
 * @author tlee
 */
public class InventoryItemChunk extends Chunk {
	// Category for the item list type.
	private ItemCategories category;
	private Item item;
	private int count;

	public void setCategory(ItemCategories category) {
		this.category = category;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setCount(Integer count) {
		this.setCount(count.intValue());
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void read(Game game, RandomAccessFile raf) throws IOException {
		this.category = ItemCategories.convert(raf.readByte());
		if (this.category == null) {
			throw new IOException("Unmatched inventory item chunk category.");
		}

		int itemPixelColor = raf.readInt();
		this.item = Item.build(new PixelData(itemPixelColor));
		if (this.item == null) {
			throw new IOException("Item cannot be created. Incorrect pixel data.");
		}

		this.count = raf.readShort();
		if (this.count > Short.MAX_VALUE) {
			throw new IOException("Inventory item count too large for (short) max value count.");
		}

		// Reloading the item into the inventory.
		this.item.setCategory(this.category);
		for (int i = 0; i < this.count; i++) {
			game.getInventory().addItem(this.item);
		}
	}

	@Override
	public void write(Game game, RandomAccessFile raf) throws IOException {
		raf.writeByte(this.category.getByte());
		raf.writeInt(this.item.getPixelData().getColor());
		raf.writeShort(this.count);
	}

	@Override
	public int getSize(Game game) {
		// Category byte id. (byte)
		int size = 1;
		// Item unique id value. (int)
		size += 4;
		// Item count. (short)
		size += 2;

		return size;
	}

}
