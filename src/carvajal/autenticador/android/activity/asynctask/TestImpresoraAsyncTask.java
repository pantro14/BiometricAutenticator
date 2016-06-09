package carvajal.autenticador.android.activity.asynctask;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import android.content.Context;
import android.os.AsyncTask;

/**
 * La clase as�ncrona TestImpresoraAsyncTask permite ejecutar 
 * la tarea de diagn�stico de la impresora externa
 * centralizaci�n, por fuera del hilo principal de ejecuci�n de la
 * aplicaci�n m�vil.
 * 
 * @author davparpa
 * 
 */
public class TestImpresoraAsyncTask extends AsyncTask<Void, Void, Void> {

	/**
	 *  Contexto de origen
	 */
	public Context context;
	
	/**
	 * 	C�digo que indica que el diagn�stico fue exitoso
	 */
	private final int TEST_IMPRESORA_OK = 1;
	
	/**
	 * 	C�digo que indica que el diagn�stico fall� por temas de WIFI
	 */
	private final int TEST_WIFI_FAIL = 2;
	
	/**
	 * 	C�digo que indica que el diagn�stico fall� por tema del Servicio OWIN
	 */
	private final int TEST_SERVER_FAIL = 3;
	
	/**
	 * Log de la aplicaci�n Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);
	
	private int resultadoTest;
	
	public TestImpresoraAsyncTask(Context context) {
		this.context = context;
		resultadoTest = TEST_SERVER_FAIL;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			// Se crea e instancia el objeto AutenticadorSyncBL
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
					context, context.getResources().getString(
							R.string.metodo_diagnostico_impresora));
			//	Se llama al m�todo en el servidor
			resultadoTest = Integer.parseInt(autSyncBL.testImpresoraSync());
		} catch (AutenticadorSyncBLException e) { 
			// Exepci�n inesperada tipo AutenticadorSyncBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ServerTestAsyncTask:doInBackground:",
							e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		/**
		 * Se verifica el C�digo que indica el resultado de la consulta
		 * del servicio we de impresi�n
		 */
		switch(resultadoTest){
			case TEST_IMPRESORA_OK:{
				//	El diagn�stico de impresi�n fue exitosa
				//	Se actualiza el estado de conexi�n a Conectado
				AutenticacionActivity.cambiarEstadoConectado
				(context);
				//	Se actualiza porcentaje de sincronizaci�n
				AutenticacionActivity.actualizarPorcentajeSincro
				(context);
			}
			break;
			case TEST_WIFI_FAIL:{
				//	El diagn�stico de impresi�n fall� por WIFI
				//	Se actualiza el estado de conexi�n a Desconectado
				AutenticacionActivity.cambiarEstadoDesconectado
				(context);
			}
			break;
			case TEST_SERVER_FAIL:{
				//	El diagn�stico de impresora fall� por Servicio OWIN
				//	Se actualiza el estado de conexi�n a Desconectado
				AutenticacionActivity.cambiarEstadoDesconectado
				(context);
			}
			break;
		}		
	}
	
	

}
