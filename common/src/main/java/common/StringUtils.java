package common;

public class StringUtils {
	public static String capitalize(String value) {
		if (value == null)
			return null;
		if (value.isBlank())
			return "";
		if (value.length() == 1)
			return value.toUpperCase();
		String firstCharacter = value.substring(0, 1);
		return firstCharacter.toUpperCase().concat(value.substring(1));
	}
}
