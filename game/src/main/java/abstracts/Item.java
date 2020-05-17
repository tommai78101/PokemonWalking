/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package abstracts;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import entity.Player;
import interfaces.Renderable;
import item.Bicycle;
import item.ModdedItem;
import level.Area;
import level.PixelData;
import main.Game;
import menu.Inventory;
import resources.Art;
import screen.Scene;
import utility.DialogueBuilder;

/**
 * Any base implementations of the abstract class object, Item, will need to implement or devise a
 * way to create Dialogues associated with that item object.
 * 
 * @author tlee
 *
 */
public abstract class Item extends Entity implements Comparable<Item>, Renderable {

	public enum Category {
		// @formatter:off
		POTIONS(0x00), 
		KEYITEMS(0x01), 
		POKEBALLS(0x02), 
		TM_HM(0x03);
		// @formatter:on

		private int id;

		private Category(int value) {
			this.id = value;
		}

		/**
		 * Obtains a Category enum value that matches the given ID number.
		 * 
		 * <p>
		 * If there is no Category that comes after the last element, it will give the first element, and
		 * wraps from there.
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
	protected String dialogueName;
	protected String description;
	protected Category category;
	protected boolean picked;
	protected int id;
	protected List<String> availableCommands;
	protected Dialogue pickedDialogue;
	protected Dialogue tossedDialogue;
	protected Inventory inventory;

	private boolean afterItemActionOccurred = false;

	public Item(String name, String description, Category category, int id) {
		this.setName(name);
		this.setDescription(description);
		this.setCategory(category);
		this.setID(id);
		this.picked = false;

		// We do not preload available commands from in-game data. This is handled automatically by the
		// subclasses of Item.
		this.availableCommands = new ArrayList<>();

		try (Formatter formatter = new Formatter()) {
			formatter.format("%-1" + Dialogue.HALF_STRING_LENGTH + "s", name);
			this.dialogueName = formatter.toString();
		}
	}

	public Item(ModdedItem itemText) {
		this.setName(itemText.itemName);
		this.setDescription(itemText.description);
		this.setCategory(itemText.category);
		this.setID(itemText.id);
		this.picked = false;
		this.availableCommands = new ArrayList<>();
		this.initializeCommands(itemText);

		try (Formatter formatter = new Formatter()) {
			formatter.format("%-1" + Dialogue.HALF_STRING_LENGTH + "s", itemText.itemName);
			this.dialogueName = formatter.toString();
		}
	}

	public void setName(String value) {
		if (value == null || value.isBlank() || value.isEmpty()) {
			value = "UnknownItem";
		}
		this.name = value;
	}

	public void setDescription(String value) {
		if (value == null || value.isBlank() || value.isEmpty()) {
			value = "Unknown Item was found.";
		}
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

	/**
	 * This returns the item's name, with extra padded whitespace. This name fills up the entire row of
	 * the dialogue box.
	 * 
	 * @return
	 */
	public String getDialogueName() {
		return this.dialogueName;
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
		this.hide();

		this.afterItemActionOccurred = true;
		if (this.pickedDialogue == null || !this.pickedDialogue.isReady()) {
			this.pickedDialogue = DialogueBuilder.createText(
				// Intentionally setting the first row of the dialogue to be only the item name, then the second
				// sentence to be a fixed sized sentence.
				this.getDialogueName() + "has been found.",
				Dialogue.MAX_STRING_LENGTH, DialogueType.SPEECH, true
			);
			this.pickedDialogue.setShowDialog(true);
		}
		if (!(this.pickedDialogue.isDialogueCompleted() && this.pickedDialogue.isShowingDialog())) {
			this.pickedDialogue.tick();
		}
		// We want the player to interact with the dialogue for the final time, before dismissing it.
		else if (Game.keys.isPrimaryPressed() || Game.keys.isSecondaryPressed()) {
			this.afterItemActionOccurred = false;
			this.pickedDialogue.clearDialogueLines();
			this.pickedDialogue.setShowDialog(false);

			// We need to completely remove the item out from the area world.
		}
	}

	public void drop() {
		this.picked = false;
		this.reveal();

		this.afterItemActionOccurred = true;
		if (this.tossedDialogue == null || !this.tossedDialogue.isReady()) {
			this.tossedDialogue = DialogueBuilder.createText(
				// Intentionally setting the first row of the dialogue to be only the item name, then the second
				// sentence to be a fixed sized sentence.
				this.getDialogueName() + "was tossed away.",
				Dialogue.MAX_STRING_LENGTH, DialogueType.SPEECH, true
			);
			this.tossedDialogue.setShowDialog(true);
		}
		if (!(this.tossedDialogue.isDialogueCompleted() && this.tossedDialogue.isShowingDialog())) {
			this.tossedDialogue.tick();
		}
		// We want the player to interact with the dialogue for the final time, before dismissing it.
		else if (Game.keys.isPrimaryPressed() || Game.keys.isSecondaryPressed()) {
			this.afterItemActionOccurred = false;
			this.tossedDialogue.clearDialogueLines();
			this.tossedDialogue.setShowDialog(false);
		}
	}

	public void hide() {
		this.getPixelData().hide();
	}

	public void reveal() {
		this.getPixelData().reveal();
	}

	/**
	 * Checks whether this item can be tossed away or sold in the PokeMart.
	 * 
	 * This can also be used to check whether the item is a Key Item or not.
	 * 
	 * @return True always for any item that is not a Key Item. False if the item is a Key Item.
	 */
	public boolean canBeTossed() {
		return true;
	}

	/**
	 * Two conditions must be satisfied before claiming the item has been properly picked up.<br/>
	 * <ol>
	 * <li>The action to initiate "picking up" has been triggered.
	 * <li>The event "after picking up" has finished triggering.
	 * </ol>
	 * The conditional check has a short-circuited condition where if the "after picking up" event is
	 * currently triggered, the item is still not properly picked up.
	 * 
	 * @return True if the item has been properly picked up. False, if otherwise.
	 */
	public boolean isPickedUp() {
		return !this.afterItemActionOccurred && (this.picked);
	}

	/**
	 * This checks if the event, "after picking up" has finished triggering.
	 * 
	 * @return True if the item has finished triggering the "after picked up" event.
	 */
	public boolean isFinishedPickingUp() {
		return !this.afterItemActionOccurred;
	}

	public void droppedAt(Area area, Player player) {
		this.drop();
		this.dropAt(area, player);
	}

	/**
	 * Items must provide a default available commands to the Inventory menu dialogue.
	 * <p>
	 * More specific items can provide additional available commands to the Inventory menu.
	 */
	public List<String> getAvailableCommands() {
		return this.availableCommands;
	}

	public void initializeCommands(ModdedItem itemText) {
		this.availableCommands.add(0, Inventory.MENU_CANCEL);
		if (itemText.tossCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_TOSS);
		if (itemText.setCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_SET);
		if (itemText.useCommandFlag)
			this.availableCommands.add(0, Inventory.MENU_USE);
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public abstract void doAction(Game game);

	// TODO: Add function that allows the item to be placed at.
	public abstract void dropAt(Area area, Player player);

	@Override
	public void tick() {
		// Items rarely have update ticks.
		return;
	}

	@Override
	public void render(Scene output, Graphics graphics, int xOffset, int yOffset) {
		if (!this.picked) {
			output.blit(Art.item, xOffset, yOffset);
		}
		if (this.afterItemActionOccurred) {
			if (this.pickedDialogue.isReady())
				this.pickedDialogue.render(output, graphics);
			else if (this.tossedDialogue.isReady())
				this.tossedDialogue.render(output, graphics);
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
		// Assume the pixel data is of type Item.
		Item item = null;

		// Using unique item IDs to determine the item type to use.
		int red = pixelData.getRed();
		int blue = pixelData.getBlue();
		// TODO(Thompson): Figure out how to assign items from the Area map based on PixelData.
		// Red - Item Unique ID
		switch (red) {
			case 0x03: {// Bicycle
				// Key item check
				if (blue == 0x01) {
					// This is a key item.
					item = new Bicycle(pixelData);
				}
				break;
			}
			default:
				break;
		}
		return item;
	}
}
