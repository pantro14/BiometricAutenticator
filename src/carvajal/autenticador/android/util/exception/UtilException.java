package carvajal.autenticador.android.util.exception;

public class UtilException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UtilException() {
		super();
	}
	
	public UtilException(final String message) {
		super(message);
	}
	
	public UtilException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public UtilException(final Throwable cause) {
		super(cause);
	}
}
