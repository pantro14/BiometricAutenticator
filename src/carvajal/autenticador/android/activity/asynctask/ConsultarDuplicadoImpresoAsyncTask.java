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

public class ConsultarDuplicadoImpresoAsyncTask extends
		AsyncTask<String, Void, Void> {
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

	public ConsultarDuplicadoImpresoAsyncTask(Activity actividad,
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
	 * Imprime primer comprobante de autenticación DUPLICADO.
	 */
	public void impresionComprobanteDuplicado() {
		// El comprobante se va a imprimir
		// como DUPLICADO
		try {
			// Se establece la fecha que se va a guardar al imprimir. y que se
			// almacenó
			// en el servidor central
			Date date = null;
			// Si novedad es nula el servidor no retornó respuesta
			if (novedades == null) {
				try {
					// Se obtiene la fecha de base de datos local
					date = new Date(Long.parseLong(crypter.decrypt(novedadesBl
							.obtenerAutenticado(
									AutenticacionActivity.censo.getCedula())
							.getFechaNovedad())));
				} catch (AutenticadorKeyczarCrypterException e) {
					log4jDroid
							.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
									e);
				} catch (NovedadesBLException e) {
					log4jDroid
							.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
									e);
				}
			} else {
				// Si no, se obtiene loa fecha del servidor
				DateFormat format = new SimpleDateFormat(
						"EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
				date = format.parse(novedades.getFechaNovedad());
			}
			// Variable que captura el éxito al imprimir
			boolean exitoImprimir;
			if (AutenticacionActivity.censo.getTipoElector() == 1) {
				// Elector SI ES JURADO, el campo esJurado va true
				// Se imprime el comprobante con la información requerida
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
				// Elector NO Jurado, el campo esJurado se va false Se imprime
				// el comprobante
				// con la información requerida
				exitoImprimir = possdkImprimir.imprimirComprobante(actividad
						.getResources().getString(R.string.nombre_eleccion),
						AutenticacionActivity.censo.getCedula(), String
								.valueOf(Integer
										.valueOf(AutenticacionActivity.censo
												.getCodMesa())), Util
								.obtenerFechaDeImpresion(date), duplicado,
						false);
			}
			if (exitoImprimir) { // ¿Imprime comprobante de autenticación?
				// Sincroniza novedad “impresión de duplicado” hacia la base de
				// datos de centralización
				ImprimeComprobanteDuplicadoAsyncTask task = new ImprimeComprobanteDuplicadoAsyncTask(
						actividad, score);
				task.execute();
			} else {
				// No se pudo imprimir
				AlertDialog cuadDialogo = Util.mensajeAceptar(actividad,
						R.style.TemaDialogo, actividad.getResources()
								.getString(R.string.title_activity_login),
						actividad.getResources().getString(R.string.M27),
						Util.DIALOG_ERROR, null);

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, actividad);
			}
		} catch (POSSDKManagerException e) {
			// Error insperado, No accede correctamente a la impresora
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
					.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
							e);
		} catch (NumberFormatException e) {// Exepción no controlada
											// NumberFormatException
			log4jDroid
					.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
							e);
		} catch (NotFoundException e) {// Exepción no controlada
										// NotFoundException
			log4jDroid
					.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
							e);
		} catch (ParseException e) {// Exepción no controlada ParseException
			log4jDroid
					.error("AutenticadorAndroidProjectConsultarDuplicadoImpresoByCedAsyncTask:impresionComprobanteDuplicado:",
							e);
		}
		// Se finaliza el diálogo y la actividad.
		actividad.finish();
	}

	@Override
	protected Void doInBackground(String... cedula) {
		// Consulta en base de datos de centralización novedades de impresión de
		// duplicados
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
					.error("AutenticadorAndroidProject:ConsultarDuplicadoImpresoByCedAsyncTask:doInBackground:",
							e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (autResponseSyncWrapper == null) {// SI el wrapper viene nulo
			// Imprime duplicado de comprobante de autenticación
			impresionComprobanteDuplicado();
		} else {
			// Bandera que permite saber si el duplicado se ha impreso
			boolean duplicadoImpreso = false;
			// Se verifica la conexión con el servidor
			if (autResponseSyncWrapper.getSync() == 1) {
				AutenticacionActivity.cambiarEstadoConectado(actividad
						.getApplicationContext());
				// Se verifica en cada una de las novedadesw y se ejecuta la
				// tarea dependiendo del tipo de novedad
				for (NovSyncWrapper novSyncWrapper : autResponseSyncWrapper
						.getListaNovedades()) {
					// Asignamos los datos del servidor en un objeto local de
					// Novedades
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
						duplicadoImpreso = false;
					} else if (novSyncWrapper.getTipoNovedad() == Integer
							.parseInt(actividad.getResources().getString(
									R.string.duplicado_impreso))) {
						duplicadoImpreso = true;
						break;
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
			if (duplicadoImpreso == false) {// Se imprimió solo una vez
				impresionComprobanteDuplicado();
			} else if (duplicadoImpreso == true) {
				try {
					novedadesBl.notificarIntentoReimpresionComprobante(
							AutenticacionActivity.censo.getCedula(),
							AutenticacionActivity.censo.getCodProv(),
							AutenticacionActivity.censo.getCodMpio(),
							AutenticacionActivity.censo.getCodZona(),
							AutenticacionActivity.censo.getCodColElec(),
							AutenticacionActivity.censo.getCodMesa(),
							AutenticacionActivity.censo.getTipoElector());
				} catch (NovedadesBLException e) {
					log4jDroid
							.error("AutenticadorAndroidProject:ConsultarDuplicadoImpresoByCedAsyncTask:onPostExecute:",
									e);
				}
				// Si intenta imprimir más de dos veces, se muestra mensaje M29
				AlertDialog cuadDialogo = Util.mensajeAceptar(actividad,
						R.style.TemaDialogo, actividad.getResources()
								.getString(R.string.title_activity_login),
						actividad.getResources().getString(R.string.M29),
						Util.DIALOG_ERROR, new OnClickListener() {
							// Método para el botón aceptar del Mensaje M29
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Aceptar, limpia los datos del electos
								AutenticacionActivity.removerDatos();
								// Termina la actividad ElectorYaAutenticado
								actividad.finish();
							}
						});

				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, actividad);
			}
		}
	}
}
