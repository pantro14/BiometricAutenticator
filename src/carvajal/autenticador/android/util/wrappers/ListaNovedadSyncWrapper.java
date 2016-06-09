package carvajal.autenticador.android.util.wrappers;

import java.util.List;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class ListaNovedadSyncWrapper {
	
	//	Atributos propios del transporte de datos
	private List<NovSyncWrapper> listanovedades;
	
	//	Constructor sin parámetros	
	public ListaNovedadSyncWrapper() {
		super();
	}

	/**
	 * 
	 * Constructor con los parametros para instanciar la clase
	 * @param listanovedades Lista de novedades.
	 */
	public ListaNovedadSyncWrapper(List<NovSyncWrapper> listanovedades) {
		super();
		this.listanovedades = listanovedades;
	}
	
	/*
	 * Getters y Setters de los parámetros globales
	 */

	public List<NovSyncWrapper> getListanovedades() {
		return listanovedades;
	}

	public void setListanovedades(List<NovSyncWrapper> listanovedades) {
		this.listanovedades = listanovedades;
	}
}
