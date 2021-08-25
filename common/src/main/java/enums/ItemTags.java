package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ItemTags {
	Type("%", "TYPE", "Item type"),
	Name("#", "NAME", "Name of the item"),
	Description("@", "DESCRIPTION", "Item description"),
	Category("^", "CATEGORY", "Item category"),
	Delimiter(";", "END", "End of the item information"),

	// Item options
	SetCommand("$", "DESCRIPTION", "Item description"),
	UseCommand("!", "CATEGORY", "Item category"),
	TossCommand("&", "END", "End of the item information");

	private final String symbol;
	private final String uppercaseSymbolName;
	private final String lowercaseSymbolName;
	private final String description;

	private ItemTags(String sym, String alternate, String description) {
		this.symbol = sym;
		this.uppercaseSymbolName = alternate.toUpperCase();
		this.lowercaseSymbolName = alternate.toLowerCase();
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
	public String removeItemTag(String line) {
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
}
