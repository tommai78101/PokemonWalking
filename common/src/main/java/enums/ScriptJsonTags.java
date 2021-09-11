package enums;

/**
 * Tags which are used when producing script files formatted in JSON. These tags are used as names
 * of the properties that holds ScriptTags data and information.
 *
 * @author tlee
 */
public enum ScriptJsonTags {
	NAME("name"),
	DESCRIPTION("description"),
	USAGE("usage"),

	// JSON array that contains multiple trigger script information.
	DATA("data"),
	COMMENT("comment"),
	AREA_CHECKSUM("areaChecksum");

	private String jsonKey;

	ScriptJsonTags(String value) {
		this.jsonKey = value;
	}

	public String getKey() {
		return this.jsonKey;
	}
}
