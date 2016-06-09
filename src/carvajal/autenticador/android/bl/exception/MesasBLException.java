package carvajal.autenticador.android.bl.exception;

public class MesasBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public MesasBLException() {
		super();
	}

	public MesasBLException(final String message) {
		super(message);
	}

	public MesasBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MesasBLException(final Throwable cause) {
		super(cause);
	}
}
