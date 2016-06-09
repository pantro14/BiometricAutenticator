package carvajal.autenticador.android.activity.asynctask;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import android.content.Context;
import android.os.AsyncTask;

/**
 * La clase asíncrona TestImpresoraAsyncTask permite ejecutar 
 * la tarea de diagnóstico de la impresora externa
 * centralización, por fuera del hilo principal de ejecución de la
 * aplicación móvil.
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
	 * 	Código que indica que el diagnóstico fue exitoso
	 */
	private final int TEST_IMPRESORA_OK = 1;
	
	/**
	 * 	Código que indica que el diagnóstico falló por temas de WIFI
	 */
	private final int TEST_WIFI_FAIL = 2;
	
	/**
	 * 	Código que indica que el diagnóstico falló por tema del Servicio OWIN
	 */
	private final int TEST_SERVER_FAIL = 3;
	
	/**
	 * Log de la aplicación Android Log4j
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
			//	Se llama al método en el servidor
			resultadoTest = Integer.parseInt(autSyncBL.testImpresoraSync());
		} catch (AutenticadorSyncBLException e) { 
			// Exepción inesperada tipo AutenticadorSyncBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ServerTestAsyncTask:doInBackground:",
							e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		/**
		 * Se verifica el Código que indica el resultado de la consulta
		 * del servicio we de impresión
		 */
		switch(resultadoTest){
			case TEST_IMPRESORA_OK:{
				//	El diagnóstico de impresión fue exitosa
				//	Se actualiza el estado de conexión a Conectado
				AutenticacionActivity.cambiarEstadoConectado
				(context);
				//	Se actualiza porcentaje de sincronización
				AutenticacionActivity.actualizarPorcentajeSincro
				(context);
			}
			break;
			case TEST_WIFI_FAIL:{
				//	El diagnóstico de impresión falló por WIFI
				//	Se actualiza el estado de conexión a Desconectado
				AutenticacionActivity.cambiarEstadoDesconectado
				(context);
			}
			break;
			case TEST_SERVER_FAIL:{
				//	El diagnóstico de impresora falló por Servicio OWIN
				//	Se actualiza el estado de conexión a Desconectado
				AutenticacionActivity.cambiarEstadoDesconectado
				(context);
			}
			break;
		}		
	}
	
	

}
