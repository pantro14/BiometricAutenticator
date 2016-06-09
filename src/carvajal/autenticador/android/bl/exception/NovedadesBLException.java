package carvajal.autenticador.android.bl.exception;

public class NovedadesBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public NovedadesBLException() {
		super();
	}

	public NovedadesBLException(final String message) {
		super(message);
	}

	public NovedadesBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NovedadesBLException(final Throwable cause) {
		super(cause);
	}
}
