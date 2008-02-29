package mork;

/**
 * Decides whether exceptions should be thrown immediately or whether they are
 * logged only.
 * 
 * @author mhaller
 */
final class ExceptionManager {

	private static ExceptionHandler exceptionHandler  = new DefaultExceptionHandler();

	public static String createString(String value, Throwable t) {
		exceptionHandler.handle(t);
		return value;
	}

	public static void setExceptionHandler(ExceptionHandler exceptionHandler) {
		ExceptionManager.exceptionHandler = exceptionHandler;
	}
}
