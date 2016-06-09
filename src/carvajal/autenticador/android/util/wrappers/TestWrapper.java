package carvajal.autenticador.android.util.wrappers;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class TestWrapper {

	//	Atributos propios del transporte de datos
	private int test;
	
	//	Constructor sin parámetros	
	public TestWrapper() {
		super();
	}
	
	/**
	 * Constructor con los parametros para instanciar la clase
	 * @param test Valor de examinación de conexión a la base de datos.
	 */
	public TestWrapper(int test) {
		this.test = test;
	}
	
	/*
	 * Getters y Setters de los parámetros globales
	 */
	
	public int getTest() {
		return test;
	}
	
	public void setTest(int test) {
		this.test = test;
	}
	
	

}
