package carvajal.autenticador.android.activity.dialogo;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.activity.ReporteActivity;
import carvajal.autenticador.android.activity.asynctask.AutenticadoAsistidoDelegadoAsyncTask;
import carvajal.autenticador.android.activity.asynctask.ConsultarComprobanteImpresoAsyncTask;
import carvajal.autenticador.android.activity.asynctask.ConsultarDuplicadoImpresoAsyncTask;
import carvajal.autenticador.android.activity.asynctask.ImprimeReporteAutenticadosAsyncTask;
import carvajal.autenticador.android.bl.AutenticacionBL;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.exception.AutenticacionBLException;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.dal.greendao.write.Novedades;
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * La Clase AutorizaDelegadoDialogFragment encapsula las caracterisitica,
 * comportamiento y validaciones del dialogo que se invoca, para porder
 * autorizar el delegado.
 * 
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since 17 de Febrero de 2015
 */
public class AutorizaDelegadoDialogFragment extends DialogFragment {
	/** instancia única de la clase Singleton POSSDKManager. permite imprimir */
	private POSSDKManager possdkImprimir;
	/**
	 * Campo requerido para conocer el tipo de novedad que se ingresa al
	 * imprimir.
	 */
	public int tipoNovedad;

	/*
	 * Valor del dedo del elector que se autentica
	 */
	public Integer score;

	/*
	 * Valor de la huella del dedo del elector que se autentica
	 */
	public Integer hit;

	/** Campo requerido para saber si el comprobante se imprime duplicado o no. */
	public boolean duplicado;

	/**
	 * Clase para encriptar y desencriptar
	 */
	AutenticadorKeyczarCrypter crypter;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	/*
	 * Actividad predecesora.
	 */
	public Activity actividad;

	// Se instancia un objeto de novedades para guardar las
	// novedades en cada caso
	public NovedadesBL novedadesBl = null;

	/**
	 * Novedad para validar la autenticacion ya realizada.
	 */
	public static Novedades novedades = null;

	/**
	 * ConfiguracionBL para acceder a los métodos de la tabla Configuracion
	 */
	public ConfiguracionBL configuracionBL;

	/**
	 * Método constructor del dialogo.
	 * 
	 * @param tipoNovedad
	 *            requerido para conocer el tipo de novedad que se ingresa al
	 *            imprimir.
	 * @param duplicado
	 *            requerido para saber si el comprobante se imprime duplicado o
	 *            no.
	 */
	public AutorizaDelegadoDialogFragment(int tipoNovedad, boolean duplicado,
			Integer score, Integer hit) {
		this.duplicado = duplicado;
		this.tipoNovedad = tipoNovedad;
		this.score = score;
		this.hit = hit;
	}

	/*
	 * Método que se invoca al crear el diálogo, en el se inicializan la
	 * instancia de impresión y debe retornar un AlertDialog construido.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Inicializar la instancia de impresión.
		possdkImprimir = POSSDKManager.getInstance(getActivity());

		try {
			crypter = AutenticadorKeyczarCrypter.getInstance(getResources());
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onCreateDialog:",
							e);
		}

		// Retorna el método de tipo Dialogo
		return crearDialogoAutorizaDelegado();
	}

	/**
	 * En este método de tipo Dialog, se lleva a cabo la lógica de la creación,
	 * validación de novedades y flujo d eimpresión para el cual, el diálogo es
	 * invocado.
	 * 
	 * @return dialog objeto dialgoo construido y generado.
	 */
	@SuppressLint("InflateParams")
	private Dialog crearDialogoAutorizaDelegado() {
		this.actividad = getActivity();
		// Declaración del dialogo que se retorna en el método
		AlertDialog dialog = null;
		// Construcción del diálogo.
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
				R.style.TemaDialogo);
		// Con el siguiente código, se impide que el diálogo se cierre, al dar
		// tap en el botón aceptar, si la contraseña es inválida.
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialogo_autorizar_delegado,
				null);
		// Título del dialogo
		builder.setTitle(R.string.titulo_dialogo);
		// Creación de los botones Positivo y Negativo del diálogo
		builder.setView(v).setPositiveButton(android.R.string.ok, null)
				.setNegativeButton(android.R.string.cancel, null);
		// Creación del diálogo a través del builder
		dialog = builder.create();
		// Se evita que se cierre el diálogo dando tap atrás de este mismo.
		dialog.setCanceledOnTouchOutside(false);
		// ShowListener se invoca al preparar el Dialogo en la actvity que lo
		// invoca
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			/*
			 * En el método onShow, se programa la funcionalidad del botón
			 * aceptar
			 */
			@Override
			public void onShow(final DialogInterface dialog) {
				// Se instancia el botón Aceptar del dialogo

				Dialog dial = ((Dialog) dialog);
				Util.cambiarLineaDialogos(dial,
						actividad.getApplicationContext());
				Button buttonNegative = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_NEGATIVE);
				buttonNegative.setText(actividad.getResources().getString(
						R.string.cancelar));
				Button buttonPositive = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_POSITIVE);
				buttonPositive.setText(actividad.getResources().getString(
						R.string.button_ok));
				// Se genera la interface del método onClickListener
				buttonPositive.setOnClickListener(new View.OnClickListener() {

					/*
					 * El método onclick del botón aceptar del diálogo
					 */
					@Override
					public void onClick(View view) {
						try {
							// Se construye el objeto novedad
							novedadesBl = new NovedadesBL(actividad
									.getApplicationContext());
						} catch (AutenticadorDaoMasterSourceException e) { // Excepción
																			// inesperada
																			// al
																			// construir
																			// el
																			// objeto
																			// novedad
							log4jDroid
									.error("A utenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
											e);
						}
						// SE cptura la contraseña ingresada
						EditText txtContrasena = (EditText) v
								.findViewById(R.id.txtContrasena);
						String contrasena = txtContrasena.getText().toString();
						// se crea objeto AutenticacionBL para validar el loggeo
						// del supervisor
						AutenticacionBL autenticacion = new AutenticacionBL(
								actividad);
						// Variable logeado que captura la validación del
						// usuario supervisor
						boolean logeado = false;
						try {
							// se hace la validación de logeo del usuario
							// supervisor
							logeado = autenticacion.validarDelegado(contrasena);
						} catch (AutenticacionBLException e) {// error
																// inesperado al
																// validar el
																// logeo de
																// usuario
																// supervisor
							log4jDroid
									.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
											e);
						}
						if (logeado) { // Contraseña correcta
							// Validaciones para cuando es YA_EUTENTICADO - FA1
							if (tipoNovedad == Integer.parseInt(getResources()
									.getString(R.string.ya_autenticado))) { // YA_AUTENTICADO
								try {
									// Verificar que comprobante de persona
									// autenticada NO se haya impreso
									if ((novedadesBl
											.isCertificadoImpreso(AutenticacionActivity.censo
													.getCedula()))) {
										// El certificado de la persona
										// autenticada SI SE imprimió antes
										// Verificar que comprobante no se trate
										// de imprimir más de dos veces
										if (novedadesBl
												.isDuplicadoImpreso(AutenticacionActivity.censo
														.getCedula())) {
											// si el comprobante ya se imprimió
											// dos veces: novedad_ Intento de
											// nueva
											novedadesBl
													.notificarIntentoReimpresionComprobante(
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
																	.getTipoElector());
											// Si intenta imprimir más de dos
											// veces, se muestra mensaje M29
											AlertDialog cuadDialogo = Util
													.mensajeAceptar(
															actividad,
															R.style.TemaDialogo,
															actividad
																	.getResources()
																	.getString(
																			R.string.title_activity_login),
															actividad
																	.getResources()
																	.getString(
																			R.string.M29),
															Util.DIALOG_ERROR,
															new OnClickListener() {
																// Método para
																// el botón
																// aceptar del
																// Mensaje M29
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	// Aceptar,
																	// limpia
																	// los datos
																	// del
																	// electos
																	AutenticacionActivity
																			.removerDatos();
																	// Termina
																	// la
																	// actividad
																	// ElectorYaAutenticado
																	actividad
																			.finish();
																}
															});

											cuadDialogo.show();

											Util.cambiarLineaDialogos(
													cuadDialogo, actividad);
										} else {
											/*
											 * Consulta en base de datos de
											 * centralización novedades de
											 * impresión de duplicados
											 */

											ConsultarDuplicadoImpresoAsyncTask task = new ConsultarDuplicadoImpresoAsyncTask(
													actividad, duplicado, score);

											task.execute(AutenticacionActivity.censo
													.getCedula());
											// new
											// ConsultarDuplicadoImpresoByCedAsyncTask().execute(AutenticacionActivity.censo
											// .getCedula());
										}
									} else {
										// El certificado de la persona
										// autenticada NO SE Imprimió antes

										/*
										 * Consulta en la base de datos de
										 * centratización si ya se imprimió el
										 * primer comprobante
										 */

										ConsultarComprobanteImpresoAsyncTask task = new ConsultarComprobanteImpresoAsyncTask(
												actividad, duplicado, score);
										task.execute(AutenticacionActivity.censo
												.getCedula());
										// new
										// ConsultarPrimerComprobanteImpresoAsyncTask().execute(AutenticacionActivity.censo
										// .getCedula());
									}
								} catch (NovedadesBLException e) {// Error
																	// inesperado
																	// al
																	// verificar
																	// la
																	// contraseña
																	// del
																	// usuario
																	// supervisor
									log4jDroid
											.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
													e);
								}
							}
							// Validaciones para cuando es
							// SIN_INFORMACION_BIOMETRICA ó
							// NO_SE_PUDO_AUTENTICAR - FA2 -
							else if ((tipoNovedad == Integer
									.parseInt(getResources()
											.getString(
													R.string.sin_informacion_biometrica)))
									|| (tipoNovedad == Integer
											.parseInt(getResources()
													.getString(
															R.string.no_se_pudo_autenticar)))
									|| (tipoNovedad == Integer
											.parseInt(getResources().getString(
													R.string.elector_impedido)))) {
								// Sincroniza autenticación hacia base de datos
								// de centralización

								// new
								// InsetarAutenticadoAsistidoDelegadoAsyncTask().execute();
								AutenticadoAsistidoDelegadoAsyncTask task = new AutenticadoAsistidoDelegadoAsyncTask(
										actividad, tipoNovedad, score, hit,
										duplicado);
								task.execute();

							} else if (tipoNovedad == Integer
									.parseInt(getResources()
											.getString(
													R.string.reporte_autenticacion_delegado))) {
								if (validarConfiguracion()) { // valida que
																// exista
																// configuración
																// activa
									// Si el diálogo se acciona al imprimir el
									// reporte de autenticación
									try {
										// Lista de cédulas autorizadas por el
										// Delegado.
										ArrayList<Long> listaCedulas = new ArrayList<Long>();
										// Se inicializa nuevamente el objeto de
										// novedadesBL
										novedadesBl = new NovedadesBL(actividad
												.getApplicationContext());
										for (Novedades novedad : novedadesBl
												.obtenerCedulasAutorizaDelegado()) {
											// Se obtienen las cédulas
											// autorizadas por el delegado
											// a través de las novedades
											listaCedulas.add(Long
													.parseLong(novedad
															.getCedula()));
										}
										// Finalmente se imprime el reporte
										possdkImprimir
												.imprimirReporteAutorizaDelegado(
														ReporteActivity.colegioDivipol,
														ReporteActivity.colegio,
														listaCedulas);
									} catch (NovedadesBLException e) { // Exepción
																		// inesperada
																		// tipo
																		// NovedadesBLException
										log4jDroid
												.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
														e);
									} catch (AutenticadorDaoMasterSourceException e) { // Exepción
																						// inesperada
																						// tipo
																						// AutenticadorDaoMasterSourceException
										log4jDroid
												.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
														e);
									} catch (POSSDKManagerException e) { // Exepción
																			// inesperada
																			// tipo
																			// POSSDKManagerException
										log4jDroid
												.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
														e);
									}
								}
							} else if (tipoNovedad == Integer
									.parseInt(getResources()
											.getString(
													R.string.reporte_total_autenticacion))) {
								// Si la autorización de delegado viene del
								// Reporte Total de Autenticaciones
								if (validarConfiguracion()) { // valida que
									// exista
									// configuración
									// activa
									try {
										// Lista de cédulas autorizadas por el
										// Delegado.
										ArrayList<String> listaCedulas = new ArrayList<String>();
										// Se inicializa nuevamente el objeto de
										// novedadesBL
										novedadesBl = new NovedadesBL(actividad
												.getApplicationContext());
										for (Novedades novedad : novedadesBl
												.obtenerElectoresAutenticados()) {
											// Se obtienen el total de cédulas
											// que se han autenticado a través de las novedades
											listaCedulas.add(novedad
															.getCedula());
										}
										// Finalmente se imprime el reporte
										ImprimeReporteAutenticadosAsyncTask task = 
												new ImprimeReporteAutenticadosAsyncTask(listaCedulas, actividad);
										task.execute();
									} catch (AutenticadorDaoMasterSourceException e) {
										// Exepción inesperada tipo AutenticadorDaoMasterSourceException
										log4jDroid
										.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
												e);
									} catch (NovedadesBLException e) {
										// Exepción inesperada tipo NovedadesBLException
										log4jDroid
										.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:onClick:",
												e);
									}
								}
							}
							// se desecha el diálogo
							dialog.dismiss();
						} else {
							// Contraseña Incorrecta se establece el texto de
							// nuevo vacio
							txtContrasena.setText("");
							// se muestra el mensaje M21
							AlertDialog cuadDialogo = Util.mensajeAceptar(
									actividad,
									R.style.TemaDialogo,
									getResources().getString(
											R.string.title_activity_login),
									getResources().getString(R.string.M21),
									Util.DIALOG_ERROR, null);
							cuadDialogo.show();

							Util.cambiarLineaDialogos(cuadDialogo, actividad);

						}

					}
				});
			}
		});

		return dialog;
	}

	/**
	 * valida que exista configuración activa Si el diálogo se acciona al
	 * imprimir el reporte de autenticación
	 * 
	 * @return
	 */
	private boolean validarConfiguracion() {
		try {
			boolean res = true;
			configuracionBL = new ConfiguracionBL(
					actividad.getApplicationContext());
			if (!Util.existeConfiguracion(configuracionBL)) {
				res = false;
			}
			return res;
		} catch (AutenticadorDaoMasterSourceException e) {// Excepción no
															// controlada de
															// AutenticadorDaoMasterSourceException
			log4jDroid
					.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:validarConfiguracion:",
							e);
			return false;
		}
	}
}
