package carvajal.autenticador.android.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import carvajal.autenticador.android.activity.dialogo.AutorizaDelegadoDialogFragment;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.dal.greendao.read.Censo;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.framework.morphosmart.ConexionHuellero;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import carvajal.autenticador.android.util.keyczar.autenticador.AutenticadorKeyczarCrypterException;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;

import com.morpho.morphosmart.sdk.ErrorCodes;

/**
 * Clase para notificaciones de electores.
 * 
 * @author grasotos
 * 
 */
public class NovedadAutenticacionActivity extends FragmentActivity {

	/**
	 * Conexion con Impresora
	 */
	private POSSDKManager possdkImprimir;
	/**
	 * Boton para impresion
	 */
	public Button imprimir;

	/**
	 * Include para el boton cuando se autentica
	 */
	public View includeBtnAut;
	/**
	 * Include para botones cuando no logra autenticarse.
	 */
	public View includeBtnNoAut;
	/**
	 * Include para el boton cuando se autentica elector impedido
	 */
	public View includeBtnImp;
	/**
	 * Texto para mostrar el departamento
	 */
	public TextView dpto;
	/**
	 * texto para mostrar el municipio
	 */
	public TextView mpio;
	/**
	 * Texto para mostrar el puesto
	 */
	public TextView puesto;
	/**
	 * Texto para mostrar la fecha
	 */
	public TextView fecha;

	/* Elector */

	/**
	 * Texto para mostrar la fecha de expedicion
	 */
	private TextView textViewVarFechExp;
	/**
	 * Texto para mostrar los nombres del elector
	 */
	private TextView textViewVarNombres;
	/**
	 * Texto para mostrar el número de documento del elector
	 */
	private TextView textViewVarNoDoc;
	/**
	 * Texto para mostrar el apellido del elector
	 */
	private TextView textViewVarApell;
	/**
	 * Texto para mostrar si el elector es jurado o no
	 */
	// private TextView textViewVarJur;

	/**
	 * Boton para reintentar autenticarse
	 */
	public Button reintentar;

	/**
	 * Objeto para acceder a capa BL de novedades
	 */
	public NovedadesBL novedadesBL;

	/**
	 * Generacion de Logs en android
	 */
	private final Logger log4jDroid = Logger
			.getLogger(NovedadAutenticacionActivity.class);

	/**
	 * Score de autenticacion o no autenticacion
	 */
	Integer score;

	/*
	 * Layout de la actividad
	 */
	RelativeLayout layout_tipos_autenticacion;

	/**
	 * Clase para encriptar y desencriptar
	 */
	AutenticadorKeyczarCrypter crypter;
	
	private static ImageView imagenElector;

	/**
	 * OnCreate para inicializar la interfaz gráfica y variables necesarias.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tipos_autenticacion);
		// Se construye el relatve layout a partir del
		// layout_tipos_autenticacion
		layout_tipos_autenticacion = (RelativeLayout) findViewById(R.id.layout_tipos_autenticacion);

		try {
			inicializarInterfaz();
			novedadesBL = new NovedadesBL(NovedadAutenticacionActivity.this);
			crypter = AutenticadorKeyczarCrypter.getInstance(getResources());
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:onCreate:",
							e);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:onCreate:",
							e);
		}
	}

	/**
	 * Permite inicializar la interfaz gráfica de la pantalla
	 * activity_tipos_autenticacion
	 * 
	 * @throws Exception
	 */
	public void inicializarInterfaz() throws Exception {
		try {
			imprimir = (Button) findViewById(R.id.buttonImprimir);
			includeBtnAut = findViewById(R.id.includeBotonesAut);
			includeBtnNoAut = findViewById(R.id.includeBotonesNoAut);
			includeBtnImp = findViewById(R.id.includeBotonesImp);
			dpto = (TextView) findViewById(R.id.textViewVarDpto);
			mpio = (TextView) findViewById(R.id.textViewVarMpio);
			puesto = (TextView) findViewById(R.id.textViewVarPuesto);
			fecha = (TextView) findViewById(R.id.lbl_fecha);
			fecha.setText(Util.obtenerFecha());
			reintentar = (Button) findViewById(R.id.buttonReintentar);

			textViewVarFechExp = (TextView) findViewById(R.id.textViewVarFechExp);
			textViewVarNombres = (TextView) findViewById(R.id.textViewVarNombres);
			textViewVarNoDoc = (TextView) findViewById(R.id.textViewVarNoDoc);
			textViewVarApell = (TextView) findViewById(R.id.textViewVarApell);
			// textViewVarJur = (TextView) findViewById(R.id.textViewVarJur);
			
			imagenElector = (ImageView) findViewById(R.id.fotoElectorTipos);

			int noAutenticado = getIntent().getExtras().getInt("noAut");
			// int noAutenticado = 2;
			int autenticado = getIntent().getExtras().getInt("aut");
			// int autenticado = 2;
			int impedido = getIntent().getExtras().getInt("imp");
			if (noAutenticado == 2) {
				includeBtnAut.setVisibility(View.GONE);
				includeBtnNoAut.setVisibility(View.VISIBLE);
				includeBtnImp.setVisibility(View.GONE);
				layout_tipos_autenticacion.setBackground(getResources()
						.getDrawable(R.drawable.ic_no_se_puede_autenticar));
			}

			if (autenticado == 1) {
				includeBtnAut.setVisibility(View.VISIBLE);
				includeBtnNoAut.setVisibility(View.GONE);
				includeBtnImp.setVisibility(View.GONE);
				layout_tipos_autenticacion.setBackground(getResources()
						.getDrawable(
								R.drawable.ic_autenticado_satisfactoriamente));
			}
			
			if (impedido == 1){
				includeBtnAut.setVisibility(View.GONE);
				includeBtnNoAut.setVisibility(View.GONE);
				includeBtnImp.setVisibility(View.VISIBLE);
				layout_tipos_autenticacion.setBackground(getResources()
						.getDrawable(
								R.drawable.ic_elector_impedido));
			}

			findViewById(R.id.textViewHoraReg).setVisibility(View.GONE);
			findViewById(R.id.textViewNoMesa).setVisibility(View.GONE);
			findViewById(R.id.textViewVarHoraReg).setVisibility(View.GONE);
			findViewById(R.id.textViewVarNoMesa).setVisibility(View.GONE);

			imprimir.setOnClickListener(clickListenerImprimirComprobante);

			mostrarInfoLugarAutenticacion();
			asignarValoresElector(AutenticacionActivity.censo);
			possdkImprimir = POSSDKManager.getInstance(this);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:inicializarInterfaz:",
							e);
			throw new Exception(e.getMessage());
		}
	}

	// TODO: a futuro se debe consultar de la Base de datos
	/**
	 * Muestra la información del lugar de autenticacion
	 * 
	 * @throws Exception
	 */
	public void mostrarInfoLugarAutenticacion() throws Exception {
		try {
			Configuracion configuracionActiva = AutenticacionActivity.configuracionBL
					.obtenerConfiguracionActiva();

			dpto.setText(AutenticacionActivity.provinciasBL.obtenerProvincia(
					configuracionActiva.getCodProv()).getNomProv());
			mpio.setText(AutenticacionActivity.municipiosBL.obtenerMunicipio(
					configuracionActiva.getCodProv(),
					configuracionActiva.getCodMpio()).getNomMpio());
			puesto.setText(AutenticacionActivity.puestosBL.obtenerPuesto(
					configuracionActiva.getCodProv(),
					configuracionActiva.getCodMpio(),
					configuracionActiva.getCodZona(),
					configuracionActiva.getCodColElec()).getNomColElec());

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:mostrarInfoLugarAutenticacion:",
							e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * @param V
	 */
	public void buttonAprobarDelegado_Click(View V) {
		// TODO: Necesito valor del duplicado.
		boolean duplicado = false;
		FragmentManager fm = getSupportFragmentManager();
		// TODO no se le manda score
		AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
				Integer.parseInt(getResources().getString(
						R.string.no_se_pudo_autenticar)), duplicado, score,
				null);
		// Falta el campo de duplicado en el constructor
		dgAutorizaDelegado.show(fm, "Dialog Fragment");
	}
	
	/**
	 * @param V
	 */
	public void buttonAprobarDelegadoImp(View V) {
		boolean duplicado = false;
		FragmentManager fm = getSupportFragmentManager();
		AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
				Integer.parseInt(getResources().getString(
						R.string.elector_impedido)), duplicado, score,
				null);
		dgAutorizaDelegado.show(fm, "Dialog Fragment");
	}

	/**
	 * Permite reintentar la captura de huella en caso de que no logre
	 * autenticarse.
	 * 
	 * @param view
	 */
	public void reintentar(View view) {
		if (ProcessInfo.getInstance().isStarted()) {
			// this.stop();
		} else {

			if (AutenticacionActivity.huelleroConectado) {
				Intent processIntent = new Intent(this, ProcessActivity.class);
				processIntent.putExtra(getString(R.string.intent_huella),
						getString(R.string.intent_autenticador));
				startActivityForResult(processIntent, 1);
			} else {
				AlertDialog cuadDialogo = Util.mensajeAceptar(this,
						R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M26), Util.DIALOG_ERROR, null);

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

			}

		}
	}

	/**
	 * En este listener se implementa la funcionalidad del boton Imprimir
	 * Comprobante.
	 */
	public View.OnClickListener clickListenerImprimirComprobante = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			imprimir.setEnabled(false);
			try {
				// construcción de la variable novedades
				novedadesBL = new NovedadesBL(
						NovedadAutenticacionActivity.this
								.getApplicationContext());
			} catch (AutenticadorDaoMasterSourceException e) {// Error
																// inesperado al
																// construir la
																// instancia de
																// novedades
				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			}
			try {
				// variable que captura el éxito al imprimir
				boolean exitoImprimir;
				// Variable de duplicado en el comprobante, es false por defecto
				// en este activity
				boolean duplicado = false;
				if (AutenticacionActivity.censo.getTipoElector() == 1) {// ¿Es
																		// un
																		// jurado
																		// de
																		// votación?
					// Elector SI ES JURADO
					// Imprime el comprobante de Autenticación
					exitoImprimir = possdkImprimir.imprimirComprobante(
							getResources().getString(R.string.nombre_eleccion),
							AutenticacionActivity.censo.getCedula(),
							String.valueOf(Integer
									.valueOf(AutenticacionActivity.censo
											.getCodMesa())),
							Util.obtenerFechaDeImpresion(new Date(Long
									.parseLong(crypter.decrypt(novedadesBL
											.obtenerAutenticado(
													AutenticacionActivity.censo
															.getCedula())
											.getFechaNovedad())))), duplicado,
							true);

				} else {
					// Elector NO Jurado
					// Imprime el comprobante de Autenticación
					exitoImprimir = possdkImprimir.imprimirComprobante(
							getResources().getString(R.string.nombre_eleccion),
							AutenticacionActivity.censo.getCedula(),
							String.valueOf(Integer
									.valueOf(AutenticacionActivity.censo
											.getCodMesa())),
							Util.obtenerFechaDeImpresion(new Date(Long
									.parseLong(crypter.decrypt(novedadesBL
											.obtenerAutenticado(
													AutenticacionActivity.censo
															.getCedula())
											.getFechaNovedad())))), duplicado,
							false);

				}
				if (exitoImprimir) {// ¿Se imprimió el comprobante?
					/*
					 * En este punto se debe realizar la sincronización de
					 * novedad 9: Certificado Impreso, con el servidor de
					 * centralización
					 * 
					 * Declaración e implementación de TAREA ASÍNCRONA
					 * AutNovedadesByCedAsyncTask Esta tarea asíncrona, va a
					 * ejecutar la consulta de novedades a partir de la cédula
					 * de un elector.
					 */
					AsyncTask<Void, Void, Void> InsetarNovedadImpresionAsyncTask = new AsyncTask<Void, Void, Void>() {
						ListaNovedadSyncWrapper listaNovedadesResponseWrapper;

						/*
						 * En el método implementado doInBackground se ejecuta
						 * el consumo de la función SetInsertNov en el servicio
						 * web, al finalizar esta consulta llenamos el wrapper
						 * ListaNovedadSyncWrapper
						 */
						@Override
						protected Void doInBackground(Void... params) {
							try {

								// Declaración e instancia del objeto
								// AutenticadorSyncBL
								AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
										NovedadAutenticacionActivity.this,
										getResources()
												.getString(
														R.string.metodo_insertar_novedad));
								// Ejecutamos la función en el servidor por
								// medio de la cédula

								String androidId = Util
										.obtenerMAC(NovedadAutenticacionActivity.this
												.getApplicationContext());
								int tipoNovedad = Integer
										.parseInt(NovedadAutenticacionActivity.this
												.getApplicationContext()
												.getString(
														R.string.certificado_impreso));
								int idHuella = 0;
								score = 0;
								NovSyncWrapper novedadSynWrapper = new NovSyncWrapper(
										androidId,
										AutenticacionActivity.censo.getCedula(),
										new Date(), score, idHuella,
										AutenticacionActivity.censo
												.getTipoElector(), tipoNovedad,
										0, AutenticacionActivity.censo
												.getCodMesa());
								List<NovSyncWrapper> listanovedades = new ArrayList<NovSyncWrapper>();
								listanovedades.add(novedadSynWrapper);
								ListaNovedadSyncWrapper listaNovedadesWrapper = new ListaNovedadSyncWrapper(
										listanovedades);
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
						 * ¨ El método onPostExecute, se ejecuta, solo cuando
						 * los procedimientos del método doInBackground han
						 * terminado
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
										AutenticacionActivity
												.cambiarEstadoDesconectado(getApplicationContext());
									} else if (novSyncWrapper.getRespuesta() == Integer
											.parseInt(getApplicationContext()
													.getString(
															R.string.novedad_sincronizada))) {// Sincronizado
										sincronizado = novSyncWrapper
												.getRespuesta();
										AutenticacionActivity
												.cambiarEstadoConectado(getApplicationContext());
									}
								}
							} else {
								AutenticacionActivity
										.cambiarEstadoDesconectado(getApplicationContext());
							}
							// Si se imprimió el comprobante
							try {
								// Se notifica la novedad de certificado impreso
								if (novedadesBL
										.notificarCertificadoImpreso(
												AutenticacionActivity.censo
														.getCedula(),
												AutenticacionActivity.censo
														.getCodProv(),
												AutenticacionActivity.censo
														.getCodMpio(),
												AutenticacionActivity.censo
														.getCodZona(),
												AutenticacionActivity.censo
														.getCodColElec(),
												AutenticacionActivity.censo
														.getCodMesa(),
												AutenticacionActivity.censo
														.getTipoElector(),
												sincronizado)) {
									AutenticacionActivity
											.actualizarPorcentajeSincro(getApplicationContext());
								}
							} catch (NovedadesBLException e) {// Excepción
																// inesperada al
																// guarda la
																// novedad
								log4jDroid
										.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
												e);
							}
							// Retorna al activity anterior de autenticación
							onBackPressed();
							imprimir.setEnabled(true);
						}
					};
					/*
					 * Una vez terminado de declarar e implementar las funciones
					 * de la tarea asíncrona InsetarNovedadAsyncTask, a
					 * continuación se ejecuta dicha tarea, agregando como
					 * variable de entrada la cedula del elector a consultar, y
					 * terminaria el flujo de Autenticación en el método
					 * onPostExecute de esta tarea asíncrona.
					 */
					InsetarNovedadImpresionAsyncTask.execute();
				} else {
					// No se imprió el comprobante
					// Se muestra el mensaje M27
					AlertDialog cuadDialogo = Util.mensajeAceptar(
							NovedadAutenticacionActivity.this,
							R.style.TemaDialogo,
							getResources().getString(
									R.string.title_activity_login),
							getResources().getString(R.string.M27),
							Util.DIALOG_ERROR, null);

					cuadDialogo.show();

					Util.cambiarLineaDialogos(cuadDialogo,
							getApplicationContext());
				}
			} catch (POSSDKManagerException e) {// Error inesperado al conectar
												// la impresora
				// Impresora no funciona, muestra mensaje M27
				AlertDialog cuadDialogo = Util
						.mensajeAceptar(
								NovedadAutenticacionActivity.this,
								R.style.TemaDialogo,
								getResources().getString(
										R.string.title_activity_login),
								getResources().getString(R.string.M27),
								Util.DIALOG_ERROR, null);

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			} catch (NovedadesBLException e) {
				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			} catch (NumberFormatException e) {
				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			} catch (NotFoundException e) {
				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			} catch (AutenticadorKeyczarCrypterException e) {
				log4jDroid
						.error("AutenticadorAndroidProject:NovedadAutenticacionActivity:clickListenerImprimirComprobante:",
								e);
			}
		}
	};

	/**
	 * Permite asignar los valores del elector que se está autenticando.
	 * 
	 * @param censo
	 */
	public void asignarValoresElector(Censo censo) {
		try {
			textViewVarFechExp.setText(Util.convertirFecha(censo
					.getFecExpedicion()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String nombre = censo.getPriNombre();
		if (censo.getSegNombre() != null) {
			if (!censo.getSegNombre().trim().isEmpty()) {
				nombre = nombre.concat(" " + censo.getSegNombre());
			}

		}
		textViewVarNombres.setText(nombre);
		textViewVarNoDoc.setText(censo.getCedula());
		String apellido = censo.getPriApellido();
		if (censo.getSegApellido() != null) {
			if (!censo.getSegApellido().trim().isEmpty()) {
				apellido = apellido.concat(" " + censo.getSegApellido());
			}

		}

		textViewVarApell.setText(apellido);
		/*
		 * if (censo.getTipoElector() == 1) {
		 * textViewVarJur.setText(getString(R.string.label_jurado_si)); } else {
		 * textViewVarJur.setText(getString(R.string.label_jurado_no)); }
		 */

		Bitmap profile = BitmapFactory.decodeByteArray(Util.obtenerFotoElector(AutenticacionActivity.censo, getApplicationContext()), 0,
				Util.obtenerFotoElector(AutenticacionActivity.censo, getApplicationContext()).length);

		imagenElector.setImageBitmap(Bitmap.createScaledBitmap(profile,
				173, 208, false));
		
	}

	/**
	 * Remueve datos de la pantalla anterior al realizar el regreso.
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AutenticacionActivity.removerDatos();
		super.onBackPressed();

	}

	/**
	 * Este evento se produce al regresar de la pantalla de lectura del code 2D
	 * Se encarga de obtener la trama del valor leido del code 2D, apartir del
	 * IntentData Luego llama al metodo, de buscar el numero de la cedula.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == 1) {
				if (resultCode == RESULT_OK) {
				}

				if (resultCode == 2) {
					final int score = data.getIntExtra("score", 0);
					int pk = data.getIntExtra("dedo", 0);
					int codigo = data.getIntExtra("codigo", 0);

					if (codigo == ErrorCodes.MORPHOERR_TIMEOUT) {

						// TODO: Novedad Tiempo
						novedadesBL.notificarNoPusoDedoParaValidacion(
								AutenticacionActivity.censo.getCedula(),
								AutenticacionActivity.censo.getCodProv(),
								AutenticacionActivity.censo.getCodMpio(),
								AutenticacionActivity.censo.getCodZona(),
								AutenticacionActivity.censo.getCodColElec(),
								AutenticacionActivity.censo.getCodMesa(),
								AutenticacionActivity.censo.getTipoElector());

						AlertDialog cuadDialogo = Util
								.mensajeConfirmacionPersonalizado(
										NovedadAutenticacionActivity.this,
										R.style.TemaDialogo,
										getString(R.string.title_activity_login),
										getString(R.string.M17),
										getString(R.string.label_reintentar),
										null, Util.DIALOG_ERROR,
										clickListenerReintentar, null);
						cuadDialogo.show();

						Util.cambiarLineaDialogos(cuadDialogo,
								getApplicationContext());

					} else {

						if (codigo == ErrorCodes.MORPHO_OK) {
							final int idHuella = Util.obtenerIdHuella(
									AutenticacionActivity.listaIdsHuellas, pk);
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
												NovedadAutenticacionActivity.this,
												getResources()
														.getString(
																R.string.metodo_insertar_novedad));
										// Ejecutamos la función en el servidor
										// por medio de la cédula

										String androidId = Util
												.obtenerMAC(NovedadAutenticacionActivity.this
														.getApplicationContext());
										int tipoNovedad = Integer
												.parseInt(NovedadAutenticacionActivity.this
														.getApplicationContext()
														.getString(
																R.string.autenticado));
										NovSyncWrapper novedadSynWrapper = new NovSyncWrapper(
												androidId,
												AutenticacionActivity.censo
														.getCedula(),
												new Date(), score, idHuella,
												AutenticacionActivity.censo
														.getTipoElector(),
												tipoNovedad, 0,
												AutenticacionActivity.censo
														.getCodMesa());
										List<NovSyncWrapper> listanovedades = new ArrayList<NovSyncWrapper>();
										listanovedades.add(novedadSynWrapper);
										ListaNovedadSyncWrapper listaNovedadesWrapper = new ListaNovedadSyncWrapper(
												listanovedades);
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
									for (NovSyncWrapper novSyncWrapper : listaNovedadesResponseWrapper
											.getListanovedades()) {
										int sincronizado = 0;
										if (novSyncWrapper.getRespuesta() == Integer
												.parseInt(getApplicationContext()
														.getString(
																R.string.novedad_no_sincronizada))) {// No
																										// Sincronizado
											sincronizado = novSyncWrapper
													.getRespuesta();
										} else if (novSyncWrapper
												.getRespuesta() == Integer
												.parseInt(getApplicationContext()
														.getString(
																R.string.novedad_sincronizada))) {// Sincronizado
											sincronizado = novSyncWrapper
													.getRespuesta();
										}
										// (NOVEDAD AUTENTICACION) DESPUES DE
										// INTENTO DE SINCRONIZACIÓN
										try {
											// TODO:Para modificar el score
											// Util.mensajeAceptar(this,
											// getString(R.string.title_activity_login),
											// getString(R.string.M19),
											// Util.DIALOG_INFORMATIVO,
											// clickListenerAuten).show();
											// MODIFICAR para actualizar
											novedadesBL.notificarAutenticado(
													AutenticacionActivity.censo
															.getCedula(),
													AutenticacionActivity.censo
															.getCodProv(),
													AutenticacionActivity.censo
															.getCodMpio(),
													AutenticacionActivity.censo
															.getCodZona(),
													AutenticacionActivity.censo
															.getCodColElec(),
													AutenticacionActivity.censo
															.getCodMesa(),
													AutenticacionActivity.censo
															.getTipoElector(),
													idHuella, score,
													sincronizado);

											modificarInterfazNoAutenticado();
											// Util.mensajeAceptar(this,
											// getString(R.string.title_activity_login),
											// getString(R.string.M19),
											// Util.DIALOG_INFORMATIVO,
											// clickListenerAuten).show();
										} catch (NovedadesBLException e) {
											log4jDroid
													.error("AutenticadorAndroidProject:InsetarNovedadAsyncTaskonPostExecute:onActivityResult:",
															e);
										}
									}
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
							this.score = score;
							novedadesBL
									.notificarNoSePudoAutenticar(
											AutenticacionActivity.censo
													.getCedula(),
											AutenticacionActivity.censo
													.getCodProv(),
											AutenticacionActivity.censo
													.getCodMpio(),
											AutenticacionActivity.censo
													.getCodZona(),
											AutenticacionActivity.censo
													.getCodColElec(),
											AutenticacionActivity.censo
													.getCodMesa(),
											AutenticacionActivity.censo
													.getTipoElector(), score);
						}
					}
				}
			}
		} catch (NovedadesBLException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onActivityResult:",
							e);
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutenticacionActivity:onActivityResult:",
							e);
		}
	}

	/**
	 * Permite reintentar la toma de huella.
	 */
	public OnClickListener clickListenerReintentar = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			iniciarProcessActivity();
		}
	};

	/**
	 * Permite inicial el proceso para toma de huella, valida la conexión.
	 */
	public void iniciarProcessActivity() {

		try {

			if (ProcessInfo.getInstance().isStarted()) {
				this.stop();
			} else {

				if (AutenticacionActivity.huelleroConectado) {
					Intent processIntent = new Intent(this,
							ProcessActivity.class);
					processIntent.putExtra(getString(R.string.intent_huella),
							getString(R.string.intent_autenticador));
					startActivityForResult(processIntent, 1);
				} else {

					AutenticacionActivity.huelleroConectado = ConexionHuellero
							.conectarHuellero(this);
					if (AutenticacionActivity.huelleroConectado) {
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
					// Util.mensajeAceptar(this,
					// getString(R.string.title_activity_login),
					// getString(R.string.M26), Util.DIALOG_ERROR, null).show();
				}

			}

		} catch (Exception e) {
			AlertDialog cuadDialogo = Util.mensajeAceptar(this,
					R.style.TemaDialogo,
					getString(R.string.title_activity_login),
					getString(R.string.M26), Util.DIALOG_ERROR, null);
			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
		}
	}

	/**
	 * 
	 */
	protected void stop() {

	}

	/**
	 * Permite actualizar la interfaz cuando el usuario no se ha podiodo
	 * autenticar.
	 */
	public void modificarInterfazNoAutenticado() {
		includeBtnAut.setVisibility(View.VISIBLE);
		includeBtnNoAut.setVisibility(View.GONE);
		layout_tipos_autenticacion.setBackground(getResources().getDrawable(
				R.drawable.ic_autenticado_satisfactoriamente));
	}

}
