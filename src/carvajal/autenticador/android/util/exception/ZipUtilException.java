package carvajal.autenticador.android.util.exception;

public class ZipUtilException extends Exception {
	private static final long serialVersionUID = 1L;

	public ZipUtilException() {
		super();
	}

	public ZipUtilException(final String message) {
		super(message);
	}

	public ZipUtilException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ZipUtilException(final Throwable cause) {
		super(cause);
	}
}
