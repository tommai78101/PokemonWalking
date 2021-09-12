package enums;

/**
 * Tags which are used when producing script files formatted in JSON. These tags are used as names
 * of the properties that holds ScriptTags data and information.
 *
 * @author tlee
 */
public enum ScriptJsonTags {
	// Script info
	TRIGGER_ID("id"),
	CHECKSUM("checksum"),
	NAME("name"),
	DESCRIPTION("description"),
	USAGE("usage"),
	COMMENT("comment"),

	// Script trigger type
	TRIGGER_TYPE("triggerType"),
	TRIGGER_TYPE_SCENE("scene"),
	TRIGGER_TYPE_NPC("npc"),

	// JSON array that contains multiple trigger script information.
	DATA("data"),
	ORDER("order"),

	// ScriptTags type and content, for ScriptTagsResult
	TYPE("type"),
	CONTENT("content"),

	// Trigger names
	SEQUENCE("sequence");

	private String jsonKey;

	ScriptJsonTags(String value) {
		this.jsonKey = value;
	}

	public String getKey() {
		return this.jsonKey;
	}
}
