package carvajal.autenticador.android.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import android.os.AsyncTask;

/**
 * Implementación de lo Métodos utilitarios para la verificación de
 * conectividad.
 * 
 * @author grasotos
 */
public class AsyncTaskUtilities {

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(AsyncTaskUtilities.class);

	/**
	 * Implementación del Método que verifica la conexión a una IP local
	 * haciendo ping.
	 * 
	 * @author grasotos
	 */
	public class VerificarConexionIpLocal extends AsyncTask<String, Void, Boolean> {

		/**
		 * Verifica la conexión a una IP local haciendo ping. String[] datos = {
		 * ip, timeOut };
		 * 
		 * @param direccionIP
		 *            Dirección IP .
		 * @param timeOut
		 *            Tiempo de espera de respuesta.
		 * @return Estado de la conexión a Internet.
		 * @author grasotos
		 */
		@Override
		protected Boolean doInBackground(final String... params) {
			boolean conexion = Boolean.FALSE;
			try {
				final InetAddress inetAddress = InetAddress.getByName(params[0]);
				if (inetAddress != null && inetAddress.isReachable(Integer.parseInt(params[1]))) {
					conexion = Boolean.TRUE;
				}
			} catch (UnknownHostException e) {
				log4jDroid.error("AutenticadorAndroidProject:AsyncTaskUtilities:VerificarConexionUrlLocal:doInBackground", e);
			} catch (NumberFormatException e) {
				log4jDroid.error("AutenticadorAndroidProject:AsyncTaskUtilities:VerificarConexionUrlLocal:doInBackground", e);
			} catch (IOException e) {
				log4jDroid.error("AutenticadorAndroidProject:AsyncTaskUtilities:VerificarConexionUrlLocal:doInBackground", e);
			} catch (Exception e) {
				log4jDroid.error("AutenticadorAndroidProject:AsyncTaskUtilities:VerificarConexionUrlLocal:doInBackground", e);
			}
			return conexion;
		}
	}
	
	
	

}
