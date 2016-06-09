// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package carvajal.autenticador.android.activity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import carvajal.autenticador.android.dal.greendao.read.Templates;
import carvajal.autenticador.android.framework.morphosmart.MorphoSmartManager;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.exception.MorphoSmartException;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;
import carvajal.autenticador.android.util.Util;

import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.FalseAcceptanceRate;
import com.morpho.morphosmart.sdk.MatchingStrategy;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.TemplateList;

/**
 * @author grasotos
 * @date 24-Feb-2015
 */
public class ProcessActivity extends Activity implements Observer {

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(ProcessActivity.class);
	/**
	 * id del actual bitmap para la captura
	 */
	private int currentCaptureBitmapId = 0;
	/**
	 * Boolean para la verificacion de la captura
	 */
	private boolean isCaptureVerif = false;
	/**
	 * Handler para la toma de huella
	 */
	private Handler mHandler = new Handler();
	/**
	 * String para mensajes del huellero
	 */
	String strMessage = new String();
	/**
	 * 
	 */
	private int index;
	/**
	 * /** Dispositivo Morpho
	 */
	private MorphoDevice morphoDevice;
	/**
	 * Manager del dispositivo morpho.
	 */
	private MorphoSmartManager morphoSmartManager;

	/**
	 * 
	 */
	private final byte[] template1 = { -126, 27, 0, 34, 83, -1, -128, -128, -112, -82, 91, 108, 126, -103, 124, 98, -55, -78, -74, -89, -106, -82, 71, -103, 76, 127, 94, -83, 126,
			-80, 98, 125, 83, 117, 111, 112, 94, 102, -70, -115, 119, 109, -128, 110, -105, -124, -82, -122, -126, 94, -105, 105, -97, 110, -69, 118, -67, 127, -61, 104, -56,
			-121, -1, -105, -9, -1, -1, 107, 69, 73, -87, -10, -20, -3 };

	private static int PULGAR_DERECHO;
	private static int INDICE_DERECHO;
	private static int DEDO_MEDIO_DERECHO;
	private static int ANULAR_DERECHO;
	private static int MENIQUE_DERECHO;
	private static int PULGAR_IZQUIERDO;
	private static int INDICE_IZQUIERDO;
	private static int DEDO_MEDIO_IZQUIERDO;
	private static int ANULAR_IZQUIERDO;
	private static int MENIQUE_IZQUIERDO;

	/**
	 * OnCreate donde se obtiene el dispositivo inicializado.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_verify);
		try {

			PULGAR_DERECHO = getResources().getInteger(R.integer.pulgar_derecho);
			INDICE_DERECHO = getResources().getInteger(R.integer.indice_derecho);
			DEDO_MEDIO_DERECHO = getResources().getInteger(R.integer.dedo_medio_derecho);
			ANULAR_DERECHO = getResources().getInteger(R.integer.anular_derecho);
			MENIQUE_DERECHO = getResources().getInteger(R.integer.menique_derecho);

			PULGAR_IZQUIERDO = getResources().getInteger(R.integer.pulgar_izquierdo);
			INDICE_IZQUIERDO = getResources().getInteger(R.integer.indice_izquierdo);
			DEDO_MEDIO_IZQUIERDO = getResources().getInteger(R.integer.dedo_medio_izquierdo);
			ANULAR_IZQUIERDO = getResources().getInteger(R.integer.anular_izquierdo);
			MENIQUE_IZQUIERDO = getResources().getInteger(R.integer.menique_izquierdo);

			morphoSmartManager = new MorphoSmartManager();
			morphoDevice = ProcessInfo.getInstance().getMorphoDevice();

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:onCreate:", e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26),
					Util.DIALOG_ERROR, null);
			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

		}
	}

	/**
	 * Inicializa la toma de captura de huella para verificación.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	@Override
	public void onResume() {
		super.onResume();
		try {
			ProcessInfo.getInstance().getMorphoInfo();
			final ProcessActivity processActivity = this;
			currentCaptureBitmapId = R.id.imageView1;
			verificacion(processActivity);
		} catch (MorphoSmartException e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:onResume:", e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26),
					Util.DIALOG_ERROR, null);
			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
		}

	}

	/**
	 * Permite hacer la verificacion de la huella.
	 * 
	 * @param observer
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 */
	public void verificacion(final Observer observer) throws MorphoSmartException {

		Intent intent = getIntent();
		String diagnostico = intent.getStringExtra(getString(R.string.intent_huella));
		if (diagnostico.equals(getString(R.string.intent_diagnostico))) {

			if (DiagnosticoActivity.procesoCancelado) {

				Intent i = new Intent(ProcessActivity.this, DiagnosticoActivity.class);

				i.putExtra(getString(R.string.id_codigo), 3);
				setResult(2, i);
				ProcessActivity.this.finish();

			} else {
				verificacionTemplate(observer);
			}

		} else {
			verificacionTemplate(observer);
		}

	}

	/**
	 * Permite realizar la comparacion entre el template o templates almacenados
	 * vs la huella capturada.
	 * 
	 * @param observer
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 */
	public void verificacionTemplate(final Observer observer) throws MorphoSmartException {

		try {

			Thread commandThread = (new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						List<byte[]> buffers = null;
						TemplateList templateList = null;

						// Se modifica para reutilizar diagnostico

						Intent intent = getIntent();
						String diagnostico = intent.getStringExtra(getString(R.string.intent_huella));
						if (diagnostico.equals(getString(R.string.intent_diagnostico))) {

							Templates template = new Templates();
							template.setTemplate1(template1);
							template.setTemplate2(null);
							template.setTemplate3(null);
							template.setTemplate4(null);
							template.setTemplate5(null);
							template.setTemplate6(null);
							template.setTemplate7(null);
							template.setTemplate8(null);
							template.setTemplate9(null);
							template.setTemplate10(null);
							// armar template
							buffers = listaHuellas(template);

							templateList = morphoSmartManager.generarListaTemplates(buffers);

						} else {
							buffers = AutenticacionActivity.listaHuellas;

							templateList = morphoSmartManager.generarListaTemplates(buffers);
						}

						int timeOut = 4;
						int far = FalseAcceptanceRate.MORPHO_FAR_5;
						Coder coderChoice = Coder.MORPHO_MSO_V9_CODER;
						int detectModeChoice = DetectionMode.MORPHO_VERIF_DETECT_MODE.getValue();
						int matchingStrategy = MatchingStrategy.MORPHO_ADVANCED_MATCHING_STRATEGY.getValue();

						int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

						callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

						ResultMatching resultMatching = new ResultMatching();

						int ret = morphoDevice.verify(timeOut, far, coderChoice, detectModeChoice, matchingStrategy, templateList, callbackCmd, observer, resultMatching);

						ProcessInfo.getInstance().setCommandBioStart(false);

						morphoSmartManager.getAndWriteFFDLogs(morphoDevice);

						String message = "";

						if (ret == ErrorCodes.MORPHO_OK) {
							message = "Matching Score = " + resultMatching.getMatchingScore() + "\nPK Number = " + resultMatching.getMatchingPKNumber();
						}
						mHandler.post(new Runnable() {
							@Override
							public synchronized void run() {
								// alert(l_ret, internalError, "Verify", msg);
							}
						});
						notifyEndProcess(resultMatching.getMatchingScore(), resultMatching.getMatchingPKNumber(), ret);
					} catch (MorphoSmartException e) {
						log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:morphoDeviceVerifyWithFile:", e);
						AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26),
								Util.DIALOG_ERROR, null);
						cuadDialogo.show();

						Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

					} catch (Exception e) {
						e.printStackTrace();
						log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:morphoDeviceVerifyWithFile:", e);
						AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26),
								Util.DIALOG_ERROR, null);
						cuadDialogo.show();

						Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

					}
				}
			}));

			commandThread.start();

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:morphoDeviceVerifyWithFile:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * 
	 * Mensajes
	 * 
	 * @param iCodeError
	 * @param iInternalError
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String convertToInternationalMessage(int iCodeError, int iInternalError) {
		switch (iCodeError) {
		case ErrorCodes.MORPHO_OK:
			return getString(R.string.MORPHO_OK);
		case ErrorCodes.MORPHOERR_INTERNAL:
			return getString(R.string.MORPHOERR_INTERNAL);
		case ErrorCodes.MORPHOERR_PROTOCOLE:
			return getString(R.string.MORPHOERR_PROTOCOLE);
		case ErrorCodes.MORPHOERR_CONNECT:
			return getString(R.string.MORPHOERR_CONNECT);
		case ErrorCodes.MORPHOERR_CLOSE_COM:
			return getString(R.string.MORPHOERR_CLOSE_COM);
		case ErrorCodes.MORPHOERR_BADPARAMETER:
			return getString(R.string.MORPHOERR_BADPARAMETER);
		case ErrorCodes.MORPHOERR_MEMORY_PC:
			return getString(R.string.MORPHOERR_MEMORY_PC);
		case ErrorCodes.MORPHOERR_MEMORY_DEVICE:
			return getString(R.string.MORPHOERR_MEMORY_DEVICE);
		case ErrorCodes.MORPHOERR_NO_HIT:
			return getString(R.string.MORPHOERR_NO_HIT);
		case ErrorCodes.MORPHOERR_STATUS:
			return getString(R.string.MORPHOERR_STATUS);
		case ErrorCodes.MORPHOERR_DB_FULL:
			return getString(R.string.MORPHOERR_DB_FULL);
		case ErrorCodes.MORPHOERR_DB_EMPTY:
			return getString(R.string.MORPHOERR_DB_EMPTY);
		case ErrorCodes.MORPHOERR_ALREADY_ENROLLED:
			return getString(R.string.MORPHOERR_ALREADY_ENROLLED);
		case ErrorCodes.MORPHOERR_BASE_NOT_FOUND:
			return getString(R.string.MORPHOERR_BASE_NOT_FOUND);
		case ErrorCodes.MORPHOERR_BASE_ALREADY_EXISTS:
			return getString(R.string.MORPHOERR_BASE_ALREADY_EXISTS);
		case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DB:
			return getString(R.string.MORPHOERR_NO_ASSOCIATED_DB);
		case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DEVICE:
			return getString(R.string.MORPHOERR_NO_ASSOCIATED_DEVICE);
		case ErrorCodes.MORPHOERR_INVALID_TEMPLATE:
			return getString(R.string.MORPHOERR_INVALID_TEMPLATE);
		case ErrorCodes.MORPHOERR_NOT_IMPLEMENTED:
			return getString(R.string.MORPHOERR_NOT_IMPLEMENTED);
		case ErrorCodes.MORPHOERR_TIMEOUT:
			return getString(R.string.MORPHOERR_TIMEOUT);
		case ErrorCodes.MORPHOERR_NO_REGISTERED_TEMPLATE:
			return getString(R.string.MORPHOERR_NO_REGISTERED_TEMPLATE);
		case ErrorCodes.MORPHOERR_FIELD_NOT_FOUND:
			return getString(R.string.MORPHOERR_FIELD_NOT_FOUND);
		case ErrorCodes.MORPHOERR_CORRUPTED_CLASS:
			return getString(R.string.MORPHOERR_CORRUPTED_CLASS);
		case ErrorCodes.MORPHOERR_TO_MANY_TEMPLATE:
			return getString(R.string.MORPHOERR_TO_MANY_TEMPLATE);
		case ErrorCodes.MORPHOERR_TO_MANY_FIELD:
			return getString(R.string.MORPHOERR_TO_MANY_FIELD);
		case ErrorCodes.MORPHOERR_MIXED_TEMPLATE:
			return getString(R.string.MORPHOERR_MIXED_TEMPLATE);
		case ErrorCodes.MORPHOERR_CMDE_ABORTED:
			return getString(R.string.MORPHOERR_CMDE_ABORTED);
		case ErrorCodes.MORPHOERR_INVALID_PK_FORMAT:
			return getString(R.string.MORPHOERR_INVALID_PK_FORMAT);
		case ErrorCodes.MORPHOERR_SAME_FINGER:
			return getString(R.string.MORPHOERR_SAME_FINGER);
		case ErrorCodes.MORPHOERR_OUT_OF_FIELD:
			return getString(R.string.MORPHOERR_OUT_OF_FIELD);
		case ErrorCodes.MORPHOERR_INVALID_USER_ID:
			return getString(R.string.MORPHOERR_INVALID_USER_ID);
		case ErrorCodes.MORPHOERR_INVALID_USER_DATA:
			return getString(R.string.MORPHOERR_INVALID_USER_DATA);
		case ErrorCodes.MORPHOERR_FIELD_INVALID:
			return getString(R.string.MORPHOERR_FIELD_INVALID);
		case ErrorCodes.MORPHOERR_USER_NOT_FOUND:
			return getString(R.string.MORPHOERR_USER_NOT_FOUND);
		case ErrorCodes.MORPHOERR_COM_NOT_OPEN:
			return getString(R.string.MORPHOERR_COM_NOT_OPEN);
		case ErrorCodes.MORPHOERR_ELT_ALREADY_PRESENT:
			return getString(R.string.MORPHOERR_ELT_ALREADY_PRESENT);
		case ErrorCodes.MORPHOERR_NOCALLTO_DBQUERRYFIRST:
			return getString(R.string.MORPHOERR_NOCALLTO_DBQUERRYFIRST);
		case ErrorCodes.MORPHOERR_USER:
			return getString(R.string.MORPHOERR_USER);
		case ErrorCodes.MORPHOERR_BAD_COMPRESSION:
			return getString(R.string.MORPHOERR_BAD_COMPRESSION);
		case ErrorCodes.MORPHOERR_SECU:
			return getString(R.string.MORPHOERR_SECU);
		case ErrorCodes.MORPHOERR_CERTIF_UNKNOW:
			return getString(R.string.MORPHOERR_CERTIF_UNKNOW);
		case ErrorCodes.MORPHOERR_INVALID_CLASS:
			return getString(R.string.MORPHOERR_INVALID_CLASS);
		case ErrorCodes.MORPHOERR_USB_DEVICE_NAME_UNKNOWN:
			return getString(R.string.MORPHOERR_USB_DEVICE_NAME_UNKNOWN);
		case ErrorCodes.MORPHOERR_CERTIF_INVALID:
			return getString(R.string.MORPHOERR_CERTIF_INVALID);
		case ErrorCodes.MORPHOERR_SIGNER_ID:
			return getString(R.string.MORPHOERR_SIGNER_ID);
		case ErrorCodes.MORPHOERR_SIGNER_ID_INVALID:
			return getString(R.string.MORPHOERR_SIGNER_ID_INVALID);
		case ErrorCodes.MORPHOERR_FFD:
			return getString(R.string.MORPHOERR_FFD);
		case ErrorCodes.MORPHOERR_MOIST_FINGER:
			return getString(R.string.MORPHOERR_MOIST_FINGER);
		case ErrorCodes.MORPHOERR_NO_SERVER:
			return getString(R.string.MORPHOERR_NO_SERVER);
		case ErrorCodes.MORPHOERR_OTP_NOT_INITIALIZED:
			return getString(R.string.MORPHOERR_OTP_NOT_INITIALIZED);
		case ErrorCodes.MORPHOERR_OTP_PIN_NEEDED:
			return getString(R.string.MORPHOERR_OTP_PIN_NEEDED);
		case ErrorCodes.MORPHOERR_OTP_REENROLL_NOT_ALLOWED:
			return getString(R.string.MORPHOERR_OTP_REENROLL_NOT_ALLOWED);
		case ErrorCodes.MORPHOERR_OTP_ENROLL_FAILED:
			return getString(R.string.MORPHOERR_OTP_ENROLL_FAILED);
		case ErrorCodes.MORPHOERR_OTP_IDENT_FAILED:
			return getString(R.string.MORPHOERR_OTP_IDENT_FAILED);
		case ErrorCodes.MORPHOERR_NO_MORE_OTP:
			return getString(R.string.MORPHOERR_NO_MORE_OTP);
		case ErrorCodes.MORPHOERR_OTP_NO_HIT:
			return getString(R.string.MORPHOERR_OTP_NO_HIT);
		case ErrorCodes.MORPHOERR_OTP_ENROLL_NEEDED:
			return getString(R.string.MORPHOERR_OTP_ENROLL_NEEDED);
		case ErrorCodes.MORPHOERR_DEVICE_LOCKED:
			return getString(R.string.MORPHOERR_DEVICE_LOCKED);
		case ErrorCodes.MORPHOERR_DEVICE_NOT_LOCK:
			return getString(R.string.MORPHOERR_DEVICE_NOT_LOCK);
		case ErrorCodes.MORPHOERR_OTP_LOCK_GEN_OTP:
			return getString(R.string.MORPHOERR_OTP_LOCK_GEN_OTP);
		case ErrorCodes.MORPHOERR_OTP_LOCK_SET_PARAM:
			return getString(R.string.MORPHOERR_OTP_LOCK_SET_PARAM);
		case ErrorCodes.MORPHOERR_OTP_LOCK_ENROLL:
			return getString(R.string.MORPHOERR_OTP_LOCK_ENROLL);
		case ErrorCodes.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH:
			return getString(R.string.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH);
		case ErrorCodes.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN:
			return getString(R.string.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN);
		case ErrorCodes.MORPHOERR_LICENSE_MISSING:
			return getString(R.string.MORPHOERR_LICENSE_MISSING);
		case ErrorCodes.MORPHOERR_CANT_GRAN_PERMISSION_USB:
			return getString(R.string.MORPHOERR_CANT_GRAN_PERMISSION_USB);
		default:
			return String.format("Unknown error %d, Internal Error = %d", iCodeError, iInternalError);
		}
	}

	/**
	 * Notifica la finalizacion de la toma de huella.
	 * 
	 * @param score
	 * @param dedo
	 * @param codeError
	 */
	private void notifyEndProcess(final int score, final int dedo, final int codeError) {
		mHandler.post(new Runnable() {
			@Override
			public synchronized void run() {
				try {

					Intent intent = getIntent();
					String diagnostico = intent.getStringExtra(getString(R.string.intent_huella));
					if (diagnostico.equals(getString(R.string.intent_diagnostico))) {

						// ProcessInfo.getInstance().getMorphoSample().stopProcess();
						Intent i = new Intent(ProcessActivity.this, AutenticacionActivity.class);
						// enviar score
						i.putExtra(getString(R.string.id_score), score);
						i.putExtra(getString(R.string.id_dedo), dedo);
						i.putExtra(getString(R.string.id_codigo), codeError);
						setResult(2, i);
						ProcessActivity.this.finish();
					} else {
						Intent i = new Intent(ProcessActivity.this, DiagnosticoActivity.class);
						// enviar score
						i.putExtra(getString(R.string.id_score), score);
						i.putExtra(getString(R.string.id_dedo), dedo);
						i.putExtra(getString(R.string.id_codigo), codeError);
						setResult(2, i);
						ProcessActivity.this.finish();
					}

				} catch (Exception e) {
					log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:notifyEndProcess:", e);
					AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26),
							Util.DIALOG_ERROR, null);
					cuadDialogo.show();

					Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

				}
			}
		});

	}

	/**
	 * Actualiza la barra de progreso.
	 * 
	 * @param level
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 */
	@SuppressWarnings("deprecation")
	private void updateSensorProgressBar(int level) throws MorphoSmartException {
		try {
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.vertical_progressbar);

			final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
			ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

			int color = Color.GREEN;

			if (level <= 25) {
				color = Color.RED;
			} else if (level <= 50) {
				color = Color.YELLOW;
			}
			pgDrawable.getPaint().setColor(color);
			ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
			progressBar.setProgressDrawable(progress);
			progressBar.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
			progressBar.setProgress(level);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:updateSensorProgressBar:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * 
	 * Actualiza los mensajes del sensor.
	 * 
	 * @param sensorMessage
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 */
	private void updateSensorMessage(String sensorMessage) throws MorphoSmartException {
		try {
			TextView tv = (TextView) findViewById(R.id.textViewMessage);
			tv.setText(sensorMessage);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:updateSensorMessage:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * Permite actualizar en tiempo real la imagen de la huella.
	 * 
	 * @param bitmap
	 * @param id
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 */
	private void updateImage(Bitmap bitmap, int id) throws MorphoSmartException {
		try {
			ImageView iv = (ImageView) findViewById(id);
			iv.setImageBitmap(bitmap);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:updateImage:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * Permite actualizar la pantalla de obtencion de la huella. Tomado del
	 * ejemplo del SDK.
	 * 
	 * @author grasotos
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		try {
			// convert the object to a callback back message.
			CallbackMessage message = (CallbackMessage) arg;

			int type = message.getMessageType();

			switch (type) {

			case 1:
				// message is a command.
				Integer command = (Integer) message.getMessage();

				// Analyze the command.
				switch (command) {
				case 0:
					strMessage = getString(R.string.no_dedo);
					break;
				case 1:
					strMessage = getString(R.string.dedo_arriba);
					break;
				case 2:
					strMessage = getString(R.string.dedo_abajo);
					break;
				case 3:
					strMessage = getString(R.string.dedo_izq);
					break;
				case 4:
					strMessage = getString(R.string.dedo_der);
					break;
				case 5:
					strMessage = getString(R.string.presionar);
					break;
				case 6:
					strMessage = getString(R.string.movimiento_latente);
					break;
				case 7:
					strMessage = getString(R.string.quitar_dedo);
					break;
				case 8:
					strMessage = getString(R.string.huella_capturada);
					// switch live acquisition ImageView
					if (isCaptureVerif) {
						isCaptureVerif = false;
						index = 4; // R.id.imageView5;
					} else {
						index++;
					}

					switch (index) {

					default:
					case 0:
						currentCaptureBitmapId = R.id.imageView1;
						break;
					}
					break;
				}

				mHandler.post(new Runnable() {
					@Override
					public synchronized void run() {
						try {
							updateSensorMessage(strMessage);
						} catch (MorphoSmartException e) {
							log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:update:", e);
							AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login),
									getString(R.string.M26), Util.DIALOG_ERROR, null);
							cuadDialogo.show();

							Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
						}
					}
				});

				break;
			case 2:
				// message is a low resolution image, display it.
				byte[] image = (byte[]) message.getMessage();

				MorphoImage morphoImage = MorphoImage.getMorphoImageFromLive(image);
				int imageRowNumber = morphoImage.getMorphoImageHeader().getNbRow();
				int imageColumnNumber = morphoImage.getMorphoImageHeader().getNbColumn();
				final Bitmap imageBmp = Bitmap.createBitmap(imageColumnNumber, imageRowNumber, Config.ALPHA_8);

				imageBmp.copyPixelsFromBuffer(ByteBuffer.wrap(morphoImage.getImage(), 0, morphoImage.getImage().length));
				mHandler.post(new Runnable() {
					@Override
					public synchronized void run() {
						try {
							updateImage(imageBmp, currentCaptureBitmapId);
						} catch (MorphoSmartException e) {
							log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:update:", e);
							AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login),
									getString(R.string.M26), Util.DIALOG_ERROR, null);
							cuadDialogo.show();

							Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
						}
					}
				});
				break;
			case 3:
				// message is the coded image quality.
				final Integer quality = (Integer) message.getMessage();
				mHandler.post(new Runnable() {
					@Override
					public synchronized void run() {
						try {
							updateSensorProgressBar(quality);
						} catch (MorphoSmartException e) {
							log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:update:", e);
							AlertDialog cuadDialogo = Util.mensajeAceptar(ProcessActivity.this, R.style.TemaDialogo, getString(R.string.title_activity_login),
									getString(R.string.M26), Util.DIALOG_ERROR, null);
							cuadDialogo.show();

							Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

						}
					}
				});
				break;
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ProcessActivity:update:", e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(this, R.style.TemaDialogo, getString(R.string.title_activity_login), getString(R.string.M26), Util.DIALOG_ERROR, null);
			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
		}
	}

	/**
	 * Permite generar el listado de byte[] obtenido de la base de datos
	 * mediante la entidad Templates.
	 * 
	 * @param template
	 *            con la información del elector obtenida de la base de datos.
	 * @return List<byte[]> con la información de las huellas del elector.
	 */
	public static List<byte[]> listaHuellas(Templates template) {

		List<byte[]> listaHuellas = new ArrayList<byte[]>();
		AutenticacionInfoActivity.listaIdsHuellas = new ArrayList<Integer>();

		byte[] template1 = template.getTemplate1();
		if (template1 != null) {
			if (tieneDatosHuella(template1)) {
				listaHuellas.add(template1);
				AutenticacionInfoActivity.listaIdsHuellas.add(PULGAR_DERECHO);
			}

		}
		byte[] template2 = template.getTemplate2();
		if (template2 != null) {
			if (tieneDatosHuella(template2)) {
				listaHuellas.add(template2);
				AutenticacionInfoActivity.listaIdsHuellas.add(INDICE_DERECHO);
			}
		}
		byte[] template3 = template.getTemplate3();
		if (template3 != null) {
			if (tieneDatosHuella(template3)) {
				listaHuellas.add(template3);
				AutenticacionInfoActivity.listaIdsHuellas.add(DEDO_MEDIO_DERECHO);
			}
		}
		byte[] template4 = template.getTemplate4();
		if (template4 != null) {
			if (tieneDatosHuella(template4)) {
				listaHuellas.add(template4);
				AutenticacionInfoActivity.listaIdsHuellas.add(ANULAR_DERECHO);
			}
		}
		byte[] template5 = template.getTemplate5();
		if (template5 != null) {
			if (tieneDatosHuella(template5)) {
				listaHuellas.add(template5);
				AutenticacionInfoActivity.listaIdsHuellas.add(MENIQUE_DERECHO);
			}
		}
		byte[] template6 = template.getTemplate6();
		if (template6 != null) {
			if (tieneDatosHuella(template6)) {
				listaHuellas.add(template6);
				AutenticacionInfoActivity.listaIdsHuellas.add(PULGAR_IZQUIERDO);
			}
		}
		byte[] template7 = template.getTemplate7();
		if (template7 != null) {
			if (tieneDatosHuella(template7)) {
				listaHuellas.add(template7);
				AutenticacionInfoActivity.listaIdsHuellas.add(INDICE_IZQUIERDO);
			}
		}
		byte[] template8 = template.getTemplate8();
		if (template8 != null) {
			if (tieneDatosHuella(template8)) {
				listaHuellas.add(template8);
				AutenticacionInfoActivity.listaIdsHuellas.add(DEDO_MEDIO_IZQUIERDO);
			}
		}
		byte[] template9 = template.getTemplate9();
		if (template9 != null) {
			if (tieneDatosHuella(template9)) {
				listaHuellas.add(template9);
				AutenticacionInfoActivity.listaIdsHuellas.add(ANULAR_IZQUIERDO);
			}
		}
		byte[] template10 = template.getTemplate10();
		if (template10 != null) {
			if (tieneDatosHuella(template10)) {
				listaHuellas.add(template10);
				AutenticacionInfoActivity.listaIdsHuellas.add(MENIQUE_IZQUIERDO);
			}
		}

		return listaHuellas;

	}

	/**
	 * Permite validar si el elector tiene al menos una huella en la base de
	 * datos del dispositivo.
	 * 
	 * @param template
	 *            con las huellas del elector, sino tiene nada este es null.
	 * @return {@code true} si el elector tiene huellas en la base de datos del
	 *         dispositivo de lo contrario {@code false}.
	 */
	public static boolean tieneHuellas(Templates template) {
		boolean tieneHuellas = false;

		if (template != null) {
			tieneHuellas = true;
		}
		return tieneHuellas;
	}

	/**
	 * Consulta si la huella proveniente del template contiene bytes o tiene
	 * datos válidos.
	 * 
	 * @param template
	 *            con los bytes de la huella.
	 * @return boolean {@code true} cuando tiene huellas, de lo contrario
	 *         {@code false}.
	 */
	public static boolean tieneDatosHuella(byte[] template) {

		boolean tieneDatos = false;
		for (byte b : template) {

			if (b != 0) {
				tieneDatos = true;
			}
		}

		return tieneDatos;
	}

	/**
	 * Bloquea el botón regresar.
	 * 
	 * @author grasotos
	 */
	@Override
	public void onBackPressed() {

	}
}
