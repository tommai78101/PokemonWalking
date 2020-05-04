/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package abstracts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import entity.Player;
import interfaces.Renderable;
import item.ActionItem;
import item.Bicycle;
import item.DummyItem;
import item.ItemText;
import level.Area;
import level.PixelData;
import level.WorldConstants;
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

	private static final String TAG_POTIONS = "POTIONS";
	private static final String TAG_KEYITEMS = "KEYITEMS";
	private static final String TAG_POKEBALLS = "POKEBALLS";
	private static final String TAG_TM_HM = "TM_HM";
	private static final String TAG_ALL = "ALL";
	private static final String FLAG_SET_COMMAND = "$";
	private static final String FLAG_USE_COMMAND = "!";
	private static final String FLAG_TOSS_COMMAND = "&";
	private static final String ITEM_DELIMITER = ";";

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

	public static HashMap<Integer, ItemText> loadItemResources(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ItemText itemText = new ItemText();
			HashMap<Integer, ItemText> result = new HashMap<>();
			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					itemText.type = ItemText.Type.getType(line.split("%")[1]);
				}
				else if (line.startsWith("#")) {
					itemText.itemName = line.split("#")[1];
				}
				else if (line.startsWith("@")) {
					itemText.description = line.split("@")[1];
				}
				else if (line.startsWith("^")) {
					// '^' is a special character, therefore, one must use backslashes to get the
					// literal form.
					String value = line.split("\\^")[1];
					if (value.equals(Item.TAG_POTIONS)) {
						itemText.category = Category.POTIONS;
					}
					else if (value.equals(Item.TAG_KEYITEMS)) {
						itemText.category = Category.KEYITEMS;
					}
					else if (value.equals(Item.TAG_POKEBALLS)) {
						itemText.category = Category.POKEBALLS;
					}
					else if (value.equals(Item.TAG_TM_HM)) {
						itemText.category = Category.TM_HM;
					}
					else if (value.equals(Item.TAG_ALL)) {
						itemText.skipCheckCategory = true;
					}
				}
				else if (line.startsWith(Item.FLAG_SET_COMMAND)) {
					itemText.setCommandFlag = true;
				}
				else if (line.startsWith(Item.FLAG_USE_COMMAND)) {
					itemText.useCommandFlag = true;
				}
				else if (line.startsWith(Item.FLAG_TOSS_COMMAND)) {
					itemText.tossCommandFlag = true;
				}
				else if (line.startsWith(Item.ITEM_DELIMITER)) {
					itemText.done = true;
				}

				if (itemText.isComplete()) {
					itemText.id = id;
					result.put(id, itemText);
					itemText = new ItemText();
					id++;
				}
			}
			return result;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static List<Map.Entry<ItemText, Item>> loadItems(String filename) {
		List<Map.Entry<ItemText, Item>> result = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ItemText itemText = new ItemText();

			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					itemText.type = ItemText.Type.getType(line.split("%")[1]);
				}
				else if (line.startsWith("#")) {
					itemText.itemName = line.split("#")[1];
				}
				else if (line.startsWith("@")) {
					itemText.description = line.split("@")[1];
				}
				else if (line.startsWith("^")) {
					// '^' is a special character, therefore, one must use backslashes to get the
					// literal form.
					String value = line.split("\\^")[1];
					if (value.equals(Item.TAG_POTIONS)) {
						itemText.category = Category.POTIONS;
					}
					else if (value.equals(Item.TAG_KEYITEMS)) {
						itemText.category = Category.KEYITEMS;
					}
					else if (value.equals(Item.TAG_POKEBALLS)) {
						itemText.category = Category.POKEBALLS;
					}
					else if (value.equals(Item.TAG_TM_HM)) {
						itemText.category = Category.TM_HM;
					}
					else if (value.equals(Item.TAG_ALL)) {
						itemText.skipCheckCategory = true;
					}
				}
				else if (line.startsWith(Item.FLAG_SET_COMMAND)) {
					itemText.setCommandFlag = true;
				}
				else if (line.startsWith(Item.FLAG_USE_COMMAND)) {
					itemText.useCommandFlag = true;
				}
				else if (line.startsWith(Item.FLAG_TOSS_COMMAND)) {
					itemText.tossCommandFlag = true;
				}
				else if (line.startsWith(Item.ITEM_DELIMITER)) {
					itemText.done = true;
				}

				if (itemText.isComplete()) {
					itemText.id = id;
					result.add(new AbstractMap.SimpleEntry<>(itemText, Item.createNewItem(itemText)));
					itemText = new ItemText();
					id++;
				}
			}

			Collections.sort(result, new Comparator<Map.Entry<ItemText, Item>>() {
				@Override
				public int compare(Entry<ItemText, Item> e1, Entry<ItemText, Item> e2) {
					if (e1.getKey().id < e2.getKey().id)
						return -1;
					if (e1.getKey().id > e2.getKey().id)
						return 1;
					return 0;
				}
			});

			return result;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static Item createNewItem(ItemText text) {
		Item result = null;
		switch (text.type) {
			case DUMMY:
				result = new DummyItem(MainComponent.getGame(), text.itemName, text.description, text.category, text.id);
				break;
			case ACTION:
				switch (text.id) {
					case WorldConstants.ITEM_BICYCLE:
						result = new Bicycle(MainComponent.getGame(), text.itemName, text.description, text.category);
						break;
					default:
						result = new ActionItem(MainComponent.getGame(), text.itemName, text.description, text.category, text.id);
						break;
				}
				break;
			case ALL:
			default:
				result = null;
				break;
		}
		return result;
	}

	public static Item build(PixelData pixelData, int x, int y) {
		return null;
	}
}
