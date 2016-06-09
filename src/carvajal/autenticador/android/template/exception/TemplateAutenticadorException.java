package carvajal.autenticador.android.template.exception;

public class TemplateAutenticadorException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public TemplateAutenticadorException() {
		super();
	}
	
	public TemplateAutenticadorException(final String message) {
		super(message);
	}
	
	public TemplateAutenticadorException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public TemplateAutenticadorException(final Throwable cause) {
		super(cause);
	}
}
