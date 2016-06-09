package carvajal.autenticador.android.activity.asynctask;

import org.apache.log4j.Logger;

import android.content.Context;
import android.os.AsyncTask;
import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.DiagnosticoActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.wrappers.TestWrapper;

/**
 * La clase asíncrona DiagnosticServerTestAsyncTask permite ejecutar la tarea de
 * probar la conexión con el servidor central y con la base de datos de
 * centralización, por fuera del hilo principal de ejecución de la aplicación
 * móvil.
 * 
 * @author grasotos
 * 
 */
public class DiagnosticServerTestAsyncTask extends AsyncTask<Void, Void, Void> {

	/**
	 * Actividad padre
	 * 
	 * @author grasotos
	 */
	public Context context;

	/**
	 * Log de la aplicación Android Log4j
	 * 
	 * @author grasotos
	 */
	private final static Logger log4jDroid = Logger.getLogger(DiagnosticServerTestAsyncTask.class);

	/**
	 * wrapper con la respuesta del servidor.
	 * 
	 * @author grasotos
	 */
	TestWrapper testWrapper = null;

	/**
	 * Nombre del servidor para mostrar mensajes informativos.
	 * 
	 * @author grasotos
	 */
	String nombreServidor;

	/**
	 * Constante que se obtiene cuando existe respuesta del servidor y de la
	 * base de datos.
	 * 
	 * @author grasotos
	 */
	private final int TEST_OK = 1;

	/**
	 * Constante que se obtiene cuando existe respuesta del servidor pero no de
	 * la base de datos.
	 * 
	 * @author grasotos
	 */
	private final int TEST_ERROR = 0;

	/**
	 * Constructor de al tarea asincrona.
	 * 
	 * @param context
	 *            contexto de la aplicación.
	 * @param nombreServidor
	 *            nombre del servidor con el cual se está intentando conectar.
	 * 
	 * @author grasotos
	 */
	public DiagnosticServerTestAsyncTask(Context context, String nombreServidor) {
		this.context = context;
		this.nombreServidor = nombreServidor;

	}

	/**
	 * Se crea e instancia el objeto AutenticadorSyncBL y se llama el método de
	 * prueba de test.
	 * 
	 * @author grasotos
	 * 
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(context, context.getResources().getString(R.string.metodo_get_test));
			testWrapper = autSyncBL.obtenerTestMetodo();
		} catch (AutenticadorSyncBLException e) {

			log4jDroid.error("AutenticadorAndroidProject:ServerTestAsyncTask:doInBackground:", e);
		}
		return null;
	}

	/**
	 * Al finalizar el doInBackground se ejecuta este método. Captura la
	 * respuesta del servidor y se muestran los resultados en la atividad
	 * principal de diagnostico.
	 * 
	 * testWrapper=null, cuando no hay respuesta del servicio. testWrapper =0,
	 * cuando el servicio responde pero la base de datos no. testWrapper =1,
	 * cuando el servicio y la base de datos responden.
	 * 
	 * @author grasotos
	 */
	@Override
	protected void onPostExecute(Void result) {

		if (testWrapper == null) {

			AutenticacionActivity.cambiarEstadoDesconectado(context);
			String mensajeM48 = context.getString(R.string.M48, nombreServidor);
			DiagnosticoActivity.mensajeWifi(mensajeM48, context.getResources().getDrawable(R.drawable.ic_error),
					context.getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(context.getString(R.string.red_inalambrica), mensajeM48);

		} else {

			int test = testWrapper.getTest();

			if (test == TEST_OK) {

				AutenticacionActivity.cambiarEstadoConectado(context);
				AutenticacionActivity.actualizarPorcentajeSincro(context);
				DiagnosticoActivity.mensajeWifi("", context.getResources().getDrawable(R.drawable.ic_correcto),
						context.getResources().getDrawable(R.drawable.borde_redondeado_verde));
				Util.logsDiagnostico(context.getString(R.string.red_inalambrica), context.getString(R.string.dispositivo_ok));

			} else if (test == TEST_ERROR) {
				AutenticacionActivity.cambiarEstadoDesconectado(context);
				String mensajeM48 = context.getString(R.string.M48, nombreServidor);
				DiagnosticoActivity.mensajeWifi(context.getResources().getString(R.string.M48, nombreServidor), context.getResources().getDrawable(R.drawable.ic_error), context
						.getResources().getDrawable(R.drawable.borde_redondeado_rojo));
				Util.logsDiagnostico(context.getString(R.string.red_inalambrica), mensajeM48);
			}
		}
	}

}
