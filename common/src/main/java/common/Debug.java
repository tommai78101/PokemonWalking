package common;

import enums.AnsiColors;

public class Debug {
	public static final int MinimumStringLength = 24;
	public static final int TabLength = 8;

	private static String createString(String msg) {
		StackTraceElement element = new Throwable().getStackTrace()[2];
		String className = element.getClassName() + ":" + element.getLineNumber();
		String tabs = "\t";
		int length = className.length();
		while (length < Debug.MinimumStringLength) {
			tabs = tabs.concat("\t");
			length += Debug.TabLength;
		}
		return className + tabs + "- " + msg;
	}

	private static String createExceptionString(Exception e) {
		StackTraceElement element = e.getStackTrace()[0];
		String className = element.getClassName() + ":" + element.getLineNumber();
		String tabs = "\t";
		int length = className.length();
		while (length < Debug.MinimumStringLength) {
			tabs = tabs.concat("\t");
			length += Debug.TabLength;
		}
		return className + tabs + "- " + e.getLocalizedMessage();
	}

	public static void log(String msg) {
		System.out.println(Debug.createString(msg));
	}

	public static void error(String msg) {
		System.err.println(Debug.createString(msg));
	}

	public static void error(String msg, Exception e) {
		Debug.error(msg);
		e.printStackTrace();
	}

	public static void exception(Exception e) {
		System.err.println(Debug.createExceptionString(e));
	}

	public static void printColor(AnsiColors color, String input) {
		System.out.println(color.getAnsiCode() + input + AnsiColors.Clear.getAnsiCode());
	}
}
