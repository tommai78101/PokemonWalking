package enums;

public enum ItemCategories {
	// @formatter:off
	POTIONS(0x00, "POTIONS", "Potions"),
	KEYITEMS(0x01, "KEYITEMS", "KeyItems"),
	POKEBALLS(0x02, "POKEBALLS", "Pokéballs"),
	TM_HM(0x03, "TM HM", "TMs_HMs"),

	// This is for modded items.
	ALL(0x04, "ALL", "Unsorted");
	// @formatter:on

	private byte categoryByte;
	private int id;
	private String keyString;
	private String chunkName;

	ItemCategories(int value, String chunkName, String key) {
		this.id = value;
		this.categoryByte = (byte) value;
		this.keyString = key;
		this.chunkName = chunkName;
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
	public static ItemCategories getWrapped(int value) {
		ItemCategories[] categories = ItemCategories.values();
		if (value < 0)
			value = (categories.length - 1);
		if (value > categories.length - 1)
			value = 0;
		for (ItemCategories c : categories) {
			if (c.id == value)
				return categories[value];
		}
		return categories[0];
	}

	public int getID() {
		return this.id;
	}

	public byte getByte() {
		return this.categoryByte;
	}

	public String getKey() {
		return this.keyString;
	}

	public boolean chunkEquals(String value) {
		return this.chunkName.equals(value);
	}

	/**
	 * Returns the correct Category enum value. Otherwise, returns null if no Category matches the
	 * value.
	 *
	 * @param value
	 * @return
	 */
	public static ItemCategories convert(byte value) {
		if (value == POTIONS.getByte())
			return POTIONS;
		if (value == KEYITEMS.getByte())
			return KEYITEMS;
		if (value == POKEBALLS.getByte())
			return POKEBALLS;
		if (value == TM_HM.getByte())
			return TM_HM;
		if (value == ALL.getByte())
			return ALL;
		return null;
	}
}