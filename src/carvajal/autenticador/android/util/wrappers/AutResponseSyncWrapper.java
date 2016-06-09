package carvajal.autenticador.android.util.wrappers;

import java.util.List;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class AutResponseSyncWrapper {

	//	Atributos propios del transporte de datos
	private String cedula;
	private int sync;
	private List<NovSyncWrapper> listaNovedades;
	
	//	Constructor sin parámetros
	public AutResponseSyncWrapper() {
		super();
	}


	/**
	 * Constructor con los parametros para instanciar la clase
	 * 
	 * @param cedula Cédula del elector
	 * @param sync Valor de sincronización con el servidor central.
	 * @param listaNovedades Lista de novedades del elector.
	 */
	public AutResponseSyncWrapper(String cedula, int sync,
			List<NovSyncWrapper> listaNovedades) {
		super();
		this.cedula = cedula;
		this.sync = sync;
		this.listaNovedades = listaNovedades;
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

	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}

	public List<NovSyncWrapper> getListaNovedades() {
		return listaNovedades;
	}

	public void setListaNovedades(List<NovSyncWrapper> listaNovedades) {
		this.listaNovedades = listaNovedades;
	}
}
