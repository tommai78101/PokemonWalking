package item;

import submenu.Inventory;

public class ItemText {
	public enum Type {
		ALL("ALL"), DUMMY("DUMMY");

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

	public ItemText() {
		type = null;
		itemName = null;
		description = null;
		category = null;
		skipCheckCategory = false;
	}

	public Type type;
	public String itemName;
	public String description;
	public Inventory.Category category;
	public boolean skipCheckCategory;
	public int id;

	public boolean isComplete() {
		if (type == null || itemName == null || description == null || (!skipCheckCategory ? category == null : false))
			return false;
		return true;
	}
}
