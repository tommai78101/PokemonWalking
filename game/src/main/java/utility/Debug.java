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
}
