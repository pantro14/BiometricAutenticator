package carvajal.autenticador.android.bl.exception;

public class AutenticacionBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public AutenticacionBLException() {
		super();
	}

	public AutenticacionBLException(final String message) {
		super(message);
	}

	public AutenticacionBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AutenticacionBLException(final Throwable cause) {
		super(cause);
	}
}
