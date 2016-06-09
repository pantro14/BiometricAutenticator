package carvajal.autenticador.android.framework.morphosmart;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import carvajal.autenticador.android.activity.AutenticacionInfoActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.info.MorphoInfo;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;
import carvajal.autenticador.android.util.Util;

import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.MorphoDevice;

/**
 * Clase para conexión del dispositivo biometrico USB.
 * 
 * @author grasotos
 * @date 24-Feb-2015
 */
@SuppressLint("UseValueOf")
public class ConexionHuellero {

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(ConexionHuellero.class);

	/**
	 * Dispositivo morpho a conectar
	 */
	public static MorphoDevice morphoDevice;

	/**
	 * Nombre del sensor detectado
	 */
	private static String sensorName = "";

	public static USBManager usbManager;

	/**
	 * Método para inicializar el huellero, solicita el permiso para usar el usb
	 * del dispositivo.
	 * 
	 * @param context
	 *            de la aplicación.
	 * @return {@code true} si el usb tiene permiso, de lo contrario
	 *         {@code false}.
	 * @author grasotos
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static boolean inicializar(Context context) throws ConexionHuelleroException {
		boolean permiso = false;
		try {

			if (AutenticacionInfoActivity.isRebootSoft) {

				AutenticacionInfoActivity.isRebootSoft = false;
			}

			morphoDevice = new MorphoDevice();

			usbManager = USBManager.getInstance();
			usbManager.initialize(context, "com.morpho.android.usb.USB_PERMISSION");

			if (usbManager.isDevicesHasPermission() == true) {
				permiso = true;
				return permiso;
			}

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:inicializar:", e);
			throw new ConexionHuelleroException(e.getMessage());
		}
		return permiso;
	}

	/**
	 * Enumera los huelleros conectados al dispositivo.
	 * 
	 * @param contexto
	 * @return {@code true} si existe al menos un huellero conectado, de lo
	 *         contrario {@code false}.
	 * @author grasotos
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static boolean validarHuellerosConectados(Context contexto) throws ConexionHuelleroException {
		try {

			Integer nbUsbDevice = new Integer(0);

			int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);

			if (ret == ErrorCodes.MORPHO_OK) {

				// Si esta conectado enumera los dispositivos
				if (nbUsbDevice > 0) {

					// Asigna el nombre del dispositivo usb al sensor
					sensorName = morphoDevice.getUsbDeviceName(0);
					// ACTIVAR CONEXION

					return true;

				} else {

					// Si no existe ningún dispositivo conectado
					return false;
				}

			} else {
				// Error de conexión del dispositivo usb

				return false;

			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:validarHuellerosConectados:", e);
			throw new ConexionHuelleroException(e.getMessage());
		}

	}

	/**
	 * Permite abrir la conexion con el nombre del sensor obtenido en
	 * {@link validarHuellerosConectados}.
	 * 
	 * @param contexto
	 *            de la aplicación.
	 * @author grasotos
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static void abrirConexion(Context contexto) throws ConexionHuelleroException {

		try {
			// abre el usb
			int ret = morphoDevice.openUsbDevice(sensorName, 0);

			if (ret != 0) {
				// ERROR, no puede abrir el dispositivo USB
				AlertDialog cuadDialogo = Util.mensajeAceptar(contexto, R.style.TemaDialogo, ErrorCodes.getError(ret, morphoDevice.getInternalError()),
						ErrorCodes.getError(ret, morphoDevice.getInternalError()), Util.DIALOG_ERROR, null);

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, contexto);

			} else {

				ProcessInfo.getInstance().setMSOSerialNumber(sensorName);
				String productDescriptor = morphoDevice.getProductDescriptor();
				java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(productDescriptor, "\n");
				if (tokenizer.hasMoreTokens()) {
					String l_s_current = tokenizer.nextToken();
					if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP")) {
						MorphoInfo.m_b_fvp = true;
						// TODO:VALIDAR FINGER VP PORQUE NO FUNCIONARIA
						// MORPHO_ADVANCED_MATCHING_STRATEGY
					}
				}

				if (ret != ErrorCodes.MORPHO_OK) {
					if (ret == ErrorCodes.MORPHOERR_BASE_NOT_FOUND) {

					}
				} else {
					morphoDevice.closeDevice();
				}

			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:abrirConexion:", e);
			throw new ConexionHuelleroException(e.getMessage());
		}
	}

	/**
	 * Valida si existe un MorphoDevice conectado.
	 * 
	 * @param actividad
	 * @return true si la conexión es válida, de lo contrario false.
	 * @author grasotos
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static boolean validarConexion() throws ConexionHuelleroException {

		boolean conexion = true;
		try {
			if (ProcessInfo.getInstance().getMorphoDevice() == null) {
				String sensorName = ProcessInfo.getInstance().getMSOSerialNumber();
				int ret = morphoDevice.openUsbDevice(sensorName, 0);
				if (ret != ErrorCodes.MORPHO_OK) {
					// actividad.finish();
					conexion = false;
					return conexion;

				}

			}
			ProcessInfo.getInstance().setMorphoDevice(morphoDevice);

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:validarConexion:", e);
			throw new ConexionHuelleroException(e.getMessage());
		}

		return conexion;
	}

	/**
	 * Permite ejecutar la conexión al dispositivo biometrico USB.
	 * 
	 * @param contexto
	 *            de la aplicación.
	 * @param actividad
	 *            donde se esta ejecutando la aplicación.
	 * @return true si el huellero está conectado, de lo contrario false.
	 * @author grasotos
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static boolean conectarHuellero(Context contexto) throws ConexionHuelleroException {
		boolean accedeLectorHuellas = true;
		try {
			if (inicializar(contexto)) {
				// si tiene permiso conectar

				if (validarHuellerosConectados(contexto)) {
					abrirConexion(contexto);
					validarConexion();
				} else {
					accedeLectorHuellas = false;
				}

			} else {
				accedeLectorHuellas = false;
			}
		} catch (ConexionHuelleroException e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:conectarHuellero:", e);
			throw new ConexionHuelleroException(e.getMessage());
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConexionHuellero:conectarHuellero:", e);
			throw new ConexionHuelleroException(e.getMessage());
		}

		return accedeLectorHuellas;

	}

}
