package carvajal.autenticador.android.util.wrappers;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class NovResponseSyncWrapper {
	
	//	Atributos propios del transporte de datos
	private int respuesta;

	//	Constructor sin parámetros	
	public NovResponseSyncWrapper() {
		super();
	}

	/**
	 * Constructor con los parametros para instanciar la clase
	 * @param respuesta Respuesta obtenida de la trasacción.
	 */
	public NovResponseSyncWrapper(int respuesta) {
		super();
		this.respuesta = respuesta;
	}
	
	/*
	 * Getters y Setters de los parámetros globales
	 */

	public int getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}
	
	
}
