package carvajal.autenticador.android.greendao.exception;

public class AutenticadorGreenDaoException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public AutenticadorGreenDaoException() {
		super();
	}
	
	public AutenticadorGreenDaoException(final String message) {
		super(message);
	}
	
	public AutenticadorGreenDaoException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public AutenticadorGreenDaoException(final Throwable cause) {
		super(cause);
	}
}
