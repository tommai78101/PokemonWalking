package utility;

public class Debug {
	public static void log(String msg) {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		System.out.println(element.getClassName() + ":" + element.getLineNumber() + "\t- " + msg);
	}

	public static void error(String msg) {
		StackTraceElement element = new Throwable().getStackTrace()[1];
		System.err.println(element.getClassName() + ":" + element.getLineNumber() + "\t- " + msg);
	}

	public static void error(String msg, Exception e) {
		StackTraceElement element = e.getStackTrace()[1];
		Debug.error(msg);
		Debug.error(element.getClassName() + ":" + element.getLineNumber() + "\t- " + e.getLocalizedMessage());
	}
}
