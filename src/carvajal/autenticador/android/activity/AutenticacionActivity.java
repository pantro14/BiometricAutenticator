package carvajal.autenticador.android.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import carvajal.autenticador.android.activity.dialogo.AutorizaDelegadoDialogFragment;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.CensoBL;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.ProvinciasBL;
import carvajal.autenticador.android.bl.MunicipiosBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.ColegiosElectoralesBL;
import carvajal.autenticador.android.bl.TemplatesBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.bl.exception.CensoBLException;
import carvajal.autenticador.android.bl.exception.ProvinciasBLException;
import carvajal.autenticador.android.bl.exception.MunicipiosBLException;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.bl.exception.ColegiosElectoralesBLException;
import carvajal.autenticador.android.bl.exception.TemplatesBLException;
import carvajal.autenticador.android.code2D.ActivityCapture;
import carvajal.autenticador.android.dal.greendao.read.Censo;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectorales;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.read.Municipios;
import carvajal.autenticador.android.dal.greendao.read.Provincias;
import carvajal.autenticador.android.dal.greendao.read.Templates;
import carvajal.autenticador.android.dal.greendao.write.Novedades;
import carvajal.autenticador.android.framework.morphosmart.ConexionHuellero;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.exception.MorphoSmartException;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.wrappers.AutResponseSyncWrapper;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;

import com.imobile.thermalprinterwifiswitch.EncendidoApagadoDispositivos;
import com.morpho.morphosmart.sdk.ErrorCodes;

/**
 * Pantalla principal de la aplicación, permite la lectura del número de cédula,
 * consulta en base de datos y acceso a verificación de huellas.
 * 
 * @author
 * 
 */
public class AutenticacionActivity extends AutenticacionInfoActivity {

	/**
	 * Objetos tipo TextView y EditText de esta interfaz.
	 */
	private TextView lblFecha;
	/**
	 * Campo para el porcentaje de sincronización
	 */
	private static TextView lblSincronizado;
	/**
	 * Campo de texto para el número de cédula.
	 */
	private static EditText numCedula;
	/**
	 * Label para mostrar la cédula del elector encontrado en la base de datos.
	 */
	private TextView elector_cedula;
	/**
	 * Label para mostrar la fecha de expedición de la cédula del elector
	 * encontrado en la base de datos.
	 */
	private TextView elector_fechaEx;
	/**
	 * Label para mostrar los nombres del elector encontrado en la base de
	 * datos.
	 */
	private TextView elector_nombres;
	/**
	 * Label para mostrar los apellidos del elector encontrado en la base de
	 * datos.
	 */
	private TextView elector_apellidos;
	/**
	 * Label para mostrar si elector encontrado en la base de datos es jurado
	 * "Si" o "No".
	 */
	// private TextView elector_isjurado;
	private static ImageView img_jurado;

	public static TextView lblConexion;

	/**
	 * Label para mostrar el nombre del departamento configurado en el
	 * dispositivo.
	 */
	private TextView lblDpto;
	/**
	 * Label para mostrar el nombre del municipio configurado en el dispositivo.
	 */
	private TextView lblMpio;
	/**
	 * Label para mostrar el nombre del puesto configurado en el dispositivo.
	 */
	private TextView lblPuesto;
	/**
	 * Button para acceder a las opciones de configuración de la aplicación.
	 */
	private Button botonConfig;
	/**
	 * Button para acceder a la impresión de Reporte de Autenticación.
	 */
	private Button botonReporte;

	/**
	 * Permite incluir en la pantalla los labels para mostrar los datos del
	 * elector.
	 */
	private static View includeElector;
	/**
	 * Permite incluir en la pantalla las opciones para registrar coincidencia o
	 * no de los datos encontrados en la base de datos con los de la cédula
	 * física.
	 */
	private static View includeCoinciden;

	/**
	 * Flag para verificar si el huellero se encuentra conectado.
	 */
	public static boolean huelleroConectado;

	/**
	 * Censo consultado de base de datos, este aplicará transversalmente en la
	 * aplicación.
	 */
	public static Censo censo = null;
	/**
	 * Novedad para validar la autenticacion ya realizada.
	 */
	public static Novedades novedades = null;

	/**
	 * TemplateBL para acceder a validar si el elector tiene huellas en la base
	 * de datos.
	 */
	public TemplatesBL templatesBL = null;

	/**
	 * NovedadesBL para acceder a los métodos de la tabla Novedades
	 */
	public NovedadesBL novedadesBL = null;

	/**
	 * ConfiguracionBL para acceder a los métodos de la tabla Configuracion
	 */
	public static ConfiguracionBL configuracionBL;

	/**
	 * DepartamentosBL para acceder a los métodos de la tabla Provincias
	 */
	public static ProvinciasBL provinciasBL;

	/**
	 * PuestosBL para acceder a los métodos de la tabla Provincias
	 */
	public static ColegiosElectoralesBL puestosBL;

	/**
	 * MunicipiosBL para acceder a los métodos de la tabla Provincias
	 */
	public static MunicipiosBL municipiosBL;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	/**
	 * Instancia de la impresora.
	 */
	private POSSDKManager possdkImprimir;

	/**
	 * Instancia del wrapper que recibe la respuesta del servicio que verifica
	 * que un elector esté autenticado, y el tipo de autenticaciones que este
	 * tiene registradas.
	 */
	AutResponseSyncWrapper autResponseSyncWrapper;

	private ProgressDialog pdServerWait;

	public static Context contexto;

	private static ImageView imagenElector;

	/**
	 * OnCreate para inicializar la interfaz de la pantalla
	 * activity_autenticacion e inicialización del hueller e impresora.
	 * 
	 * @author grasotos
	 * @date 24-Feb-2015
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_autenticacion);
		pdServerWait = new ProgressDialog(this, R.style.TemaDialogo);
		// ServerTestAsyncTask testAsyncTask = new ServerTestAsyncTask(
		// AutenticacionActivity.this.getApplicationContext());

		try {
			if (validarConfiguracion()) {
				novedadesBL = new NovedadesBL(getApplicationContext());
				provinciasBL = new ProvinciasBL(getApplicationContext());
				municipiosBL = new MunicipiosBL(getApplicationContext());
				puestosBL = new ColegiosElectoralesBL(getApplicationContext());
				// testAsyncTask.execute();
			}

			contexto = getApplicationContext();
			inicializarInterfaz();
			encendidoApagadoDispositivos = new EncendidoApagadoDispositivos(
					getApplicationContext().getResources());

			// huelleroConectado = ConexionHuellero.conectarHuellero(this,
			// AutenticacionActivity.this);
			conexionImpresora();
			huelleroConectado = ConexionHuellero.conectarHuellero(this);
			templatesBL = new TemplatesBL(AutenticacionActivity.this);

		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onCreate:",
							e);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onCreate:",
							e);
		}

	}

	/**
	 * Permite inicializar los componentes de la interfaz gráfica. Esconde el
	 * includeElector y el includeCoinciden. En caso de ser perfil Supervisor el
	 * botón configurar no se muestra.
	 * 
	 * @author grasotos
	 * @date 24-Feb-2015
	 */
	public void inicializarInterfaz() {
		verficarCensoBD();
		try {
			lblFecha = (TextView) findViewById(R.id.lbl_fecha);
			lblFecha.setText(Util.obtenerFecha());
			lblSincronizado = (TextView) findViewById(R.id.lbl_sincronizado);
			lblConexion = (TextView) findViewById(R.id.lbl_conexion);
			numCedula = (EditText) findViewById(R.id.txtCedula);
			includeElector = findViewById(R.id.includeElector);
			includeCoinciden = findViewById(R.id.includeCoinciden);
			includeElector.setVisibility(View.GONE);
			includeCoinciden.setVisibility(View.GONE);
			lblDpto = (TextView) findViewById(R.id.textViewVarDpto);
			lblMpio = (TextView) findViewById(R.id.textViewVarMpio);
			lblPuesto = (TextView) findViewById(R.id.textViewVarPuesto);
			botonConfig = (Button) findViewById(R.id.btnConfigurar);
			botonReporte = (Button) findViewById(R.id.btnReporte);
			findViewById(R.id.textViewHoraReg).setVisibility(View.GONE);
			findViewById(R.id.textViewNoMesa).setVisibility(View.GONE);
			findViewById(R.id.textViewVarHoraReg).setVisibility(View.GONE);
			findViewById(R.id.textViewVarNoMesa).setVisibility(View.GONE);
			img_jurado = (ImageView) findViewById(R.id.imgJurado);
			img_jurado.setVisibility(View.GONE);
			imagenElector = (ImageView) findViewById(R.id.fotoElector);
			imagenElector.setVisibility(View.GONE);
			// TODO:grasotos Se modifica la visibilidad del botón configuracion
			// if (getIntent().getStringExtra(getString(R.string.id_perfil))
			// .equals(getString(R.string.rol_supervisor))) {
			// botonConfig.setVisibility(View.VISIBLE);
			// }

			mostrarInfoLugarAutenticacion();
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:inicializarInterfaz:",
							e);
		}

	}

	/**
	 * Este evento es cuando se presiona el boton atras de android.
	 */

	@Override
	public void onBackPressed() {
		try {
			this.openAlertExit();
			// super.onBackPressed();
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onBackPressed:",
							e);
		}
	}

	/**
	 * Metodo encargado de la logica de motsrar un dialogo al usuario,
	 * preguntando si se desea salir de la app Se usa la clase Util, para armar
	 * el objeto AlertDialog y preguntar al usuario.
	 * 
	 * @author johgrame
	 * @since 18-02-2015
	 */
	private void openAlertExit() {
		try {
			AlertDialog cuadDialogo = Util.mensajeConfirmacion(this,
					R.style.TemaDialogo,
					getResources().getText(R.string.msg_tittle_salir_app)
							.toString(),
					getResources().getText(R.string.msg_salir_app).toString(),
					Util.DIALOG_INFORMATIVO,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Para salir de la aplicacion
							encendidoApagadoDispositivos.desconectar();
							finish();
						}
					}, null);

			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:openAlertExit:",
							e);
		}

	}

	/**
	 * Este metodo se desencadena, cuando se lanza el finish() de el activity
	 * actual.
	 */
	@Override
	protected void onDestroy() {
		try {
			// se realiza forzado de al aplicación para retornar al SO
			android.os.Process.killProcess(android.os.Process.myPid());
			super.onDestroy();
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onDestroy:",
							e);
		}
	}

	/**
	 * Metodo enlazado al boton Leer codigo 2D Desencadena evento para lanzar el
	 * activity encargado de la lectura 2D
	 * 
	 * @author johgrame
	 * @param v
	 *            Vista del activiy
	 */
	public void read2Dcode(View v) {
		verficarCensoBD();

		if (validarConfiguracion()) {
			// Se settean los paramatros para la ejecución correcta del Activty
			// encargado de la lectura 2D
			try {
				ActivityCapture.userLicence = getResources().getString(
						R.string.licence_user_manatee);
				ActivityCapture.keyLicence = getResources().getString(
						R.string.licence_key_manatee);
				// Se llama al activity encargado de la lectura por la camara
				Intent intent = new Intent(this, ActivityCapture.class);
				startActivityForResult(intent, 1);
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:read2Dcode:",
								e);
				AlertDialog cuadDialogo = Util.mensajeAceptar(this,
						R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M25), Util.DIALOG_ERROR, null);

				cuadDialogo.show();
				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
			}
		} else {
			mostrarMensajeConfiguracion();
		}

	}

	/**
	 * Este evento se produce al regresar de la pantalla de lectura del code 2D
	 * Se encarga de obtener la trama del valor leido del code 2D, apartir del
	 * IntentData Luego llama al metodo, de buscar el numero de la cedula.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			// este caso es para cuando retorna de la pantalla de captura del
			// code 2D
			if (requestCode == 1) {
				if (resultCode == RESULT_OK) {
					String resultadoLectura = Util.ObtenerCedulaDesdeTrama(data
							.getStringExtra("CEDULA"));
					if (!resultadoLectura.equals(Util.NO_CEDULA)) {
						numCedula.setText(resultadoLectura);
						consultarElector(numCedula.getText().toString());
					} else {
						numCedula.setText("");
						AlertDialog cuadDialogo = Util.mensajeAceptar(
								AutenticacionActivity.this,
								R.style.TemaDialogo,
								getString(R.string.title_activity_login),
								getString(R.string.M15),
								Util.DIALOG_ADVERTENCIA, null);
						cuadDialogo.show();
						Util.cambiarLineaDialogos(cuadDialogo,
								getApplicationContext());

					}

				}
				// este caso es para cuando retorna de la pantalla de la toma de
				// la huella
				if (resultCode == 2) {
					final int score = data.getIntExtra(
							getString(R.string.id_score), 0);
					int pk = data.getIntExtra(getString(R.string.id_dedo), 0);
					int codigo = data.getIntExtra(
							getString(R.string.id_codigo), 0);

					if (codigo == ErrorCodes.MORPHOERR_TIMEOUT) {

						// TODO: Novedad Tiempo
						novedadesBL.notificarNoPusoDedoParaValidacion(
								censo.getCedula(), censo.getCodProv(),
								censo.getCodMpio(), censo.getCodZona(),
								censo.getCodColElec(), censo.getCodMesa(),
								censo.getTipoElector());
						AlertDialog cuadDialogo = Util
								.mensajeConfirmacionPersonalizado(
										AutenticacionActivity.this,
										R.style.TemaDialogo,
										getString(R.string.title_activity_login),
										getString(R.string.M17),
										getString(R.string.label_reintentar),
										"Aceptar", Util.DIALOG_ERROR,
										clickListenerReintentar,
										clickListenerCancelar);
						cuadDialogo.show();
						Util.cambiarLineaDialogos(cuadDialogo,
								getApplicationContext());

					} else {

						if (codigo == ErrorCodes.MORPHO_OK) {

							final int idHuella = Util.obtenerIdHuella(
									listaIdsHuellas, pk);

							/*
							 * En este punto se debe realizar la sincronización
							 * de novedad 0: Autenticación con el servidor de
							 * centralización
							 * 
							 * Declaración e implementación de TAREA ASÍNCRONA
							 * InsetarNovedadAsyncTask Esta tarea asíncrona, va
							 * a ejecutar la insersión de novedadad 0:
							 * Autenticación Exitosa.
							 */
							AsyncTask<Void, Void, Void> InsetarNovedadAsyncTask = new AsyncTask<Void, Void, Void>() {
								ListaNovedadSyncWrapper listaNovedadesResponseWrapper;

								/*
								 * En el método implementado doInBackground se
								 * ejecuta el consumo de la función SetInsertNov
								 * en el servicio web, al finalizar esta
								 * consulta llenamos el wrapper
								 * ListaNovedadSyncWrapper
								 */
								@Override
								protected Void doInBackground(Void... params) {
									try {

										// Declaración e instancia del objeto
										// AutenticadorSyncBL
										AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
												AutenticacionActivity.this
														.getApplicationContext(),
												getResources()
														.getString(
																R.string.metodo_insertar_novedad));
										// Se asigna la MAC del dispositivo
										// móvil
										String androidId = Util
												.obtenerMAC(AutenticacionActivity.this
														.getApplicationContext());
										// Se asigna el tipo de novedad
										int tipoNovedad = Integer
												.parseInt(AutenticacionActivity.this
														.getApplicationContext()
														.getString(
																R.string.autenticado));
										// Asigno las variables a insertar en el
										// servidor central al wrapper
										// NovSyncWrapper
										NovSyncWrapper novedadSynWrapper = new NovSyncWrapper(
												androidId, censo.getCedula(),
												new Date(), score, idHuella,
												censo.getTipoElector(),
												tipoNovedad, 0,
												censo.getCodMesa());
										// Después este wrapper NovSyncWrapper,
										// se almacena en la lista de wrappers
										List<NovSyncWrapper> listanovedades = new ArrayList<NovSyncWrapper>();
										listanovedades.add(novedadSynWrapper);
										// La lista de wrappers de
										// NovSyncWrapper, se almacena en el
										// wrapper ListaNovedadSyncWrapper
										ListaNovedadSyncWrapper listaNovedadesWrapper = new ListaNovedadSyncWrapper(
												listanovedades);
										// Se ejecuta la función que inserta las
										// novedades en el servidor central.
										listaNovedadesResponseWrapper = autSyncBL
												.insertaNovedad(listaNovedadesWrapper);

									} catch (AutenticadorSyncBLException e) {// Exepción
																				// no
																				// controlada
																				// de
																				// AutenticadorSyncBLException
										log4jDroid
												.error("AutenticadorAndroidProject:AutenticacionActivity:TestAsyncTask:",
														e);
									}
									return null;
								}

								/*
								 * ¨ El método onPostExecute, se ejecuta, solo
								 * cuando los procedimientos del método
								 * doInBackground han terminado
								 */
								@Override
								protected void onPostExecute(Void result) {
									int sincronizado = 0;
									if (listaNovedadesResponseWrapper != null) {
										for (NovSyncWrapper novSyncWrapper : listaNovedadesResponseWrapper
												.getListanovedades()) {

											if (novSyncWrapper.getRespuesta() == Integer
													.parseInt(getApplicationContext()
															.getString(
																	R.string.novedad_no_sincronizada))) {// No
																											// Sincronizado
												sincronizado = novSyncWrapper
														.getRespuesta();
												cambiarEstadoDesconectado(getApplicationContext());
											} else if (novSyncWrapper
													.getRespuesta() == Integer
													.parseInt(getApplicationContext()
															.getString(
																	R.string.novedad_sincronizada))) {// Sincronizado
												sincronizado = novSyncWrapper
														.getRespuesta();
												cambiarEstadoConectado(getApplicationContext());
											}
										}
									} else {
										cambiarEstadoDesconectado(getApplicationContext());
									}
									// (NOVEDAD AUTENTICACION) DESPUES DE
									// INTENTO DE SINCRONIZACIÓN
									try {
										if (novedadesBL.notificarAutenticado(
												censo.getCedula(),
												censo.getCodProv(),
												censo.getCodMpio(),
												censo.getCodZona(),
												censo.getCodColElec(),
												censo.getCodMesa(),
												censo.getTipoElector(),
												idHuella, score, sincronizado)) {
											actualizarPorcentajeSincro(getApplicationContext());
										}
									} catch (NovedadesBLException e) {
										log4jDroid
												.error("AutenticadorAndroidProject:InsetarNovedadAsyncTaskonPostExecute:onActivityResult:",
														e);
									}
									Intent intent = new Intent(
											AutenticacionActivity.this,
											NovedadAutenticacionActivity.class);
									intent.putExtra("aut", 1);
									startActivity(intent);
								}
							};
							/*
							 * Una vez terminado de declarar e implementar las
							 * funciones de la tarea asíncrona
							 * InsetarNovedadAsyncTask, a continuación se
							 * ejecuta dicha tarea, agregando como variable de
							 * entrada la cedula del elector a consultar, y
							 * terminaria el flujo de Autenticación en el método
							 * onPostExecute de esta tarea asíncrona.
							 */
							InsetarNovedadAsyncTask.execute();
						} else {
							// si no se puede autenticar
							// NOVEDAD(NO SE PUEDE AUTENTICAR)
							novedadesBL.notificarNoSePudoAutenticar(
									censo.getCedula(), censo.getCodProv(),
									censo.getCodMpio(), censo.getCodZona(),
									censo.getCodColElec(), censo.getCodMesa(),
									censo.getTipoElector(), score);
							// Util.mensajeAceptar(this,
							// getString(R.string.title_activity_login),
							// getString(R.string.M23), Util.DIALOG_ERROR,
							// clickListenerNoAuten).show();
							Intent intent = new Intent(
									AutenticacionActivity.this,
									NovedadAutenticacionActivity.class);
							intent.putExtra("noAut", 2);
							startActivity(intent);
						}
					}
				}
			}
		} catch (NovedadesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConexionHuellero:onActivityResult:",
							e);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConexionHuellero:onActivityResult:",
							e);
		}

	}

	/**
	 * Listener para reintentar el proceso de captura de huella.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public OnClickListener clickListenerReintentar = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				iniciarProcessActivity();
			} catch (MorphoSmartException e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:onClick:",
								e);

			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:onClick:",
								e);
			}
		}
	};

	/**
	 * Listener para iniciar actividad {@link NovedadAutenticacionActivity}
	 * cuando no se encuentra autenticado el elector.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public OnClickListener clickListenerNoAuten = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				Intent intent = new Intent(AutenticacionActivity.this,
						NovedadAutenticacionActivity.class);
				intent.putExtra("noAut", 2);
				startActivity(intent);
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:onClick:",
								e);
			}
		}
	};

	/**
	 * Listener para iniciar actividad {@link NovedadAutenticacionActivity}
	 * cuandoF se encuentra autenticado el elector.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public OnClickListener clickListenerAuten = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				Intent intent = new Intent(AutenticacionActivity.this,
						NovedadAutenticacionActivity.class);
				intent.putExtra("aut", 1);
				startActivity(intent);
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:clickListenerAuten:",
								e);
			}

		}
	};

	/**
	 * Para remover datos en el cancelar.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public OnClickListener clickListenerCancelar = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				removerDatos();
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:clickListenerCancelar:",
								e);
			}

		}
	};

	/*
	 * En este punto se debe verificar nuevamente las novedades pero en el
	 * servidor central
	 * 
	 * Declaración e implementación de TAREA ASÍNCRONA
	 * AutNovedadesByCedAsyncTask Esta tarea asíncrona, va a ejecutar la
	 * consulta de novedades a partir de la cédula de un elector.
	 * 
	 * Tiene como parámetro de entrada un String: Cedula, y variables finales de
	 * comunicación Void.
	 */
	private class ConsultarNovedadesByCedAsyncTask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			pdServerWait.setMessage("Consultando elector...");
			pdServerWait.setTitle("Autenticador Electorar");
			pdServerWait.setCancelable(false);
			pdServerWait.show();
			Util.cambiarLineaDialogos(pdServerWait, getApplicationContext());
		}

		/*
		 * En el método implementado doInBackground se ejecuta el consumo de la
		 * función GetNovedadesByCed en el servicio web, al finalizar esta
		 * consulta llenamos el wrapper autResponseSyncWrapper
		 */
		@Override
		protected Void doInBackground(String... cedula) {
			// Reiniciamos a null para ejecutar nueva
			// consulta
			autResponseSyncWrapper = null;
			try {
				// Declaración e instancia del objeto
				// AutenticadorSyncBL
				AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
						AutenticacionActivity.this.getApplicationContext(),
						getResources().getString(
								R.string.metodo_get_novedad_by_ced));
				// Ejecutamos la función en el servidor por
				// medio de la cédula
				autResponseSyncWrapper = autSyncBL
						.obtenerAutenticadoByCed(cedula[0]);

			} catch (AutenticadorSyncBLException e) {// Exepción
														// no
														// controlada
														// de
														// AutenticadorSyncBLException
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:TestAsyncTask:",
								e);
			}
			return null; // No se retorna nada al
							// onPostExecute
		}

		/*
		 * ¨ El método onPostExecute, se ejecuta, solo cuando los procedimientos
		 * del método doInBackground han terminado
		 */
		@Override
		protected void onPostExecute(Void result) {
			// verifica que el wrapper de respuesta para la
			// cedula autenticada sea nula
			if (autResponseSyncWrapper == null) {// SI es
													// nula
				updateFrameDatosElector(); // Sigue con el
											// flujo normal
											// de
											// autenticación
				cambiarEstadoDesconectado(getApplicationContext());
			} else {
				// Se captura el valor de autenticación de
				// la cédula
				int autenticado = autResponseSyncWrapper.getSync();
				if (autenticado == 2) { // Error en conexión
										// con base de datos
					updateFrameDatosElector();
					cambiarEstadoDesconectado(getApplicationContext());
				} else if (autenticado == 0) {// Si 0: No
					// Autenticado
					updateFrameDatosElector(); // Sigue con
												// el flujo
												// normal de
												// autenticación
					cambiarEstadoConectado(getApplicationContext());
					actualizarPorcentajeSincro(getApplicationContext());
				} else if (autenticado == 1) {// No,
												// entonces
												// 1: YA
												// Autenticado
					// YA ES AUTENTICADO se muestra la
					// pantalla 'Elector ya autenticado'

					/*
					 * Se asignan los datos de la novedad que están en el
					 * servidor en el objeto local de novedad. Para esto se hace
					 * un bucle sobre la Lista de Novedades que pertenen al
					 * elector consultado, obteniendo al final la novedad de
					 * tipo 0: Autenticado
					 */
					cambiarEstadoConectado(getApplicationContext());
					actualizarPorcentajeSincro(getApplicationContext());
					for (NovSyncWrapper novSyncWrapper : autResponseSyncWrapper
							.getListaNovedades()) {
						// Asignamos los datos del
						// servidor en un objeto local
						// de Novedades
						novedades = AutenticadorSyncBL
								.convertirNovedades(novSyncWrapper);
						break; // Fin de Ciclo.
					}
					// Se preparan los view para mostrar la
					// pantalla de elector Ya Autenticado
					includeElector.setVisibility(View.GONE);
					includeCoinciden.setVisibility(View.GONE);
					try {
						// Se guarda la novedad 7 en base de
						// datos.
						novedadesBL.notificarYaAutenticado(censo.getCedula(),
								censo.getCodProv(), censo.getCodMpio(),
								censo.getCodZona(), censo.getCodColElec(),
								censo.getCodMesa(), censo.getTipoElector());
					} catch (NovedadesBLException e) {
						log4jDroid
								.error("AutenticadorAndroidProject:AutenticacionActivity:TestAsyncTask:",
										e);
					}
					// Se abre entonces la pantalla de
					// elector ya autenticado
					Intent intent = new Intent(AutenticacionActivity.this,
							ElectorYaAutenticadoActivity.class);
					intent.putExtra("sincronizacion", "1");
					startActivity(intent);
				}
			}
			pdServerWait.dismiss();
		}
	}

	/**
	 * Metodo encargado de llamar a la capa BL del Censo, para buscar al elector
	 * por la cedula.
	 * 
	 * @param cedula
	 *            captura en la cedula.
	 */
	private void consultarElector(String cedula) {

		try {

			CensoBL censoBL = new CensoBL(this);
			censo = censoBL.getElector(cedula);
			novedades = null;
			// SI se encuentra el elector
			if (censo != null) {

				if (censo.getElectorImpedido() > 0) {
					// si no se puede autenticar
					// NOVEDAD(NO SE PUEDE AUTENTICAR)
					novedadesBL.notificarElectorImpedido(censo.getCedula(),
							censo.getCodProv(), censo.getCodMpio(),
							censo.getCodZona(), censo.getCodColElec(),
							censo.getCodMesa(), censo.getTipoElector());
					Intent intent = new Intent(AutenticacionActivity.this,
							NovedadAutenticacionActivity.class);
					intent.putExtra("imp", 1);
					startActivity(intent);
				}

				// se actualiza el frame, con los datos encontrados del elector.
				// TODO : ¿Al elector le corresponde sufragar en este puesto de
				// votación?
				// se consultan las huellas del elector encontrado
				template = consultarTemplate(censo.getCedula());

				Configuracion configuracion = configuracionBL
						.obtenerConfiguracionActiva();

				String codDepto = configuracion.getCodProv();
				String codMpio = configuracion.getCodMpio();
				String codZona = configuracion.getCodZona();
				String codPuesto = configuracion.getCodColElec();
				String divipolPuestoConfiguracion = codDepto.concat(codMpio)
						.concat(codZona).concat(codPuesto);

				if (Util.isCodDivipolIgual(divipolPuestoConfiguracion,
						censo.getCodProv(), censo.getCodMpio(),
						censo.getCodZona(), censo.getCodColElec())) {
					// se valida que el elector, ya haya sido autenticado.
					novedades = novedadesBL.obtenerAutenticado(cedula);
					// Vota en el puesto configurado
					// ¿Elector ya fue autenticado?
					if (novedades != null) {
						// HACER CUANDO YA ES AUTENTICADO
						// se muestra la pantalla 'Elector ya autenticado'
						includeElector.setVisibility(View.GONE);
						includeCoinciden.setVisibility(View.GONE);
						novedadesBL.notificarYaAutenticado(censo.getCedula(),
								censo.getCodProv(), censo.getCodMpio(),
								censo.getCodZona(), censo.getCodColElec(),
								censo.getCodMesa(), censo.getTipoElector());
						Intent intent = new Intent(this,
								ElectorYaAutenticadoActivity.class);
						intent.putExtra("sincronizacion", "0");
						startActivity(intent);
					} else {

						/*
						 * Una vez terminado de declarar e implementar las
						 * funciones de la tarea asíncrona
						 * AutNovedadesByCedAsyncTask, a continuación se ejecuta
						 * dicha tarea, agregando como variable de entrada la
						 * cedula del elector a consultar, y terminaria el flujo
						 * de Autenticación en el método onPostExecute de esta
						 * tarea asíncrona.
						 */
						ConsultarNovedadesByCedAsyncTask task = new ConsultarNovedadesByCedAsyncTask();
						task.execute(cedula);
						// new ConsultarNovedadesByCedAsyncTask.execute(cedula);
						// // Se
						// ejecuta
						// tarea
						// asíncrona
					}

				} else {

					// ES JURADO
					if (censo.getTipoElector() == 1) {
						// ¿Elector ya fue autenticado?
						if (novedadesBL.obtenerAutenticado(cedula) != null) {
							// HACER CUANDO YA ES AUTENTICADO
							// se muestra la pantalla 'Elector ya autenticado'
							novedades = novedadesBL.obtenerAutenticado(cedula);
							includeElector.setVisibility(View.GONE);
							includeCoinciden.setVisibility(View.GONE);
							novedadesBL.notificarYaAutenticado(
									censo.getCedula(), censo.getCodProv(),
									censo.getCodMpio(), censo.getCodZona(),
									censo.getCodColElec(), censo.getCodMesa(),
									censo.getTipoElector());

							Intent intent = new Intent(this,
									ElectorYaAutenticadoActivity.class);
							intent.putExtra("sincronizacion", "0");
							startActivity(intent);
							// updateFrameDatosElector();
						} else {
							// ES JURADO Y NO HA SIDO AUTENTICADO
							/*
							 * Una vez terminado de declarar e implementar las
							 * funciones de la tarea asíncrona
							 * AutNovedadesByCedAsyncTask, a continuación se
							 * ejecuta dicha tarea, agregando como variable de
							 * entrada la cedula del elector a consultar, y
							 * terminaria el flujo de Autenticación en el método
							 * onPostExecute de esta tarea asíncrona.
							 */

							ConsultarNovedadesByCedAsyncTask task = new ConsultarNovedadesByCedAsyncTask();
							task.execute(cedula);
							// new
							// ConsultarNovedadesByCedAsyncTask.execute(cedula);
							// // Se
							// ejecuta
							// tarea
							// asíncrona
						}

					} else {

						// TODO : ¿El elector es jurado de votación?
						// ==No==>Novedad Elector en colegio electoral diferente
						// ??:Muestra
						// mensaje M28
						// PASAR PARAMETROS MENSAJE
						// NOVEDAD

						novedadesBL
								.notificarElectorEnColegioElectoralDiferente(
										censo.getCedula(), censo.getCodProv(),
										censo.getCodMpio(), censo.getCodZona(),
										censo.getCodColElec(),
										censo.getCodMesa());

						Provincias nombreProv = provinciasBL
								.obtenerProvincia(censo.getCodProv());
						Municipios nombreMpio = municipiosBL.obtenerMunicipio(
								censo.getCodProv(), censo.getCodMpio());
						// TODO:Modificar cuando el método cambie.
						ColegiosElectorales nombrePuesto = puestosBL
								.obtenerPuesto(censo.getCodProv(),
										censo.getCodMpio(), censo.getCodZona(),
										censo.getCodColElec());

						String mensajeM28 = getString(R.string.M28,
								obtenerNombre(), obtenerApellido(),
								nombreProv.getNomProv(),
								nombreMpio.getNomMpio(),
								nombrePuesto.getNomColElec(),
								Integer.valueOf(censo.getCodMesa()));
						AlertDialog cuadDialogo = Util.mensajeAceptar(
								AutenticacionActivity.this,
								R.style.TemaDialogo,
								getString(R.string.title_activity_login),
								mensajeM28, Util.DIALOG_INFORMATIVO, null);
						cuadDialogo.show();
						Util.cambiarLineaDialogos(cuadDialogo,
								getApplicationContext());
						removerDatos();
					}

				}

			} else {
				// mostrar mensaje al usuario diciendo que el elector no se
				// encuentra (revisar caso de uso)
				// Se guarda la novedad de elector no encontrado.
				novedadesBL.notificarElectorNoEncontrado(cedula);

				// TODO:VALIDAR SI ES NECESARIO HACER UNA PANTALLA PARA ESTO
				AlertDialog cuadDialogo = Util.mensajeAceptar(
						AutenticacionActivity.this, R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M10)
								+ "\n \nElector:\nNo. Documento: " + cedula,
						Util.DIALOG_ERROR, null);

				cuadDialogo.show();
				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
				removerDatos();
			}
		} catch (CensoBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:consultarElector:",
							e);
			// TODO: ERROR BD M24
		} catch (NovedadesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:consultarElector:",
							e);
			e.printStackTrace();
			// TODO: ERROR BD M24
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:consultarElector:",
							e);
			// TODO: ERROR BD M24
		} catch (TemplatesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:consultarElector:",
							e);
			// TODO: ERROR BD M24
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:consultarElector:",
							e);
		}

	}

	/**
	 * Permite registrar novedad de coincidencia o no de los datos del elector
	 * con los registrados en la base de datos.
	 * 
	 * @param view
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public void coincidenDatos(View view) {
		try {

			if (view.getId() == R.id.imageViewNoCoincide) {
				// TODO: Novedad No coinciden datos
				// novedadesBL = new
				// NovedadesBL(AutenticacionActivity.this);
				novedadesBL.notificarDatosNoCoinciden(censo.getCedula(),
						censo.getCodProv(), censo.getCodMpio(),
						censo.getCodZona(), censo.getCodColElec(),
						censo.getCodMesa(), censo.getTipoElector());

			}

			if (ProcessActivity.tieneHuellas(template)) {
				listaHuellas = ProcessActivity.listaHuellas(template);
				iniciarProcessActivity();
			} else {
				novedadesBL.notificarSinInformacionBiometrica(
						censo.getCedula(), censo.getCodProv(),
						censo.getCodMpio(), censo.getCodZona(),
						censo.getCodColElec(), censo.getCodMesa(),
						censo.getTipoElector());

				AlertDialog cuadDialogo = Util.mensajeAceptar(
						AutenticacionActivity.this, R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M22), Util.DIALOG_INFORMATIVO,
						clickListenerSinHuellas);

				cuadDialogo.show();
				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
			}

		} catch (NovedadesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:coincidenDatos:",
							e);
		} catch (MorphoSmartException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:coincidenDatos:",
							e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(this,
					R.style.TemaDialogo,
					getString(R.string.title_activity_login),
					getString(R.string.M26), Util.DIALOG_ERROR, null);
			cuadDialogo.show();
			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:coincidenDatos:",
							e);
		}

	}

	/**
	 * Listener para solicitar aprobación del delegado cuando no se presenta
	 * información biometrica del elector en el dispositivo. En el parametro
	 * duplicado siempre el valor por defecto en este caso es false ya que aún
	 * no se a autenticado el elector.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public OnClickListener clickListenerSinHuellas = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				FragmentManager fm = getSupportFragmentManager();
				AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
						Integer.parseInt(getString(R.string.sin_informacion_biometrica)),
						false, null, null);
				dgAutorizaDelegado.show(fm, "Dialog Fragment");
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:clickListenerSinHuellas:onClick:",
								e);
			}

		}
	};

	// TODO: a futuro se debe consultar de la Base de datos

	/**
	 * Permite asignar la información inicial de autenticado en:
	 * 
	 * @author grasotos
	 * @date 24-Feb-2015
	 */
	public void mostrarInfoLugarAutenticacion() {
		// TODO: agregar try catch
		if (validarConfiguracion()) {
			try {
				Configuracion configuracionActiva = configuracionBL
						.obtenerConfiguracionActiva();

				lblDpto.setText(provinciasBL.obtenerProvincia(
						configuracionActiva.getCodProv()).getNomProv());
				lblMpio.setText(municipiosBL.obtenerMunicipio(
						configuracionActiva.getCodProv(),
						configuracionActiva.getCodMpio()).getNomMpio());
				lblPuesto.setText(puestosBL.obtenerPuesto(
						configuracionActiva.getCodProv(),
						configuracionActiva.getCodMpio(),
						configuracionActiva.getCodZona(),
						configuracionActiva.getCodColElec()).getNomColElec());
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:mostrarInfoLugarAutenticacion:",
								e);
			}
		} else {
			lblDpto.setText("");
			lblMpio.setText("");
			lblPuesto.setText("");
		}
	}

	public void btnReporte_Click(View v) {
		Intent intent = new Intent(AutenticacionActivity.this,
				ReporteActivity.class);
		startActivity(intent);
	}

	/**
	 * Metodo encargado de dar memoria a los objetos TextView del frame
	 * DatosElector Luego se encarga de pasar los datos de la entidad consultada
	 * en BD, para ser viusalizados en la interfaz grafica.
	 */
	private void updateFrameDatosElector() {

		try {
			elector_cedula = (TextView) findViewById(R.id.textViewVarNoDoc);
			elector_fechaEx = (TextView) findViewById(R.id.textViewVarFechExp);
			elector_nombres = (TextView) findViewById(R.id.textViewVarNombres);
			elector_apellidos = (TextView) findViewById(R.id.textViewVarApell);
			// elector_isjurado = (TextView) findViewById(R.id.textViewVarJur);

			// se pone el visible el include que contiene los datos del elector.
			includeElector.setVisibility(View.VISIBLE);
			includeCoinciden.setVisibility(View.VISIBLE);
			elector_cedula.setText(censo.getCedula());
			elector_fechaEx.setText(Util.convertirFecha(censo
					.getFecExpedicion()));
			// para concatenar los nombres y apellidos

			elector_nombres.setText(obtenerNombre());

			elector_apellidos.setText(obtenerApellido());
			if (censo.getTipoElector() == 0) {
				img_jurado.setVisibility(View.INVISIBLE);
			} else {
				img_jurado.setVisibility(View.VISIBLE);
			}

			Bitmap profile = BitmapFactory.decodeByteArray(censo.getFoto(), 0,
					censo.getFoto().length);

			imagenElector.setImageBitmap(Bitmap.createScaledBitmap(profile,
					185, 221, false));
			imagenElector.setVisibility(View.VISIBLE);
		} catch (ParseException e) {
			elector_fechaEx.setText("");
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:updateFrameDatosElector:",
							e);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:updateFrameDatosElector:",
							e);
		}
	}

	/**
	 * Metodo encargado de la logica, al presionar "Validar Elector" de la
	 * vista. Basicamente se llama al mismo metodo de consultar elector, solo
	 * que validando que no esté vacío el campo.
	 * 
	 * @param vista
	 *            actual
	 */
	public void btnValidarElector(View v) {
		verficarCensoBD();

		if (validarConfiguracion()) {
			try {
				hideSoftKeyboard(AutenticacionActivity.this, v);
				// se valida que el campo no esté vacio.
				if (!numCedula.getText().toString().equals("")) {
					consultarElector(numCedula.getText().toString());
					// en caso de estar vacio, se muestra mensaje M03
				} else {
					AlertDialog cuadDialogo = Util.mensajeAceptar(
							this,
							R.style.TemaDialogo,
							getResources().getString(
									R.string.title_activity_login),
							getResources().getString(R.string.M03),
							Util.DIALOG_ERROR, null);
					cuadDialogo.show();
					Util.cambiarLineaDialogos(cuadDialogo,
							getApplicationContext());
				}

				if (censo != null && novedades == null) {
					includeElector.setVisibility(View.VISIBLE);
					includeCoinciden.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				log4jDroid
						.error("AutenticadorAndroidProject:AutenticacionActivity:btnValidarElector:",
								e);
			}
		} else {
			mostrarMensajeConfiguracion();
		}

	}

	/**
	 * Permite iniciar la actividad para la solicitud de la huella.
	 * 
	 * @author grasotos
	 * @throws MorphoSmartException
	 * @date 24-feb-2015
	 */
	public void iniciarProcessActivity() throws MorphoSmartException {

		try {

			if (ProcessInfo.getInstance().isStarted()) {
				this.stop();
			} else {
				// TODO: modificar deteccion del huellero conectado
				if (huelleroConectado) {
					Intent processIntent = new Intent(this,
							ProcessActivity.class);
					processIntent.putExtra(getString(R.string.intent_huella),
							getString(R.string.intent_autenticador));
					startActivityForResult(processIntent, 1);
				} else {

					huelleroConectado = ConexionHuellero.conectarHuellero(this);
					if (huelleroConectado) {
						Intent processIntent = new Intent(this,
								ProcessActivity.class);
						processIntent.putExtra(
								getString(R.string.intent_huella),
								getString(R.string.intent_autenticador));
						startActivityForResult(processIntent, 1);
					} else {
						AlertDialog cuadDialogo = Util.mensajeAceptar(this,
								R.style.TemaDialogo,
								getString(R.string.title_activity_login),
								getString(R.string.M26), Util.DIALOG_ERROR,
								null);
						cuadDialogo.show();
						Util.cambiarLineaDialogos(cuadDialogo,
								getApplicationContext());
					}
				}

			}

		} catch (ConexionHuelleroException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:iniciarProcessActivity:",
							e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(this,
					R.style.TemaDialogo,
					getString(R.string.title_activity_login),
					getString(R.string.M26), Util.DIALOG_ERROR, null);
			cuadDialogo.show();
			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:iniciarProcessActivity:",
							e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * Permite limpiar la pantalla cuando se regresa desde otra pantalla.
	 * 
	 * @author grasotos
	 * @date 24-feb-2015
	 */
	public static void removerDatos() {
		try {
			censo = null;
			template = null;

			numCedula.setText("");

			includeElector.setVisibility(View.GONE);
			includeCoinciden.setVisibility(View.GONE);
			img_jurado.setVisibility(View.GONE);
			imagenElector.setVisibility(View.GONE);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:removerDatos:",
							e);
		}
	}

	/**
	 * Permite consultar los templates por número de cédula.
	 * 
	 * @param cedula
	 *            del elector.
	 * @return Template con la cantidad de dedos almacenados
	 * @author grasotos
	 * @throws TemplatesBLException
	 * @date 24-Feb-2015
	 */
	public Templates consultarTemplate(String cedula)
			throws TemplatesBLException {

		try {
			template = templatesBL.getTemplates(cedula);

		} catch (TemplatesBLException e) {
			throw new TemplatesBLException(e.getMessage());
		}
		return template;

	}

	/**
	 * @return
	 */
	public static String obtenerNombre() {
		String nombre = censo.getPriNombre();
		if (censo.getSegNombre() != null) {
			if (!censo.getSegNombre().trim().isEmpty()) {
				nombre = nombre.concat(" " + censo.getSegNombre());
			}
		}
		return nombre;
	}

	/**
	 * @return
	 */
	public static String obtenerApellido() {
		String apellido = censo.getPriApellido();
		if (censo.getSegApellido() != null) {
			if (!censo.getSegApellido().trim().isEmpty()) {
				apellido = apellido.concat(" " + censo.getSegApellido());
			}
		}
		return apellido;
	}

	public void conexionImpresora() throws POSSDKManagerException {
		try {
			possdkImprimir = POSSDKManager
					.getInstance(AutenticacionActivity.this);
			possdkImprimir.conexionImpresora();

		} catch (POSSDKManagerException e) {
			throw new POSSDKManagerException(e.getMessage());
		}
	}

	public static void hideSoftKeyboard(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}

	public void verficarCensoBD() {
		try {
			// boolean flag = Util.verificarBD(this);
			if (!Util.verificarBD(this)) {
				AlertDialog cuadDialogo = Util.mensajeAceptar(this,
						R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M42), Util.DIALOG_ERROR, null);
				cuadDialogo.show();
				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
			}
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:verficarCensoBD:",
							e);
		}
	}

	public void abrirConfiguracion(View v) {

		try {

			if (ProcessInfo.getInstance().isStarted()) {
				this.stop();
			} else {
				// TODO: modificar deteccion del huellero conectado
				if (!huelleroConectado) {
					huelleroConectado = ConexionHuellero.conectarHuellero(this);
				}

			}

			String perfil = "";
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				perfil = extras.getString("perfil");
			}

			Intent intent = new Intent(this, ConfiguracionActivity.class);
			intent.putExtra("perfil", perfil);
			startActivity(intent);

		} catch (ConexionHuelleroException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:iniciarProcessActivity:",
							e);
			AlertDialog cuadDialogo = Util.mensajeAceptar(this,
					R.style.TemaDialogo,
					getString(R.string.title_activity_login),
					getString(R.string.M26), Util.DIALOG_ERROR, null);
			cuadDialogo.show();
			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:iniciarProcessActivity:",
							e);
		}

	}

	@Override
	protected void onResume() {
		try {
			if (validarConfiguracion()) {
				novedadesBL = new NovedadesBL(
						AutenticacionActivity.this.getApplicationContext());
				provinciasBL = new ProvinciasBL(
						AutenticacionActivity.this.getApplicationContext());
				municipiosBL = new MunicipiosBL(
						AutenticacionActivity.this.getApplicationContext());
				puestosBL = new ColegiosElectoralesBL(
						AutenticacionActivity.this.getApplicationContext());
			} else {
				mostrarMensajeConfiguracion();
			}

			mostrarInfoLugarAutenticacion();

		} catch (NotFoundException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onResume:",
							e);
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onResume:",
							e);
		} catch (ProvinciasBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onResume:",
							e);
		} catch (MunicipiosBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onResume:",
							e);
		} catch (ColegiosElectoralesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onResume:",
							e);
		}
		super.onResume();
	}

	private boolean validarConfiguracion() {
		try {
			boolean res = true;
			configuracionBL = new ConfiguracionBL(getApplicationContext());
			if (!Util.existeConfiguracion(configuracionBL)) {
				res = false;
			}
			return res;
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:validarConfiguracion:",
							e);
			return false;
		}
	}

	private void mostrarMensajeConfiguracion() {

		AlertDialog cuadDialogo = Util.mensajeAceptar(this,
				R.style.TemaDialogo,
				getResources().getString(R.string.title_activity_login),
				getResources().getString(R.string.M41),
				Util.DIALOG_INFORMATIVO, null);
		cuadDialogo.show();
		Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
	}

	/**
	 * Método que permite actualizar el label de sincronización a "Conectado"
	 * 
	 * @param context
	 */
	public static void cambiarEstadoConectado(Context context) {
		lblConexion.setTextColor(Color.WHITE);
		lblConexion.setText(context.getResources().getString(
				R.string.label_conectado));
	}

	/**
	 * Método que permite actualizar el label de sincronización a "Desconectado"
	 * 
	 * @param context
	 */
	public static void cambiarEstadoDesconectado(Context context) {
		lblConexion.setTextColor(Color.RED);
		lblConexion.setText(context.getResources().getString(
				R.string.label_desconectado));
	}

	/**
	 * Método que sirve para actualizar en el label de sincronización el
	 * porcentaje de sincronización.
	 * 
	 * @param context
	 */
	public static void actualizarPorcentajeSincro(Context context) {
		try {
			NovedadesBL novedadesBL = new NovedadesBL(context);
			lblSincronizado.setText(context.getResources().getString(
					R.string.label_sincronizado)
					+ novedadesBL.obtenerPorcentajeNovedadesSincronizadas());
		} catch (NotFoundException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:actualizarPorcentajeSincro:",
							e);
		} catch (NovedadesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:actualizarPorcentajeSincro:",
							e);
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:actualizarPorcentajeSincro:",
							e);
		}
	}

}
