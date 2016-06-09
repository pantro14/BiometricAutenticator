package carvajal.autenticador.android.util.wrappers;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class AutSyncWrapper {
	
	//	Atributos propios del transporte de datos
	private String cedula;
	
	//	Constructor sin parámetros
	public AutSyncWrapper() {
		super();
	}
	
	/**
	 * Constructor con los parametros para instanciar la clase
	 * @param cedula Cedula elector.
	 */
	public AutSyncWrapper(String cedula) {
		this.cedula = cedula;
	}

	/*
	 * Getters y Setters de los parámetros globales
	 */
	
	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
}
