/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package item;

import abstracts.Item.Category;

public class ItemText {
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
	};

	public Type type;
	public String itemName;
	public String description;
	public Category category;
	public boolean skipCheckCategory;
	public int id;
	public boolean setCommandFlag;
	public boolean useCommandFlag;
	public boolean tossCommandFlag;
	public boolean done;

	public ItemText() {
		type = null;
		itemName = null;
		description = null;
		category = null;
		id = -1;
		skipCheckCategory = false;
		setCommandFlag = useCommandFlag = tossCommandFlag = false;
		done = false;
	}

	public boolean isComplete() {
		return done;
	}
}
