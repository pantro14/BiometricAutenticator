package carvajal.autenticador.android.util.exception;

public class AutenticadorGsonConverterException extends Exception {

	
	private static final long serialVersionUID = 1L;

	public AutenticadorGsonConverterException() {
		super();
	}

	public AutenticadorGsonConverterException(String detailMessage) {
		super(detailMessage);
	}

	public AutenticadorGsonConverterException(Throwable throwable) {
		super(throwable);
	}

	public AutenticadorGsonConverterException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

}
