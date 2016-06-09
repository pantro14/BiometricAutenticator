package carvajal.autenticador.android.activity.asynctask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.dal.greendao.write.Novedades;
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import carvajal.autenticador.android.util.keyczar.autenticador.AutenticadorKeyczarCrypterException;
import carvajal.autenticador.android.util.wrappers.AutResponseSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;

public class ConsultarComprobanteImpresoAsyncTask extends
		AsyncTask<String, Void, String> {

	/**
	 * Instancia del wrapper que recibe la respuesta del servicio que verifica
	 * que un elector esté autenticado, y el tipo de autenticaciones que este
	 * tiene registradas.
	 */
	AutResponseSyncWrapper autResponseSyncWrapper = null;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	/*
	 * Actividad predecesora.
	 */
	public Activity actividad;

	/*
	 * Se instancia un objeto de novedades para guardar las novedades en cada
	 * caso
	 */
	public NovedadesBL novedadesBl = null;

	/**
	 * Novedad para validar la autenticacion ya realizada.
	 */
	public static Novedades novedades = null;

	/* instancia única de la clase Singleton POSSDKManager. permite imprimir */
	private POSSDKManager possdkImprimir;

	/**
	 * Clase para encriptar y desencriptar
	 */
	private AutenticadorKeyczarCrypter crypter;

	/*
	 * Valor del dedo del elector que se autentica
	 */
	public Integer score;

	/** Campo requerido para saber si el comprobante se imprime duplicado o no. */
	public boolean duplicado;

	public ConsultarComprobanteImpresoAsyncTask(Activity actividad,
			boolean duplicado, Integer score) {
		this.actividad = actividad;
		this.duplicado = duplicado;
		this.score = score;
		try {
			this.novedadesBl = new NovedadesBL(actividad);
			this.possdkImprimir = POSSDKManager.getInstance(actividad);
			this.crypter = AutenticadorKeyczarCrypter.getInstance(actividad
					.getResources());
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
		} catch (AutenticadorKeyczarCrypterException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
		}
	}

	/**
	 * Imprime primer comprobante de autenticación por primera vez.
	 */
	public void impresionComprobante() {
		try {
			// Se establece la fecha que se va a guardar al imprimir. y que se
			// almacenó
			// en el servidor central
			Date date = null;
			// Si novedad es nulo, el servidor no respondió
			if (novedades == null) {
				try {
					// Se asigna la fecha de base de datos local
					date = new Date(Long.parseLong(crypter.decrypt(novedadesBl
							.obtenerAutenticado(
									AutenticacionActivity.censo.getCedula())
							.getFechaNovedad())));
				} catch (AutenticadorKeyczarCrypterException e) {// Error
																	// inesperado
																	// AutenticadorKeyczarCrypterException
					log4jDroid
							.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
									e);
				} catch (NovedadesBLException e) {// Error inesperado
													// NovedadesBLException
					log4jDroid
							.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
									e);
				}
			} else {// se asigna el valor que viene del servidor
				DateFormat format = new SimpleDateFormat(
						"EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
				date = format.parse(novedades.getFechaNovedad());
			}
			// variable que captura el éxito al imprimir
			boolean exitoImprimir;
			// Variable de duplicado en el comprobante, es false por defecto
			// en este activity
			boolean duplicado = false;
			if (AutenticacionActivity.censo.getTipoElector() == 1) {// ¿Es un
																	// jurado de
																	// votación?
				// Elector SI ES JURADO
				// Imprime el comprobante de Autenticación
				exitoImprimir = possdkImprimir
						.imprimirComprobante(actividad.getResources()
								.getString(R.string.nombre_eleccion),
								AutenticacionActivity.censo.getCedula(),
								String.valueOf(Integer
										.valueOf(AutenticacionActivity.censo
												.getCodMesa())), Util
										.obtenerFechaDeImpresion(date),
								duplicado, true);
			} else {
				// Elector NO Jurado
				// Imprime el comprobante de Autenticación
				exitoImprimir = possdkImprimir.imprimirComprobante(actividad
						.getResources().getString(R.string.nombre_eleccion),
						AutenticacionActivity.censo.getCedula(), String
								.valueOf(Integer
										.valueOf(AutenticacionActivity.censo
												.getCodMesa())), Util
								.obtenerFechaDeImpresion(date), duplicado,
						false);
			}
			if (exitoImprimir) {// ¿Se imprimió el comprobante?, SI se imprimió
								// el comprobante

				// SINCRONIZAR novedad “Comprobante se imprimió
				// satisfactoriamente”
				// hacia base de datos de centralización.
				int tipoNovedad = Integer.parseInt(actividad.getResources()
						.getString(R.string.ya_autenticado));
				ImprimePrimerComprobanteAsyncTask task = new ImprimePrimerComprobanteAsyncTask(
						actividad, tipoNovedad, score);
				task.execute();
			} else {
				// No se imprió el comprobante, Se muestra el mensaje M27
				AlertDialog cuadDialogo = Util.mensajeAceptar(actividad,
						R.style.TemaDialogo, actividad.getResources()
								.getString(R.string.title_activity_login),
						actividad.getResources().getString(R.string.M27),
						Util.DIALOG_ERROR, null);

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, actividad);
			}
		} catch (POSSDKManagerException e) {// Error inesperado al conectar la
											// impresora
			// Impresora no funciona, muestra mensaje M27
			AlertDialog cuadDialogo = Util.mensajeAceptar(
					actividad,
					R.style.TemaDialogo,
					actividad.getResources().getString(
							R.string.title_activity_login), actividad
							.getResources().getString(R.string.M27),
					Util.DIALOG_ERROR, null);

			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, actividad);
			log4jDroid
					.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
							e);
		} catch (NumberFormatException e) {// Error inesperado
											// NumberFormatException
			log4jDroid
					.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
							e);
		} catch (NotFoundException e) {// Error inesperado NotFoundException
			log4jDroid
					.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
							e);
		} catch (ParseException e) {// Error inesperado ParseException
			log4jDroid
					.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:impresionComprobante:",
							e);
		}
	}

	@Override
	protected String doInBackground(String... cedula) {
		try {
			// Declaración e instancia del objeto AutenticadorSyncBL
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(actividad,
					actividad.getResources().getString(
							R.string.metodo_get_novedad_by_ced));
			// Ejecutamos la función en el servidor por medio de la cédula
			autResponseSyncWrapper = autSyncBL
					.obtenerAutenticadoByCed(cedula[0]);

		} catch (AutenticadorSyncBLException e) {// Exepción no controlada de
													// AutenticadorSyncBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:doInBackground:",
							e);
		}
		return cedula[0];
	}

	@Override
	protected void onPostExecute(String cedula) {
		// verifica que el wrapper de respuesta para la cedula autenticada sea
		// nula
		if (autResponseSyncWrapper == null) {// SI es nula
			// Imprime primer comprobante de autenticación.
			impresionComprobante();
			AutenticacionActivity.cambiarEstadoDesconectado(actividad
					.getApplicationContext());
		} else {
			// Bandera que valida que si la novedad es tipo 9: Certificado
			// Impreso
			boolean certificadoImpreso = true;
			// Se verifica la conexión con el servidor
			if (autResponseSyncWrapper.getSync() == 1) {
				AutenticacionActivity.cambiarEstadoConectado(actividad
						.getApplicationContext());
				// Analizamos la lista de novedades para ejecutar las tareas
				for (NovSyncWrapper novSyncWrapper : autResponseSyncWrapper
						.getListaNovedades()) {
					// Asignamos los datos de la novedad en el servidor en un
					// objeto local de Novedades
					novedades = AutenticadorSyncBL
							.convertirNovedades(novSyncWrapper);
					if (novSyncWrapper.getTipoNovedad() == Integer
							.parseInt(actividad.getResources().getString(
									R.string.certificado_impreso))) { // El tipo
																		// de
																		// novedad,
																		// es de
																		// tipo
																		// 9: SI
						certificadoImpreso = false;
						break; // Fin de Ciclo si encuentra es novedad tipo 9:
								// Certificado Impreso.
					} else if (novSyncWrapper.getTipoNovedad() == Integer
							.parseInt(actividad.getResources().getString(
									R.string.duplicado_impreso))) {
						certificadoImpreso = false;
					}
				}
			} else if (autResponseSyncWrapper.getSync() == 0) {// el elector no
																// tiene
																// novedades en
																// tabla
				AutenticacionActivity.cambiarEstadoConectado(actividad
						.getApplicationContext());
			} else if (autResponseSyncWrapper.getSync() == 2) {// No hay
																// conexión en
																// base de
																// datos.
				AutenticacionActivity.cambiarEstadoDesconectado(actividad
						.getApplicationContext());
			}
			/*
			 * Al terminar el ciclo en la lista de novedades, se verifica el
			 * valor de la bandera "certificadoImpreso", con el fin de saber si
			 * se debe imprimir comprobante.
			 */
			if (certificadoImpreso) {
				// 1. Imprime primer comprobante de autenticación.
				impresionComprobante();
			} else {
				try {
					// Consulta en base de datos local si ya se imprimió
					// duplicado del comprobante
					if (novedadesBl
							.isDuplicadoImpreso(AutenticacionActivity.censo
									.getCedula())) {
						// Novedad Intento de nueva impresión de comprobante.
						novedadesBl.notificarIntentoReimpresionComprobante(
								AutenticacionActivity.censo.getCedula(),
								AutenticacionActivity.censo.getCodProv(),
								AutenticacionActivity.censo.getCodMpio(),
								AutenticacionActivity.censo.getCodZona(),
								AutenticacionActivity.censo.getCodColElec(),
								AutenticacionActivity.censo.getCodMesa(),
								AutenticacionActivity.censo.getTipoElector());
						// Si intenta imprimir más de dos veces, se muestra
						// mensaje M29
						AlertDialog cuadDialogo = Util.mensajeAceptar(
								actividad,
								R.style.TemaDialogo,
								actividad.getResources().getString(
										R.string.title_activity_login),
								actividad.getResources()
										.getString(R.string.M29),
								Util.DIALOG_ERROR, new OnClickListener() {
									// Método para el botón aceptar del Mensaje
									// M29
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Aceptar, limpia los datos del electos
										AutenticacionActivity.removerDatos();
										// Termina la actividad
										// ElectorYaAutenticado
										actividad.finish();
									}
								});

						cuadDialogo.show();

						Util.cambiarLineaDialogos(cuadDialogo, actividad);
					} else {
						// Consulta en base de datos de centralización
						// novedades de impresión de duplicados
						ConsultarDuplicadoImpresoAsyncTask task = new ConsultarDuplicadoImpresoAsyncTask(
								actividad, duplicado, score);
						task.execute(AutenticacionActivity.censo.getCedula());
					}
				} catch (NovedadesBLException e) { // Exepción no controlada en
													// NovedadesBLException
					log4jDroid
							.error("AutenticadorAndroidProject:ConsultarPrimerComprobanteImpresoAsyncTask:doInBackground:",
									e);
				}
			}
		}
	}
}
