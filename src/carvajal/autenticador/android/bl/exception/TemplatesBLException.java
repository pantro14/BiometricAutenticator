package carvajal.autenticador.android.bl.exception;

public class TemplatesBLException extends Exception {
	private static final long serialVersionUID = 1L;

	public TemplatesBLException() {
		super();
	}

	public TemplatesBLException(final String message) {
		super(message);
	}

	public TemplatesBLException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TemplatesBLException(final Throwable cause) {
		super(cause);
	}
}
