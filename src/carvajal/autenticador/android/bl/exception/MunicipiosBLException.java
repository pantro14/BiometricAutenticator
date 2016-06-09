package carvajal.autenticador.android.bl.exception;

public class MunicipiosBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public MunicipiosBLException() {
		super();
	}

	public MunicipiosBLException(final String message) {
		super(message);
	}

	public MunicipiosBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MunicipiosBLException(final Throwable cause) {
		super(cause);
	}
}
