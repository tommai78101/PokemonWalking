/**
 * 
 */
package constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author tlee
 */
public enum ScriptTag {
	ScriptName("@", "NAME", "Script Name"),
	Comment("/", "COMMENT", "Comment"),
	BeginScript("$", "BEGIN", "Script begins"),
	PathData("^", "PATH", "Scripted Path Data"),
	EndScript("%", "END", "Script ends"),
	Speech("#", "SPEAK", "A speech dialogue"),
	Question("?", "ASK", "A question dialogue"),
	Affirm("+", "AFFIRM", "Affirm with a positive answer"),
	Reject("-", "REJECT", "Reject with a negative answer"),
	Confirm("[", "CONFIRM", "Asking the player to confirm response"),
	Deny("]", "DENY", "Asking the player to deny response"),
	Repeat(";", "LOOP", "Repeat the script"),
	Repeatable(";", "IS_LOOP", "Script can be repeated again"),
	Condition("~", "CONDITION", "Conditions that are to be met to complete the script");

	private final String symbol;
	private final String fullSymbol;
	private final String description;

	private ScriptTag(String sym, String alternate, String description) {
		this.symbol = sym;
		this.fullSymbol = alternate;
		this.description = description;
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
		if (this.startsWithUpperCase(line))
			return true;
		if (line.startsWith(this.symbol))
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
			return (uppercase.trim().equals(this.fullSymbol));
		}
		return false;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPrettyDescription() {
		return this.description + ".";
	}

	public String getSymbolName() {
		return this.fullSymbol;
	}
}
