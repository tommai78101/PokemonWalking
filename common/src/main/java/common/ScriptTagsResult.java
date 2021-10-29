package common;

import enums.ScriptTags;

/**
 * A wrapper class that holds the ScriptTags enum type and the script content. The script content
 * has the ScriptTags symbol removed.
 * <p>
 * Used in conjunction with {@linkplain ScriptTags#determineType ScriptTags.determineType()}.
 *
 * @author tlee
 */
public class ScriptTagsResult {
	private ScriptTags type;
	private String content;

	public ScriptTagsResult(ScriptTags type, String content) {
		this.type = type;
		this.content = content;
	}

	public ScriptTags getType() {
		return this.type;
	}

	public void setType(ScriptTags type) {
		this.type = type;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
