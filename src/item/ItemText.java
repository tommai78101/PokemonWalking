package item;

public class ItemText {
	public enum Type {
		DUMMY("DUMMY");

		private String value;

		Type(String value) {
			this.value = value;
		}

		public static Type getType(String value) {
			for (Type t : Type.values()) {
				if (t.value.equals(value.toUpperCase()))
					return t;
			}
			return DUMMY;
		}
	};

	public Type type;
	public String itemName;
	public String description;
	public int id;

	public boolean isComplete() {
		if (type == null || itemName == null || description == null)
			return false;
		return true;
	}
}
