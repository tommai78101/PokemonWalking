package utility;

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

import abstracts.Item;
import abstracts.Item.Category;
import item.Bicycle;
import item.DummyItem;
import item.ModdedItem;
import item.KeyItem;
import level.WorldConstants;

public class ItemBuilder {

	public static enum Tag {
		POTIONS("POTIONS"),
		KEYITEMS("KEYITEMS"),
		POKEBALLS("POKEBALLS"),
		TM_HM("TM HM"),
		ALL("ALL");

		private String name;

		private Tag(String value) {
			this.name = value;
		}

		public String toGameName() {
			return this.name;
		}

		public static Tag convert(String value) {
			value = value.toUpperCase();
			Tag[] tags = Tag.values();
			for (Tag tag : tags) {
				if (tag.toGameName().equals(value)) {
					return tag;
				}
			}
			return null;
		}
	}

	public static enum ItemCommand {
		SET("$"),
		USE("!"),
		TOSS("&");

		private String scriptValue;

		private ItemCommand(String value) {
			this.scriptValue = value;
		}

		public String getScript() {
			return this.scriptValue;
		}

		public static ItemCommand convert(char value) {
			switch (value) {
				case '$':
					return SET;
				case '!':
					return USE;
				case '&':
					return TOSS;
				default:
					return null;
			}
		}
	}

	private static final String TAG_POTIONS = "POTIONS";
	private static final String TAG_KEYITEMS = "KEYITEMS";
	private static final String TAG_POKEBALLS = "POKEBALLS";
	private static final String TAG_TM_HM = "TM HM";
	private static final String TAG_ALL = "ALL";
	private static final String FLAG_SET_COMMAND = "$";
	private static final String FLAG_USE_COMMAND = "!";
	private static final String FLAG_TOSS_COMMAND = "&";
	private static final String ITEM_DELIMITER = ";";

	public static HashMap<Integer, ModdedItem> loadItemResources(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ModdedItem itemText = new ModdedItem();
			HashMap<Integer, ModdedItem> result = new HashMap<>();
			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					itemText.type = ModdedItem.Type.getType(line.split("%")[1]);
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
					Tag tag = Tag.convert(value);
					switch (tag) {
						case POTIONS:
							itemText.category = Category.POTIONS;
							break;
						case KEYITEMS:
							itemText.category = Category.KEYITEMS;
							break;
						case POKEBALLS:
							itemText.category = Category.POKEBALLS;
							break;
						case TM_HM:
							itemText.category = Category.TM_HM;
							break;
						case ALL:
							itemText.skipCheckCategory = true;
							break;
						default:
							// Intentionally doing nothing.
							break;
					}
				}
				else if (line.startsWith(ItemBuilder.ITEM_DELIMITER)) {
					itemText.done = true;
				}
				else {
					ItemCommand command = ItemCommand.convert(line.charAt(0));
					if (command != null) {
						switch (command) {
							case SET:
								itemText.setCommandFlag = true;
								break;
							case USE:
								itemText.useCommandFlag = true;
								break;
							case TOSS:
								itemText.tossCommandFlag = true;
								break;
						}
					}
				}

				if (itemText.isComplete()) {
					itemText.id = id;
					result.put(id, itemText);
					itemText = new ModdedItem();
					id++;
				}
			}
			return result;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static List<Map.Entry<ModdedItem, Item>> loadItems(String filename) {
		List<Map.Entry<ModdedItem, Item>> result = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ModdedItem itemText = new ModdedItem();

			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					itemText.type = ModdedItem.Type.getType(line.split("%")[1]);
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
					if (value.equals(ItemBuilder.TAG_POTIONS)) {
						itemText.category = Category.POTIONS;
					}
					else if (value.equals(ItemBuilder.TAG_KEYITEMS)) {
						itemText.category = Category.KEYITEMS;
					}
					else if (value.equals(ItemBuilder.TAG_POKEBALLS)) {
						itemText.category = Category.POKEBALLS;
					}
					else if (value.equals(ItemBuilder.TAG_TM_HM)) {
						itemText.category = Category.TM_HM;
					}
					else if (value.equals(ItemBuilder.TAG_ALL)) {
						itemText.skipCheckCategory = true;
					}
				}
				else if (line.startsWith(ItemBuilder.FLAG_SET_COMMAND)) {
					itemText.setCommandFlag = true;
				}
				else if (line.startsWith(ItemBuilder.FLAG_USE_COMMAND)) {
					itemText.useCommandFlag = true;
				}
				else if (line.startsWith(ItemBuilder.FLAG_TOSS_COMMAND)) {
					itemText.tossCommandFlag = true;
				}
				else if (line.startsWith(ItemBuilder.ITEM_DELIMITER)) {
					itemText.done = true;
				}

				if (itemText.isComplete()) {
					itemText.id = id;
					result.add(new AbstractMap.SimpleEntry<>(itemText, ItemBuilder.createNewItem(itemText)));
					itemText = new ModdedItem();
					id++;
				}
			}

			Collections.sort(result, new Comparator<Map.Entry<ModdedItem, Item>>() {
				@Override
				public int compare(Entry<ModdedItem, Item> e1, Entry<ModdedItem, Item> e2) {
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

	public static Item createNewItem(ModdedItem text) {
		Item result = null;
		switch (text.type) {
			case DUMMY:
				result = new DummyItem(text.itemName, text.description, text.category, text.id);
				break;
			case ACTION:
				switch (text.id) {
					case WorldConstants.ITEM_BICYCLE:
						result = new Bicycle(text.itemName, text.description, text.category);
						break;
					default:
						result = new KeyItem(text.itemName, text.description, text.category, text.id);
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
}
