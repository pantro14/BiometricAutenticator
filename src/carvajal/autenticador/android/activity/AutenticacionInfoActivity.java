// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package carvajal.autenticador.android.activity;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import carvajal.autenticador.android.dal.greendao.read.Templates;
import carvajal.autenticador.android.framework.morphosmart.ConexionHuellero;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.exception.MorphoSmartException;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;

import com.imobile.thermalprinterwifiswitch.EncendidoApagadoDispositivos;
import com.morpho.morphosmart.sdk.MorphoDevice;

//import carvajal.autenticador.android.framework.morphosmart.subtype.SecurityOption;

/**
 * @author grasotos
 * 
 */
public class AutenticacionInfoActivity extends FragmentActivity implements Observer {

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(AutenticacionInfoActivity.class);

	/**
	 * 
	 */
	public static boolean isRebootSoft = false;
	// Declare the UI components

	/**
	 * 
	 */
	static MorphoDevice morphoDevice = new MorphoDevice();

	/**
	 * Template de huellas para comparación.
	 */
	public static Templates template = null;

	/**
	 * Listado con huellas para generar el Template.
	 */
	public static List<byte[]> listaHuellas;

	/**
	 * Listado con los ids de las huellas.
	 */
	public static List<Integer> listaIdsHuellas;

	public EncendidoApagadoDispositivos encendidoApagadoDispositivos;

	/**
	 * 
	 */
	@Override
	public void update(Observable arg0, Object arg1) {

	}

	/**
	 * OnCreate de la actividad
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	/**
	 * Permite inicializar el huellero.
	 * 
	 * @param contexto
	 * @throws ConexionHuelleroException
	 */
	public void inicializar(Context contexto) throws ConexionHuelleroException {
		ConexionHuellero.inicializar(contexto);
	}

	/**
	 * Permite validar los huelleros conectador al dispositivo.
	 * 
	 * @param contexto
	 * @return
	 * @throws ConexionHuelleroException
	 */
	public boolean validarHuellerosConectados(Context contexto) throws ConexionHuelleroException {
		return ConexionHuellero.validarHuellerosConectados(contexto);

	}

	/**
	 * @param actividad
	 * @return
	 * @throws ConexionHuelleroException
	 */
	public boolean validarConexion() throws ConexionHuelleroException {
		return ConexionHuellero.validarConexion();

	}

	/**
	 * @param actividad
	 * @throws ConexionHuelleroException
	 */
	public void abrirConexion(Activity actividad) throws ConexionHuelleroException {
		ConexionHuellero.abrirConexion(actividad);

	}

	/**
	 * @param contexto
	 * @param actividad
	 * @throws ConexionHuelleroException
	 */
	public void conectarHuellero(Context contexto, Activity actividad) throws ConexionHuelleroException {
		// solicita permiso
		ConexionHuellero.conectarHuellero(contexto);

	}

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 
	 */
	protected void stop() {

	}

	/**
	 * @throws ConexionHuelleroException
	 * 
	 */
	public void closeDeviceAndFinishActivity() throws ConexionHuelleroException {
		try {
			morphoDevice.closeDevice();
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:AutenticacionInfoActivity:closeDeviceAndFinishActivity:", e);
			throw new ConexionHuelleroException(e.getMessage());
		} finally {
			ProcessInfo.getInstance().setMorphoDevice(null);
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			closeDeviceAndFinishActivity();
		} catch (ConexionHuelleroException e) {
			log4jDroid.error("AutenticadorAndroidProject:AutenticacionInfoActivity:closeDeviceAndFinishActivity:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try {
			morphoDevice.resumeConnection(30, this);
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 */
	@Override
	protected void onPause() {
		try {
			if (morphoDevice != null && ProcessInfo.getInstance().isStarted()) {
			//	morphoDevice.cancelLiveAcquisition();
			//	try {
			//		Thread.sleep(1000);
				//} catch (InterruptedException e) {
					//e.printStackTrace();
				//}
			}

			super.onPause();
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 */
	public final void onRebootSoft(View view) {
		// enableDisableBoutton(false);
		try {
			isRebootSoft = true;
			int ret = morphoDevice.rebootSoft(30, this);

			if (ret != 0) {
				// alert(getString(R.string.msg_rebootfailure));
			}
		} catch (Exception e) {

		}
	}

	/**
	 * @param view
	 * @throws MorphoSmartException
	 */
	public final void onCloseAndQuit(View view) throws MorphoSmartException {
		try {
			finish();
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:AutenticacionInfoActivity:onCloseAndQuit:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * @throws MorphoSmartException
	 * 
	 */
	public void stopProcess() throws MorphoSmartException {
		try {
			if (ProcessInfo.getInstance().isStarted()) {
				this.stop();
			}
			// enableDisableIHM(true);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:AutenticacionInfoActivity:stopProcess:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}



	@Override
	protected void onStart() {
		super.onStart();
	}

}
