package carvajal.autenticador.android.bl.exception;

public class ColegiosElectoralesBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public ColegiosElectoralesBLException() {
		super();
	}

	public ColegiosElectoralesBLException(final String message) {
		super(message);
	}

	public ColegiosElectoralesBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ColegiosElectoralesBLException(final Throwable cause) {
		super(cause);
	}
}
