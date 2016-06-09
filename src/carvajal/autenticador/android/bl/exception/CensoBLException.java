package carvajal.autenticador.android.bl.exception;

public class CensoBLException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CensoBLException() {
		super();
	}
	
	public CensoBLException(final String message) {
		super(message);
	}
	
	public CensoBLException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public CensoBLException(final Throwable cause) {
		super(cause);
	}
}
