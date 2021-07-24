package common;

import javax.swing.JOptionPane;

import enums.AnsiColors;

/**
 * Debug utility class with ANSI colors support.
 * 
 * @author tlee
 */
public class Debug {
	/**
	 * Logs information in yellow text.
	 * 
	 * @param msg
	 */
	public static void log(String msg) {
		Debug.printColor(AnsiColors.Yellow, Debug.createString(msg));
	}

	/**
	 * Prints out information in green text.
	 * 
	 * @param msg
	 */
	public static void info(String msg) {
		Debug.printColor(AnsiColors.Green, Debug.createString(msg));
	}

	/**
	 * Displays error in bright red text.
	 * 
	 * @param msg
	 */
	public static void error(String msg) {
		Debug.printColor(AnsiColors.BrightRed, Debug.createString(msg));
	}

	/**
	 * Displays error in bright red text, and prints out the exception in dark red text.
	 * 
	 * @param msg
	 * @param e
	 */
	public static void error(String msg, Exception e) {
		Debug.printColor(AnsiColors.BrightRed, Debug.createString(msg));
		Debug.printColor(AnsiColors.Red, Debug.createExceptionString(e));
	}

	/**
	 * Displays error exception in dark red text.
	 * 
	 * @param e
	 */
	public static void exception(Exception e) {
		Debug.printColor(AnsiColors.Red, Debug.createExceptionString(e));
	}

	/**
	 * Displays an alert message box with the error message and line number.
	 * 
	 * @param e
	 */
	public static void showExceptionCause(Exception e) {
		Debug.showExceptionCause("", e);
	}

	/**
	 * Displays an alert message box with a customized message, the error message, and the line number.
	 * 
	 * @param msg
	 * @param e
	 */
	public static void showExceptionCause(String msg, Exception e) {
		if (msg == null) {
			StackTraceElement threadStack = Thread.currentThread().getStackTrace()[2];
			String threadCause = threadStack.getClassName() + ":" + threadStack.getLineNumber();
			JOptionPane.showMessageDialog(null, "Debug.showExceptionCause encountered null message.\n" + threadCause);
			return;
		}
		StackTraceElement element = e.getStackTrace()[0];
		String cause = "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
		if (!msg.isBlank()) {
			msg += "\n";
		}
		msg += e.getMessage() + " " + cause;
		JOptionPane.showMessageDialog(null, msg);
	}

	// -----------------------------------------------------------------
	// Private static methods and class member fields.

	private static final int MinimumStringLength = 17;
	private static final int TabLength = 8;

	/**
	 * Handles wrapping the input string with ANSI color tags.
	 * 
	 * @param color
	 * @param input
	 */
	private static void printColor(AnsiColors color, String input) {
		System.out.println(color.getAnsiCode() + input + AnsiColors.Clear.getAnsiCode());
	}

	/**
	 * Builds the log messages in a readable format for developers.
	 * 
	 * @param msg
	 * @return
	 */
	private static String createString(String msg) {
		StackTraceElement[] elements = new Throwable().getStackTrace();
		StackTraceElement element = elements[2];
		String className = element.getClassName() + ":" + element.getLineNumber();
		String tabs = "\t";
		int length = className.length();
		while (length < Debug.MinimumStringLength) {
			tabs = tabs.concat("\t");
			length += Debug.TabLength;
		}
		return className + tabs + "- " + msg;
	}

	/**
	 * Builds a simplified exception message thrown out by the actual class object containing the bug.
	 * Does not display anything related with Java SDK internal classes.
	 * 
	 * @param e
	 * @return
	 */
	private static String createExceptionString(Exception e) {
		StackTraceElement[] elements = e.getStackTrace();
		StackTraceElement element = elements[elements.length - 1];
		String className = element.getClassName() + ":" + element.getLineNumber();
		String tabs = "\t";
		int length = className.length();
		while (length < Debug.MinimumStringLength) {
			tabs = tabs.concat("\t");
			length += Debug.TabLength;
		}
		return className + tabs + "- " + e.getLocalizedMessage();
	}
}
