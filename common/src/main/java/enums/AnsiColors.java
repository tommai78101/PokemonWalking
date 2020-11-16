/**
 * 
 */
package enums;

/**
 * @see https://en.wikipedia.org/wiki/ANSI_escape_code#Colors
 * @author tlee
 */
public enum AnsiColors {
	Clear("\033[0m"),

	Black("\033[30m"),
	Red("\033[31m"),
	Green("\033[32m"),
	Yellow("\033[33m"),
	Blue("\033[34m"),
	Magenta("\033[35m"),
	Cyan("\033[36m"),
	White("\033[37m"),

	BrightBlack("\033[90m"),
	BrightRed("\033[91m"),
	BrightGreen("\033[92m"),
	BrightYellow("\033[93m"),
	BrightBlue("\033[94m"),
	BrightMagenta("\033[95m"),
	BrightCyan("\033[96m"),
	BrightWhite("\033[97m");

	private String ansiCode;

	private AnsiColors(String tag) {
		this.ansiCode = tag;
	}

	public String getAnsiCode() {
		return this.ansiCode;
	}
}