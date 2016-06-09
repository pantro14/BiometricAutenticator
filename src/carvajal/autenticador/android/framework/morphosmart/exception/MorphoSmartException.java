/**
 * 
 */
package carvajal.autenticador.android.framework.morphosmart.exception;

/**
 * Excepción genérica para errores con lector biométrico.
 * 
 * @author grasotos
 * @date 24-Feb-2015
 */
public class MorphoSmartException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de excepcion de MorphoSmartException
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public MorphoSmartException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor de excepcion de MorphoSmartException
	 * 
	 * @param detailMessage
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public MorphoSmartException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor de excepcion de MorphoSmartException
	 * 
	 * @param throwable
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public MorphoSmartException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor de excepcion de MorphoSmartException
	 * 
	 * @param detailMessage
	 * @param throwable
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public MorphoSmartException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}
}
