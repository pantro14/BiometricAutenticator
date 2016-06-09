package carvajal.autenticador.android.log4j.exception;

public class AndroidLog4jException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public AndroidLog4jException() {
		super();
	}
	
	public AndroidLog4jException(final String message) {
		super(message);
	}
	
	public AndroidLog4jException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public AndroidLog4jException(final Throwable cause) {
		super(cause);
	}
}
