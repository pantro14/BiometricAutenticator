package carvajal.autenticador.android.bl.exception;

public class ParametrosBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public ParametrosBLException() {
		super();
	}

	public ParametrosBLException(final String message) {
		super(message);
	}

	public ParametrosBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ParametrosBLException(final Throwable cause) {
		super(cause);
	}
}
