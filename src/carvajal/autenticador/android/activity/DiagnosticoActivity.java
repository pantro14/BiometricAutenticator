package carvajal.autenticador.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import carvajal.autenticador.android.activity.asynctask.DiagnosticServerTestAsyncTask;
import carvajal.autenticador.android.bl.exception.ConfiguracionBLException;
import carvajal.autenticador.android.code2D.ActivityCapture;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.util.Util;

import com.morpho.morphosmart.sdk.ErrorCodes;

/**
 * En esta claase activity se maneja la lógica y el comportamiento del layout
 * utilizado el manejo de hilos secuenciales sin bloqueo de interfaz gráfica
 * para el diagnóstico de: 1. Huellero, 2. Lector 2D, 3. Wifi, 4.Impresora.
 * 
 * @author grasotos
 * 
 */
/**
 * @author grasotos
 * 
 */
public class DiagnosticoActivity extends Activity {
	/**
	 * Logs del diagnóstico.
	 * 
	 * @author grasotos
	 */
	private final static Logger log4jDroid = Logger.getLogger(DiagnosticoActivity.class);

	/**
	 * Instancia para impresora.
	 * 
	 * @author grasotos
	 */
	private POSSDKManager posSdkImpresora;

	/**
	 * Tarea donde se ejecuta el llamado al huellero para las pruebas y
	 * diagóstico.
	 * 
	 * @author grasotos
	 */
	private TimerTask huellaTask;

	/**
	 * Tarea donde se ejecuta el llamado al lector 2D para las pruebas y
	 * diagóstico.
	 * 
	 * @author grasotos
	 */
	private TimerTask camaraTask;

	/**
	 * Tarea donde se ejecuta el llamado al wifi y conexión inalámbrica para las
	 * pruebas y diagóstico.
	 * 
	 * @author grasotos
	 */
	private TimerTask conexionTask;

	/**
	 * Tarea donde se ejecuta el llamado a la impresora para las pruebas y
	 * diagóstico.
	 * 
	 * @author grasotos
	 */
	private TimerTask impresoraTask;

	/**
	 * Variable que permite informar si el proceso de diagnóstico ha sido
	 * cancelado por el usuario. Si el proceso ha sido cancelado es {@code true}
	 * , de lo contrario {@code false}.
	 * 
	 * @author grasotos
	 */
	public static boolean procesoCancelado = false;

	/**
	 * Variable que permite informar si el proceso de diagnóstico ha finalizado.
	 * Si el proceso finalizo es {@code true}, de lo contrario {@code false}.
	 * 
	 * @author grasotos
	 */
	public boolean finalizo = false;

	/**
	 * {@code TextView} para mostrar los mensajes informativos del proceso de
	 * diagnóstico del huellero.
	 * 
	 * @author grasotos
	 */
	public TextView textMensajeHuellas;

	/**
	 * {@code TextView} para mostrar los mensajes informativos del proceso de
	 * diagnóstico de la cámara.
	 * 
	 * @author grasotos
	 */
	public TextView textMensajeCamara;

	/**
	 * {@code TextView} para mostrar los mensajes informativos del proceso de
	 * diagnóstico de la red inalámbrica.
	 * 
	 * @author grasotos
	 */
	public static TextView textMensajeRedInalambrica;

	/**
	 * {@code TextView} para mostrar los mensajes informativos del proceso de
	 * diagnóstico de la impresora.
	 * 
	 * @author grasotos
	 */
	public TextView textMensajeImpresora;

	/**
	 * {@code ImageView} para mostrar la imagen informativa del proceso de
	 * diagnóstico del huellero.
	 * 
	 * @author grasotos
	 */
	public ImageView imagenHuellas;

	/**
	 * {@code ImageView} para mostrar la imagen informativa del proceso de
	 * diagnóstico del lector 2D.
	 * 
	 * @author grasotos
	 */
	public ImageView imagenCamara;

	/**
	 * {@code ImageView} para mostrar la imagen informativa del proceso de
	 * diagnóstico de la red inalámbrica.
	 * 
	 * @author grasotos
	 */
	public static ImageView imagenRedInalambrica;

	/**
	 * {@code ImageView} para mostrar la imagen informativa del proceso de
	 * diagnóstico de la impresora.
	 * 
	 * @author grasotos
	 */
	public ImageView imagenImpresora;

	/**
	 * {@code TableRow} para mostrar el borde con color cambiante para las
	 * huellas.
	 * 
	 * @author grasotos
	 */
	public TableRow bordeHuellas;

	/**
	 * {@code TableRow} para mostrar el borde con color cambiante para la
	 * cámara.
	 * 
	 * @author grasotos
	 */
	public TableRow bordeCamara;

	/**
	 * {@code TableRow} para mostrar el borde con color cambiante para la red
	 * inalámbrica.
	 * 
	 * @author grasotos
	 */
	public static TableRow bordeRedInalambrica;

	/**
	 * {@code TableRow} para mostrar el borde con color cambiante para la
	 * impresora.
	 * 
	 * @author grasotos
	 */
	public TableRow bordeImpresora;

	/**
	 * Variable para definir el tiempo de espera del timer ente cada ejecución
	 * de los diagnósticos.
	 * 
	 * @author grasotos
	 */
	private int tiempo;

	/**
	 * Permite mostrar al usuario la espera mientras se realiza la comprobación
	 * de la impresora y la red inalámbrica.
	 * 
	 * @author grasotos
	 */
	private ProgressDialog dialogoProceso;

	/**
	 * IP de la configuracion cuando es local.
	 * 
	 * @author grasotos
	 */
	private final String IP_0 = "0";

	/**
	 * Resultado cuando proviene de actividad externa.
	 * 
	 * @author grasotos
	 */
	private final int RESULTADO_ACTIVIDAD = 1;

	/**
	 * Valor cuando resultado proviene de la camara.
	 * 
	 * @author grasotos
	 */
	private final int RESULTADO_HUELLERO = 2;

	/**
	 * Valor cuando la cámara se cancela.
	 * 
	 * @author grasotos
	 */
	private final int CAMARA_CANCELADA = 0;

	/**
	 * Posicion del código de respuesta del huellero
	 * 
	 * @author grasotos
	 */
	private final int POS_COD_HUELLERO = 0;

	/**
	 * Permite mostrar al usuario la espera mientras se realiza la comprobación
	 * de la impresora y la red inalámbrica.
	 * 
	 * @author grasotos
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_diagnostico_layout);
		try {
			((TextView)findViewById(R.id.lbl_fecha_diag)).setText(Util.obtenerFecha());
			procesoCancelado=false;
			tiempo = getResources().getInteger(R.integer.timer);
			
			iniciarlizarInterfaz();
			iniciarDiagnostico();
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:onCreate:", e);
		}

	}

	/**
	 * Permite inicializar la interfaz gráfica del layout
	 * activity_diagnostico_layout para el diagnóstico de la aplicación.
	 * 
	 * @author grasotos
	 */
	public void iniciarlizarInterfaz() {
		try {

			textMensajeHuellas = (TextView) findViewById(R.id.textViewMsjHuellas);
			textMensajeCamara = (TextView) findViewById(R.id.textViewMsjCamara);
			textMensajeRedInalambrica = (TextView) findViewById(R.id.textViewMsjWifi);
			textMensajeImpresora = (TextView) findViewById(R.id.textViewMsjImpresora);

			imagenHuellas = (ImageView) findViewById(R.id.imageViewHuellas);
			imagenCamara = (ImageView) findViewById(R.id.imageViewCamara);
			imagenRedInalambrica = (ImageView) findViewById(R.id.imageViewWifi);
			imagenImpresora = (ImageView) findViewById(R.id.imageViewImpresora);

			bordeHuellas = (TableRow) findViewById(R.id.tableRowHuellas);
			bordeCamara = (TableRow) findViewById(R.id.tableRowCamara);
			bordeRedInalambrica = (TableRow) findViewById(R.id.tableRowRedInalambrica);
			bordeImpresora = (TableRow) findViewById(R.id.tableRowImpresora);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:iniciarlizarInterfaz:", e);
		}

	}

	/**
	 * Inicia el proceso de diagnóstico llamando al diagnóstico del huellero.
	 * Valida si el proceso ha sido cancelado por el usuario.
	 * 
	 * @author grasotos
	 */
	public void iniciarDiagnostico() {
		try {
			if (!procesoCancelado) {
				iniciarDiagnosticoHuellero();
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:iniciarlizarInterfaz:", e);
		}
	}

	/**
	 * Se ejecuta la tarea cronometrada del diagnóstico del huellero.
	 * 
	 * @author grasotos
	 */
	private void iniciarDiagnosticoHuellero() {
		try {
			final Handler HuellaHandler = new Handler();
			huellaTask = new TimerTask() {
				@Override
				public void run() {
					HuellaHandler.post(new Runnable() {
						public void run() {
							if (!procesoCancelado) {
								diagnosticoHuellero();
							}
						}
					});
				}
			};
			ejecutarTimer(huellaTask, tiempo);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:iniciarDiagnosticoHuellero:", e);
		}
	}

	/**
	 * Permite llamar el ProcessActivity donde se realiza el diagnóstico del
	 * huellero y se realizan los cambios de mensajes e imágenes dependiendo del
	 * resultado obtenido.
	 * 
	 * @author grasotos
	 * 
	 * */
	private void diagnosticoHuellero() {

		try {

			if (AutenticacionActivity.huelleroConectado) {
				if (!procesoCancelado) {

					Intent processIntent = new Intent(this, ProcessActivity.class);
					processIntent.putExtra(getString(R.string.intent_huella), getString(R.string.intent_diagnostico));
					startActivityForResult(processIntent, 1);

				}
			} else {
				if (!procesoCancelado) {
					String mensajeE31 = getResources().getString(R.string.M31);
					mensajeHuellas(mensajeE31, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
					Util.logsDiagnostico(getString(R.string.huellero), mensajeE31);
					iniciarDiagnosticoCamara();

				}
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoHuellero:", e);
			String mensajeE31 = getResources().getString(R.string.M31);
			mensajeHuellas(mensajeE31, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.huellero), mensajeE31);
			iniciarDiagnosticoCamara();
		}

	}

	/**
	 * Se ejecuta la tarea cronometrada del diagnóstico del lector 2D.
	 * 
	 * @author grasotos
	 */
	public void iniciarDiagnosticoCamara() {
		try {
			final Handler CamaraHandler = new Handler();
			camaraTask = new TimerTask() {
				@Override
				public void run() {
					CamaraHandler.post(new Runnable() {
						public void run() {
							if (!procesoCancelado) {
								diagnosticoCamara();
							}
						}
					});
				}
			};
			ejecutarTimer(camaraTask, tiempo);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:iniciarDiagnosticoCamara:", e);

		}
	}

	/**
	 * En este método se realiza la prueba de diagnóstico del lector 2D. Lectura
	 * 2D.
	 * 
	 * @author grasotos
	 */
	public void diagnosticoCamara() {
		try {
			if (!procesoCancelado) {
				ActivityCapture.userLicence = getResources().getString(R.string.licence_user_manatee);
				ActivityCapture.keyLicence = getResources().getString(R.string.licence_key_manatee);

				Intent intent = new Intent(this, ActivityCapture.class);
				startActivityForResult(intent, 1);
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoCamara:", e);
			String mensajeE31 = getResources().getString(R.string.M31);
			mensajeCamara(mensajeE31, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.camara), mensajeE31);
		}
	}

	/**
	 * Obtiene los resultados de las actividades lanzadas en modo
	 * startActivityForResult. - 1 : Lector 2D ok 0 : Lector 2D cancelado por el
	 * usuario. 2 : huellero , si el huellero arroja -19 es debido a error de
	 * tiempo, se encuentra configurado en 4 segundos.
	 * 
	 * @author grasotos
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULTADO_ACTIVIDAD) { // Resultado exitoso
			if (resultCode == RESULT_OK) {

				mensajeCamara("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
				Util.logsDiagnostico(getString(R.string.camara), getString(R.string.dispositivo_ok));
				iniciarDiagnosticoRedInalambrica();

			} else if (resultCode == RESULTADO_HUELLERO) { // el resultado viene
															// de la
				// lectura de

				int codigo = data.getIntExtra(getString(R.string.id_codigo), POS_COD_HUELLERO);

				if (codigo == ErrorCodes.MORPHOERR_TIMEOUT) {

					String mensajeM17 = getResources().getString(R.string.M17);
					mensajeHuellas(mensajeM17, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
					Util.logsDiagnostico(getString(R.string.huellero), mensajeM17);
					iniciarDiagnosticoCamara();
				} else if (codigo == ErrorCodes.MORPHO_OK || codigo == ErrorCodes.MORPHOERR_NO_HIT) {

					mensajeHuellas("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
					Util.logsDiagnostico(getString(R.string.huellero), getString(R.string.dispositivo_ok));
					iniciarDiagnosticoCamara();
				} else if (codigo == 3) {

					String mensajeM35 = getString(R.string.M35);
					mensajeHuellas(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));

				} else {
					String mensajeM34 = getString(R.string.M34);
					mensajeHuellas(mensajeM34, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
					Util.logsDiagnostico(getString(R.string.huellero), mensajeM34);
					iniciarDiagnosticoCamara();
				}

			} else if (resultCode == CAMARA_CANCELADA) {
				// Cámara cancelada
				String mensajeM35 = getResources().getString(R.string.M35);
				mensajeCamara(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				Util.logsDiagnostico(getString(R.string.camara), mensajeM35);
				iniciarDiagnosticoRedInalambrica();

			}

		}

	}

	/**
	 * Se ejecuta la tarea cronometrada del diagnóstico de la red inalámbrica.
	 * 
	 * @author grasotos
	 */
	private void iniciarDiagnosticoRedInalambrica() {

		// En la interfaz de escribe que ha sido exitoso el diagnóstico
		// de Camara 2D
		// label_camara.setText("Camara OK");
		// SE EJECUTA LA TAREA CRONOMETRADA DE DIAGNÓSTICO DE RED
		// INALÁMBRICA E IMPRESORA
		// este handler nos permite saber el estado 'post' de la tareas,
		// con el fin de
		// editar la interfaz gráfica de usuario.
		final Handler ConexionHandler = new Handler();
		// Se inicializa y define el timerTask de conexión de red
		conexionTask = new TimerTask() {
			@Override
			public void run() {// Método que se ejecuta al correr la
								// tarea
				ConexionHandler.post(new Runnable() {
					public void run() {
						// Se ejecuta el método de diagnóstico de
						// conexión de red inalámbrica

						if (!procesoCancelado) {
							diagnosticoRedInalambrica();// Si existe
						} // conexión

					}
				});
			}
		};

		cuadroDialogo(getString(R.string.diag_red), getString(R.string.titulo_dialogos));
		ejecutarTimer(conexionTask, tiempo);
	}

	/**
	 * Este método tipo booleano permite hacer el diagnóstico de conexión de red
	 * inalámbrica.
	 * 
	 * 1. Comprobar que el dispositivo esté encendido. Si esta prueba no es
	 * exitosa entonces el sistema debe presentar el mensaje (M31 - Se ha
	 * detectado error de conexión).
	 * 
	 * 2. Comprobar que el dispositivo pueda conectarse con una red inalámbrica
	 * cualquiera, esta prueba aplica aunque la tableta esté configurada para
	 * trabajar sin conexión a un servidor, es decir de manera Local. Si esta
	 * prueba no es exitosa el sistema debe presentar el mensaje (M47- Aún no se
	 * ha conectado a una red inalámbrica).
	 * 
	 * 3. En caso que la tableta tenga un servidor configurado el sistema debe
	 * comprobar que la conexión se esté dando correctamente. Si no se logra la
	 * conexión con el servidor el sistema debe presentar el mensaje (M44 - No
	 * se pudo establecer comunicación con [nombre del servidor]. Por favor
	 * contacte al supervisor.).
	 * 
	 * 4. En caso que la tableta tenga un servidor configurado el sistema debe
	 * enviar hacia el peticiones. Si no se recibe respuesta del servidor el
	 * sistema debe presentar al usuario el mensaje informativo. (M48 - No se
	 * recibió respuesta del servidor[nombre del servidor]. Por favor contacte
	 * al supervisor).
	 * 
	 * @author grasotos
	 * 
	 */
	public void diagnosticoRedInalambrica() {

		try {

			if (!procesoCancelado) {
				WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

				if (wifiManager.isWifiEnabled()) {

					ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

					NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if (!procesoCancelado) {

						if (mWifi.isConnected()) {

							if (!procesoCancelado) {
								if (AutenticacionActivity.configuracionBL != null) {

									Configuracion configuracion = AutenticacionActivity.configuracionBL.obtenerConfiguracionActiva();

									if (configuracion == null) {

										mensajeWifi("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
										Util.logsDiagnostico(getString(R.string.red_inalambrica), getString(R.string.dispositivo_ok));

									} else if (configuracion.getIPServidor().equals(IP_0)) {
										/**
										 * Si la ip == 0 no debe hacer ping.
										 */
										mensajeWifi("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
										Util.logsDiagnostico(getString(R.string.red_inalambrica), getString(R.string.dispositivo_ok));

									} else {
										if (!procesoCancelado) {
											/**
											 * Si tiene configuración y la ip !=
											 * 0 debe hacer ping.
											 */

											String url = configuracion.getIPServidor();

											String ip = Util.obtenerIpDeUrl(url);

											String nombreServidor = Util.obtenerNombreServidor(DiagnosticoActivity.this, configuracion.getIPServidor());

											if (Util.verificarConexionIpLocal(ip, getResources().getString(R.integer.sync_time_out_ping))) {

												DiagnosticServerTestAsyncTask diagnosticServerTestAsyncTask = new DiagnosticServerTestAsyncTask(this, nombreServidor);

												final AsyncTask<Void, Void, Void> pruebaConexion = diagnosticServerTestAsyncTask.execute();
												Void cnx = pruebaConexion.get();

											} else {
												/**
												 * No prueba el servicio.
												 */

												String mensajeM44 = getResources().getString(R.string.M44, nombreServidor);

												mensajeWifi(mensajeM44, getResources().getDrawable(R.drawable.ic_error),
														getResources().getDrawable(R.drawable.borde_redondeado_rojo));

												Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM44);

											}
										}
									}
								} else {
									mensajeWifi("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
									Util.logsDiagnostico(getString(R.string.red_inalambrica), getString(R.string.dispositivo_ok));

								}
							}

						} else {

							/**
							 * No existe conexión de red inalámbrica activa
							 * retornamos diagnóstico fallido.
							 */
							String mensajeM47 = getResources().getString(R.string.M47);
							mensajeWifi(mensajeM47, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
							Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM47);

						}
					}
				} else {
					/**
					 * No está encendido el wifi.
					 */
					String mensajeM31 = getResources().getString(R.string.M31);
					mensajeWifi(mensajeM31, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
					Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM31);

				}
			}
		} catch (ConfiguracionBLException e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoRedInalambrica:", e);
			String mensajeM34 = getResources().getString(R.string.M34);
			mensajeWifi(mensajeM34, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM34);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoRedInalambrica:", e);
			String mensajeM34 = getResources().getString(R.string.M34);
			mensajeWifi(mensajeM34, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM34);
		} finally {
			dialogoProceso.dismiss();
			if (!procesoCancelado) {
				iniciarDiagnosticoConexionImpresora();
			}
		}

	}

	/**
	 * Se ejecuta la tarea cronometrada del diagnóstico de impresora.
	 * 
	 * @author grasotos
	 */
	private void iniciarDiagnosticoConexionImpresora() {
		final Handler ImpresoraHandler = new Handler();
		impresoraTask = new TimerTask() {
			@Override
			public void run() {
				ImpresoraHandler.post(new Runnable() {
					public void run() {
						diagnosticoConexionImpresora();
					}
				});
			}
		};
		cuadroDialogo(getString(R.string.diag_impr), getString(R.string.titulo_dialogos));

		ejecutarTimer(impresoraTask, tiempo);
	}

	/**
	 * Este método es utilizado para hacer el diagnóstico de la conexión entre
	 * la tableta y la impresora conectada a través de un periférico. Después de
	 * ejecutar dicho diagnóstic se imprime un comprobante de prueba.
	 * 
	 * @author grasotos
	 */
	private void diagnosticoConexionImpresora() {

		try {
			posSdkImpresora = POSSDKManager.getInstance(DiagnosticoActivity.this);

			if (posSdkImpresora.imprimirComprobante(getString(R.string.titulo_comprobante_prueba), getString(R.string.texto_prueba), getString(R.string.texto_prueba),
					getString(R.string.texto_prueba), false, false)) {

				mensajeImpresora("", getResources().getDrawable(R.drawable.ic_correcto), getResources().getDrawable(R.drawable.borde_redondeado_verde));
				Util.logsDiagnostico(getString(R.string.impresora), getString(R.string.dispositivo_ok));

			} else {
				String mensajeM31 = getResources().getString(R.string.M31);
				mensajeImpresora(mensajeM31, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
				Util.logsDiagnostico(getString(R.string.impresora), mensajeM31);

			}
		} catch (POSSDKManagerException e) { // Excepción no

			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoConexionImpresora:", e);
			String mensajeM31 = getResources().getString(R.string.M31);
			mensajeImpresora(getResources().getString(R.string.M31), getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.impresora), mensajeM31);

		} catch (Exception e) {
			String mensajeM34 = getResources().getString(R.string.M34);
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoConexionImpresora:", e);
			mensajeImpresora(mensajeM34, getResources().getDrawable(R.drawable.ic_error), getResources().getDrawable(R.drawable.borde_redondeado_rojo));
			Util.logsDiagnostico(getString(R.string.impresora), mensajeM34);

		} finally {
			try {
				finalizo = true;
				findViewById(R.id.btnCancelarDiagnostico).setVisibility(View.GONE);
				dialogoProceso.dismiss();
				((TextView) findViewById(R.id.textViewMensaje)).setText(getString(R.string.M36));
			} catch (Exception e) {
				log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:diagnosticoConexionImpresora:finally:", e);

			}
		}

	}

	/**
	 * Este método se llama cuando hacemos click en el botón cancelar
	 * diagnóstico. aquí cancelamos todos los diagnósticos que se estén
	 * ejecutando.
	 * 
	 * @param view
	 *            del botón asignado.
	 * @author grasotos
	 */
	public void btnCancelarDiagnostico_Click(View view) {
		cancelarDiagnostico();

	}

	/**
	 * Permite modificar la imagen y los mensajes relacionados con el
	 * diagnóstico del huellero.
	 * 
	 * @param mensaje
	 *            a mostrar.
	 * @param icono
	 *            a mostrar.
	 * @param borde
	 *            con color dependiendo del resultado.
	 * @author grasotos
	 */
	public void mensajeHuellas(String mensaje, Drawable icono, Drawable borde) {
		try {
			textMensajeHuellas.setText(mensaje);
			imagenHuellas.setVisibility(View.VISIBLE);
			imagenHuellas.setImageDrawable(icono);
			bordeHuellas.setBackground(borde);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:mensajeHuellas:", e);
		}

	}

	/**
	 * Permite modificar la imagen y los mensajes relacionados con el
	 * diagnóstico del wifi.
	 * 
	 * @param mensaje
	 *            a mostrar.
	 * @param icono
	 *            a mostrar.
	 * @param borde
	 *            con color dependiendo del resultado.
	 * @author grasotos
	 */
	public static void mensajeWifi(String mensaje, Drawable icono, Drawable borde) {
		try {
			textMensajeRedInalambrica.setText(mensaje);
			imagenRedInalambrica.setImageDrawable(icono);
			imagenRedInalambrica.setVisibility(View.VISIBLE);
			bordeRedInalambrica.setBackground(borde);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:mensajeHuellas:", e);

		}

	}

	/**
	 * Permite modificar la imagen y los mensajes relacionados con el
	 * diagnóstico de la impresora.
	 * 
	 * @param mensaje
	 *            a mostrar.
	 * @param icono
	 *            a mostrar.
	 * @param borde
	 *            con color dependiendo del resultado.
	 * @author grasotos
	 */
	public void mensajeCamara(String mensaje, Drawable icono, Drawable borde) {
		try {
			textMensajeCamara.setText(mensaje);
			imagenCamara.setImageDrawable(icono);
			imagenCamara.setVisibility(View.VISIBLE);
			bordeCamara.setBackground(borde);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:mensajeCamara:", e);
		}
	}

	/**
	 * Permite modificar la imagen y los mensajes relacionados con el
	 * diagnóstico de la impresora.
	 * 
	 * @param mensaje
	 *            a mostrar.
	 * @param icono
	 *            a mostrar.
	 * @author grasotos
	 */
	public void mensajeImpresora(String mensaje, Drawable icono, Drawable borde) {
		try {
			textMensajeImpresora.setText(mensaje);
			imagenImpresora.setImageDrawable(icono);
			imagenImpresora.setVisibility(View.VISIBLE);
			bordeImpresora.setBackground(borde);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:mensajeImpresora:", e);

		}

	}

	/**
	 * Permite volver al menú de configuración. Si el proceso ha sido finalizado
	 * o cancelado por el usuario, el permite ejecutar la acción, de lo
	 * contrario no permite regresar a la pantalla anterior.
	 * 
	 * @author grasotos
	 */
	@Override
	public void onBackPressed() {

		if (finalizo) {
			DiagnosticoActivity.this.finish();
			super.onBackPressed();
		}

	}

	/**
	 * Método para cancelar en cascada el diagnóstico de los dispositivos. Se
	 * debe preguntar cual TimerTask se está ejecutando, para ello se sabe que
	 * las tareas se ejecutan secuencialmente de esta forma: 1. Huellero 2.
	 * Cámara 2D 3. Conexión Inalámbrica 4. Impresora.
	 * 
	 * @author grasotos
	 */
	public void cancelarDiagnostico() {
		try {

			((TextView) findViewById(R.id.textViewMensaje)).setText("");
			String mensajeM35 = getString(R.string.M35);

			procesoCancelado = true;

			if ((huellaTask == null) || (huellaTask != null) && (camaraTask == null)) {
				if (huellaTask != null) {
					huellaTask.cancel();
				}
				mensajeHuellas(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeCamara(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeWifi(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeImpresora(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));

				Util.logsDiagnostico(getString(R.string.huellero), mensajeM35);
				Util.logsDiagnostico(getString(R.string.camara), mensajeM35);
				Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM35);
				Util.logsDiagnostico(getString(R.string.impresora), mensajeM35);

			} else if ((camaraTask != null) && (conexionTask == null)) {
				camaraTask.cancel();

				mensajeCamara(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeWifi(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeImpresora(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));

				Util.logsDiagnostico(getString(R.string.camara), mensajeM35);
				Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM35);
				Util.logsDiagnostico(getString(R.string.impresora), mensajeM35);

			} else if ((conexionTask != null) && (impresoraTask == null)) {
				conexionTask.cancel();

				mensajeWifi(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));
				mensajeImpresora(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));

				Util.logsDiagnostico(getString(R.string.red_inalambrica), mensajeM35);
				Util.logsDiagnostico(getString(R.string.impresora), mensajeM35);

			} else if ((impresoraTask != null)) {
				impresoraTask.cancel();

				mensajeImpresora(mensajeM35, getResources().getDrawable(R.drawable.ic_advertencia), getResources().getDrawable(R.drawable.borde_redondeado_morado));

				Util.logsDiagnostico(getString(R.string.impresora), mensajeM35);
			}

			findViewById(R.id.btnCancelarDiagnostico).setVisibility(View.GONE);
			finalizo = true;

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:cancelarDiagnostico:", e);
		}
	}

	/**
	 * Permite ejecutar los TimerTask en un tiempo determinado.
	 * 
	 * @param timerTask
	 *            a ejecutar.
	 * @param tiempo
	 *            de espera para ejecutar el timertask.
	 * @author grasotos
	 */
	private void ejecutarTimer(TimerTask timerTask, int tiempo) {
		try {
			Timer timer = new Timer();
			timer.schedule(timerTask, tiempo);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:ejecutarTimer:", e);
		}
	}

	/**
	 * Permite generar el cuadro de dialogo de progreso para el diagnóstico de
	 * la impresora y la red inalámbrica.
	 * 
	 * @param mensaje
	 *            que se mostrará en el cuadro de diálogo.
	 * @param titulo
	 *            del cuadro de diálogo.
	 * @author grasotos
	 */
	private void cuadroDialogo(String mensaje, String titulo) {
		try {
			dialogoProceso = new ProgressDialog(DiagnosticoActivity.this, R.style.TemaDialogo);
			dialogoProceso.setMessage(mensaje);
			dialogoProceso.setTitle(titulo);
			dialogoProceso.setCancelable(false);
			dialogoProceso.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					cancelarDiagnostico();

				}
			});
			dialogoProceso.show();

			Util.cambiarLineaDialogos(dialogoProceso, getApplicationContext());

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:DiagnosticoActivity:cuadroDialogo:", e);

		}
	}

}
