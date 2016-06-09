package carvajal.autenticador.android.bl.exception;

public class DataBaseException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataBaseException() {
		super();
	}

	public DataBaseException(final String message) {
		super(message);
	}

	public DataBaseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DataBaseException(final Throwable cause) {
		super(cause);
	}
}
