/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package item;

import enums.ItemCategories;

/**
 * This class is dedicated for loading item data externally. This will be used to load modded items
 * from user-created mods.
 * 
 * @author tlee
 */
public class ModdedItem {
	public Type type;

	public String itemName;
	public String description;
	public ItemCategories category;
	public boolean skipCheckCategory;
	public int id;
	public boolean setCommandFlag;
	public boolean useCommandFlag;
	public boolean tossCommandFlag;
	public boolean done;

	public ModdedItem() {
		this.type = null;
		this.itemName = null;
		this.description = null;
		this.category = null;
		this.id = -1;
		this.skipCheckCategory = false;
		this.setCommandFlag = this.useCommandFlag = this.tossCommandFlag = false;
		this.done = false;
	}

	public enum Type {
		// @formatter:off
		ALL("ALL"), 
		DUMMY("DUMMY"), 
		ACTION("ACTION");
		// @formatter:on

		private String value;

		Type(String value) {
			this.value = value;
		}

		public static Type getType(String value) {
			for (Type t : Type.values()) {
				if (t.value.equals(value.toUpperCase()))
					return t;
			}
			return ALL;
		}
	}

	public boolean isComplete() {
		return this.done;
	}
}
