package common;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import enums.AnsiColors;

/**
 * Debug utility class with ANSI colors support.
 *
 * @author tlee
 */
public class Debug {
	private static final int MinimumStringLength = 17;

	private static final int TabLength = 8;

	protected static final int MaxDuplicateLogOccurrences = 1;

	private static Map<String, NotYetImplemented> notYetImplementedTracker = new HashMap<>();

	static class NotYetImplemented {
		private int occurrences = 0;

		private final String location;

		public NotYetImplemented(String loc) {
			this.location = loc;
		}

		public boolean grab(String loc) {
			if (this.location.equals(loc) && !this.hasReachedLimit()) {
				this.increment();
				return true;
			}
			return false;
		}

		public boolean hasReachedLimit() {
			return this.occurrences >= Debug.MaxDuplicateLogOccurrences;
		}

		public void increment() {
			this.occurrences++;
		}
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
	 * Prints out information in green text.
	 *
	 * @param msg
	 */
	public static void info(String msg) {
		Debug.printColor(AnsiColors.Green, Debug.createString(msg));
	}

	public static void notYetImplemented() {
		Debug.toDo("Not yet implemented", 4);
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

	public static void toDo(String msg) {
		Debug.toDo(msg, 5);
	}

	/**
	 * Nifty way of marking where in the code, have we not yet implemented any Java code.
	 */
	public static void toDo(String msg, int stackTraceIndex) {
		String key = Debug.createString("", stackTraceIndex);
		NotYetImplemented nyi = Debug.notYetImplementedTracker.getOrDefault(key, new NotYetImplemented(key));
		if (nyi.grab(key)) {
			int index = 3;
			do {
				msg = Debug.createString(msg, index);
				index--;
			}
			while (msg == null || (msg.contains("$") && index > 0));
			Debug.printColor(AnsiColors.BrightMagenta, msg);
			Debug.notYetImplementedTracker.put(key, nyi);
		}
	}

	/**
	 * Logs information in yellow text.
	 *
	 * @param msg
	 */
	public static void warn(String msg) {
		Debug.printColor(AnsiColors.Yellow, Debug.createString(msg));
	}

	/**
	 * Builds the log messages in a readable format for developers.
	 *
	 * @param msg
	 * @return
	 */
	private static String createString(String msg) {
		return Debug.createString(msg, 3);
	}

	/**
	 * Builds the log messages in a readable format for developers.
	 *
	 * @param msg
	 * @param index
	 *            Specify which stack trace element to choose from.
	 * @return
	 */
	private static String createString(String msg, int index) {
		StackTraceElement[] elements = new Throwable().getStackTrace();
		StackTraceElement element = elements[index];
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
	 * Handles wrapping the input string with ANSI color tags.
	 *
	 * @param color
	 * @param input
	 */
	private static void printColor(AnsiColors color, String input) {
		System.out.println(color.getAnsiCode() + input + AnsiColors.Clear.getAnsiCode());
	}

	/**
	 * Builds a simplified exception message thrown out by the actual class object containing the bug.
	 * Does not display anything related with Java SDK internal classes.
	 *
	 * @param e
	 * @return
	 */
	protected static String createExceptionString(Exception e) {
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
