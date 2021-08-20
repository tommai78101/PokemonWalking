package enums;

/**
 * Entity class objects include NPC, Player, and monsters. Thus, it is fitting for the Entity
 * objects to include a GenderType.
 */
public enum GenderType {
	// @formatter:off
	Nondetermined((byte) 0x7F),
	Male((byte) 0x1),
	Female((byte) 0xFF);
	// @formatter:on

	private byte typeId;

	GenderType(byte value) {
		this.typeId = value;
	}

	public byte getByte() {
		return this.typeId;
	}

	public static GenderType determineGender(byte value) {
		if (value == Male.typeId)
			return Male;
		if (value == Female.typeId)
			return Female;
		return Nondetermined;
	}
}
