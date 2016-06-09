/**
 * 
 */
package carvajal.autenticador.android.framework.morphosmart.exception;

/**
 * Excepción genérica para errores con el SDK del lector biométrico.
 * 
 * @author grasotos
 * @date 24-Feb-2015
 */
public class ConexionHuelleroException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de excepcion de ConexionHuelleroException
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public ConexionHuelleroException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor de excepcion de ConexionHuelleroException
	 * 
	 * @param detailMessage
	 *            mensaje detallado de la excepcion
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public ConexionHuelleroException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor de excepcion de ConexionHuelleroException
	 * 
	 * @param throwable
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public ConexionHuelleroException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * * Constructor de excepcion de ConexionHuelleroException
	 * 
	 * @param detailMessage
	 * @param throwable
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public ConexionHuelleroException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}
}
