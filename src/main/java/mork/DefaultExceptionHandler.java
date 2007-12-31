package mork;

/**
 * The default exception handler simply rethrows the exception
 * 
 * @author mhaller
 */
class DefaultExceptionHandler implements ExceptionHandler {

	public void handle(Throwable t) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		} else {
			throw new RuntimeException(t);
		}
	}

}
