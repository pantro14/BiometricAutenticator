package carvajal.autenticador.android.bl.exception;

public class ConfiguracionBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConfiguracionBLException() {
		super();
	}

	public ConfiguracionBLException(final String message) {
		super(message);
	}

	public ConfiguracionBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConfiguracionBLException(final Throwable cause) {
		super(cause);
	}
}
