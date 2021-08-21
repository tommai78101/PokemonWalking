/**
 *
 */
package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.StringUtils;

/**
 * Please read the documentation, in the Trigger section [PWD10] for more information on what the
 * script tags mean.
 *
 * @author tlee
 */
public enum ScriptTags {
	/**
	 * @
	 */
	ScriptName("@", "NAME", "Script name"),

	/**
	 * &
	 */
	Checksum("&", "CHKSUM", "Script checksum"),

	/**
	 * /
	 */
	Comment("/", "REM", "Comment"),

	/**
	 * $
	 */
	BeginScript("$", "BEGIN", "Trigger Script begins"),

	/**
	 * !
	 */
	NpcScript("!", "NPC", "NPC Script begins"),

	/**
	 * ^
	 */
	PathData("^", "PATH", "Scripted path data"),

	/**
	 * %
	 */
	EndScript("%", "END", "Script ends"),

	/**
	 * #
	 */
	Speech("#", "SPEAK", "A normal speech dialogue"),

	/**
	 * ?
	 */
	Question("?", "ASK", "A question dialogue asking for the player's response to YES or NO.", "A single question must be followed by an Affirmative and a Negative Dialogue."),

	/**
	 * +
	 */
	Affirm("+", "AFFIRM", "Affirm a question dialogue with a positive answer. If a question dialogue has been asked, and the player reponded to YES, this and similar consecutive dialogues will be shown."),

	/**
	 * -
	 */
	Reject("-", "REJECT", "Reject a question dialogue with a negative answer. If a question dialogue has been asked, and the player reponded to NO, this and similar consecutive dialogues will be shown."),

	/**
	 * [
	 */
	Confirm("[", "CONFIRM", "Asking the player to confirm an option. If a confirm dialogue has been selected, this and similar consecutive dialogues will be shown."),

	/**
	 * ]
	 */
	Cancel("]", "DENY", "Asking the player to deny or cancel an option. If a denial dialogue has been selected, this and similar consecutive dialogues will be shown."),

	/**
	 * ;
	 */
	Repeat(";", "LOOP", "Repeat the script"),

	/**
	 * |
	 */
	Counter("|", "COUNTER", "Script can only be repeated for a limited time"),

	/**
	 * ~
	 */
	Condition("~", "CONDITION", "Conditions that are to be met to complete the script");

	private final String symbol;
	private final String uppercaseSymbolName;
	private final String capitalizedSymbolName;
	private final String lowercaseSymbolName;
	private final String description;
	private final String note;

	ScriptTags(String sym, String alternate, String description) {
		this(sym, alternate, description, null);
	}

	ScriptTags(String sym, String alternate, String description, String note) {
		this.symbol = sym;
		this.uppercaseSymbolName = alternate.toUpperCase();
		this.lowercaseSymbolName = alternate.toLowerCase();
		this.capitalizedSymbolName = StringUtils.capitalize(this.lowercaseSymbolName);
		this.description = description;
		this.note = (note != null && !note.isBlank()) ? note : "";
	}

	/**
	 * Checks if the line starts with either the symbol representation, or the tag name.
	 *
	 * @param line
	 * @return True if either the symbol or the tag name matches. False, if otherwise.
	 */
	public boolean beginsAt(String line) {
		if (line == null || line.isEmpty() || line.isBlank())
			return false;
		if (this.startsWithUpperCase(line) || line.startsWith(this.symbol))
			return true;
		String upper = line.toUpperCase().trim();
		String lower = line.toLowerCase().trim();
		if (upper.startsWith(this.uppercaseSymbolName) || lower.startsWith(this.lowercaseSymbolName))
			return true;
		return line.regionMatches(true, 0, this.name(), 0, this.name().length());
	}

	/**
	 * Replaces the first occurrence of the tag name with the equivalent symbol representation.
	 * <p>
	 * Otherwise, if the line already has the symbol representation, then it does nothing.
	 *
	 * @param line
	 * @return The replaced line.
	 */
	public String replace(String line) {
		if (line.startsWith(this.symbol))
			return line;
		return line.replaceFirst(Pattern.quote(this.name()), Matcher.quoteReplacement(this.symbol));
	}

	public boolean startsWithUpperCase(String line) {
		String[] tokens = line.toUpperCase().split(":=");
		if (tokens.length > 0) {
			String uppercase = tokens[0];
			return (uppercase.trim().equals(this.uppercaseSymbolName));
		}
		return false;
	}

	/**
	 * Removes the script tag or script symbol from the given line, and returns the result.
	 *
	 * @param line
	 *            The line that contains the script tag or symbol.
	 * @return The resulting string value after the script tag or symbol has been removed.
	 */
	public String removeScriptTag(String line) {
		if (line.startsWith(this.symbol))
			return line.substring(this.symbol.length());
		else if (line.toUpperCase().startsWith(this.uppercaseSymbolName) || line.toLowerCase().startsWith(this.lowercaseSymbolName)) {
			// We ignore anything that comes before the script tag. We're not doing anything complicated here.
			int endOfIndex = line.indexOf(this.uppercaseSymbolName) + this.uppercaseSymbolName.length();
			return line.substring(endOfIndex).trim();
		}
		// Cannot remove any script tags or symbols.
		return line;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPrettyDescription() {
		return this.description + ".";
	}

	public String getSymbolName() {
		return this.uppercaseSymbolName;
	}

	public String getLowercaseSymbolName() {
		return this.lowercaseSymbolName;
	}

	public String getCapitalizedSymbolName() {
		return this.capitalizedSymbolName;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public String getAdditionalNote() {
		return this.note;
	}

	// ------------------------------------------------------------------------
	// Static methods

	public static ScriptTags parse(String actionCommand) {
		ScriptTags[] values = ScriptTags.values();
		for (ScriptTags tag : values) {
			if (tag.getSymbolName().equals(actionCommand))
				return tag;
		}
		throw new IllegalArgumentException("Unknown script tag action command symbol.");
	}
}
