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
import enums.ItemCategories;
import enums.ItemTags;
import item.Bicycle;
import item.ReturnMenu;
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

	public static HashMap<Integer, ModdedItem> loadItemResources(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ModdedItem itemText = new ModdedItem();
			HashMap<Integer, ModdedItem> result = new HashMap<>();
			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (ItemTags.Type.beginsAt(line)) {
					itemText.type = ModdedItem.Type.getType(ItemTags.Type.removeItemTag(line));
				}
				else if (ItemTags.Name.beginsAt(line)) {
					itemText.itemName = ItemTags.Name.removeItemTag(line);
				}
				else if (ItemTags.Description.beginsAt(line)) {
					itemText.description = ItemTags.Description.removeItemTag(line);
				}
				else if (ItemTags.Category.beginsAt(line)) {
					// '^' is a special character, therefore, one must use backslashes to get the
					// literal form.
					String value = ItemTags.Category.removeItemTag(line);
					Tag tag = Tag.convert(value);
					switch (tag) {
						case POTIONS:
							itemText.category = ItemCategories.POTIONS;
							break;
						case KEYITEMS:
							itemText.category = ItemCategories.KEYITEMS;
							break;
						case POKEBALLS:
							itemText.category = ItemCategories.POKEBALLS;
							break;
						case TM_HM:
							itemText.category = ItemCategories.TM_HM;
							break;
						case ALL:
							itemText.skipCheckCategory = true;
							break;
						default:
							// Intentionally doing nothing.
							break;
					}
				}
				else if (ItemTags.Delimiter.beginsAt(line)) {
					itemText.done = true;
				}
				else {
					// The rest of the line is what available item options the item allows the player to use.
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
				if (ItemTags.Type.beginsAt(line)) {
					itemText.type = ModdedItem.Type.getType(ItemTags.Type.removeItemTag(line));
				}
				else if (ItemTags.Name.beginsAt(line)) {
					itemText.itemName = ItemTags.Name.removeItemTag(line);
				}
				else if (ItemTags.Description.beginsAt(line)) {
					itemText.description = ItemTags.Description.removeItemTag(line);
				}
				else if (ItemTags.Category.beginsAt(line)) {
					// '^' is a special character, therefore, one must use backslashes to get the
					// literal form.
					String value = ItemTags.Category.removeItemTag(line);
					if (ItemCategories.POTIONS.chunkEquals(value)) {
						itemText.category = ItemCategories.POTIONS;
					}
					else if (ItemCategories.KEYITEMS.chunkEquals(value)) {
						itemText.category = ItemCategories.KEYITEMS;
					}
					else if (ItemCategories.POKEBALLS.chunkEquals(value)) {
						itemText.category = ItemCategories.POKEBALLS;
					}
					else if (ItemCategories.TM_HM.chunkEquals(value)) {
						itemText.category = ItemCategories.TM_HM;
					}
					else if (ItemCategories.ALL.chunkEquals(value)) {
						itemText.skipCheckCategory = true;
					}
				}
				else if (ItemTags.SetCommand.beginsAt(line)) {
					itemText.setCommandFlag = true;
				}
				else if (ItemTags.UseCommand.beginsAt(line)) {
					itemText.useCommandFlag = true;
				}
				else if (ItemTags.TossCommand.beginsAt(line)) {
					itemText.tossCommandFlag = true;
				}
				else if (ItemTags.Delimiter.beginsAt(line)) {
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
				result = new ReturnMenu(text.itemName, text.description, text.category, text.id);
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
