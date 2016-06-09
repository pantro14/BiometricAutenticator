package carvajal.autenticador.android.util.file.exception;

public class AutenticadorFileReaderException  extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public AutenticadorFileReaderException() {
		super();
	}
	
	public AutenticadorFileReaderException(final String message) {
		super(message);
	}
	
	public AutenticadorFileReaderException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public AutenticadorFileReaderException(final Throwable cause) {
		super(cause);
	}
}
