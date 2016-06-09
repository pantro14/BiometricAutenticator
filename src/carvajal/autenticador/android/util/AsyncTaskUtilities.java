package carvajal.autenticador.android.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import android.os.AsyncTask;

/**
 * Implementaci�n de lo M�todos utilitarios para la verificaci�n de
 * conectividad.
 * 
 * @author grasotos
 */
public class AsyncTaskUtilities {

	/**
	 * Log de la aplicaci�n Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(AsyncTaskUtilities.class);

	/**
	 * Implementaci�n del M�todo que verifica la conexi�n a una IP local
	 * haciendo ping.
	 * 
	 * @author grasotos
	 */
	public class VerificarConexionIpLocal extends AsyncTask<String, Void, Boolean> {

		/**
		 * Verifica la conexi�n a una IP local haciendo ping. String[] datos = {
		 * ip, timeOut };
		 * 
		 * @param direccionIP
		 *            Direcci�n IP .
		 * @param timeOut
		 *            Tiempo de espera de respuesta.
		 * @return Estado de la conexi�n a Internet.
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
