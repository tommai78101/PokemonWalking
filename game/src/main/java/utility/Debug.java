package utility;

public class Debug {
	public static final int MinimumStringLength = 17;

	private static String createString(String msg) {
		StackTraceElement element = new Throwable().getStackTrace()[2];
		String className = element.getClassName() + ":" + element.getLineNumber();
		String tabs = "\t";
		int length = className.length();
		while (length < Debug.MinimumStringLength) {
			tabs = tabs.concat("\t");
			length += 4;
		}
		return className + tabs + "- " + msg;
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
}
