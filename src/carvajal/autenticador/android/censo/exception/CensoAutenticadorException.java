package carvajal.autenticador.android.censo.exception;

public class CensoAutenticadorException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public CensoAutenticadorException() {
		super();
	}
	
	public CensoAutenticadorException(final String message) {
		super(message);
	}
	
	public CensoAutenticadorException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public CensoAutenticadorException(final Throwable cause) {
		super(cause);
	}
}
