/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package abstracts;

import java.util.ArrayList;
import java.util.List;

import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import entity.Player;
import interfaces.Renderable;
import item.Bicycle;
import item.ItemText;
import level.Area;
import level.PixelData;
import main.Game;
import main.MainComponent;
import menu.Inventory;
import resources.Art;
import screen.Scene;
import utility.DialogueBuilder;

/**
 * Any base implementations of the abstract class object, Item, will need to implement or devise a way to create Dialogues associated with that item object.
 * 
 * @author tlee
 *
 */
public abstract class Item extends Entity implements Comparable<Item>, Renderable {

	public enum Category {
		// @formatter:off
		POTIONS(0), 
		KEYITEMS(1), 
		POKEBALLS(2), 
		TM_HM(3);
		// @formatter:on

		private int id;

		private Category(int value) {
			this.id = value;
		}

		/**
		 * Obtains a Category enum value that matches the given ID number.
		 * 
		 * <p>
		 * If there is no Category that comes after the last element, it will give the first element, and wraps from there.
		 * </p>
		 * 
		 * @param value
		 *            The ID number of the category that is to be obtained.
		 * 
		 * @return The category that matches the given ID number.
		 */
		public static Category getWrapped(int value) {
			Category[] categories = Category.values();
			if (value < 0)
				value = (categories.length - 1);
			if (value > categories.length - 1)
				value = 0;
			for (Category c : categories) {
				if (c.id == value)
					return categories[value];
			}
			return categories[0];
		}

		public int getID() {
			return this.id;
		}
	}

	protected String name;
	protected String description;
	protected Game game;
	protected Category category;
	protected boolean picked;
	protected int id;
	protected List<String> availableCommands;
	protected final List<Dialogue> dialogues;

	public Item(Game game, String name, String description, Category category, int id) {
		this.setName(name);
		this.setDescription(description);
		this.setCategory(category);
		this.setID(id);
		this.game = game;
		this.picked = false;
		this.availableCommands = new ArrayList<>();
		this.dialogues = new ArrayList<>();
	}

	public Item(Game game, ItemText itemText) {
		this.setName(itemText.itemName);
		this.setDescription(itemText.description);
		this.setCategory(itemText.category);
		this.setID(itemText.id);
		this.game = game;
		this.picked = false;
		this.availableCommands = new ArrayList<>();
		this.initializeCommands(itemText);
		this.dialogues = new ArrayList<>();
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setID(int value) {
		this.id = value;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public Category getCategory() {
		return this.category;
	}

	public int getID() {
		return this.id;
	}

	public void pick() {
		this.picked = true;
	}

	public void drop() {
		this.picked = false;
	}

	public void droppedAt(Area area, Player player) {
		this.drop();
		this.dropAt(area, player);
	}

	public List<String> getAvailableCommands() {
		return this.availableCommands;
	}

	public final List<Dialogue> getDialogues() {
		if (this.dialogues.isEmpty()) {
			this.dialogues.add(DialogueBuilder.createText("Error: Dialogue not found.", DialogueType.SPEECH));
		}
		return this.dialogues;
	}

	public void initializeCommands(ItemText itemText) {
		this.availableCommands.add(0, Inventory.MENU_CANCEL);
		if (itemText.tossCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_TOSS);
		if (itemText.setCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_SET);
		if (itemText.useCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_USE);
	}

	public abstract void doAction();

	// TODO: Add function that allows the item to be placed at.
	public abstract void dropAt(Area area, Player player);

	@Override
	public void render(Scene output, int xOffset, int yOffset) {
		if (!this.picked) {
			output.blit(Art.item, xOffset, yOffset);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Item other = (Item) obj;
		if (this.category != other.category) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.category == null) ? 0 : this.category.hashCode());
		result = prime * result + this.id;
		return result;
	}

	@Override
	public int compareTo(Item other) {
		return this.id - other.id;
	}

	public static Item build(PixelData pixelData) {
		//Assume the pixel data is of type Item.
		Item item = null;

		// Using unique item IDs to determine the item type to use.
		int red = pixelData.getRed();
		int blue = pixelData.getBlue();
		// TODO(Thompson): Figure out how to assign items from the Area map based on PixelData.
		//Red - Item Unique ID
		switch (red) {
			case 0x03: {// Bicycle
				//Key item check
				if (blue == 0x01) {
					//This is a key item.
					item = new Bicycle(MainComponent.getGame(), pixelData);
				}
				break;
			}
			default:
				break;
		}
		return item;
	}
}
