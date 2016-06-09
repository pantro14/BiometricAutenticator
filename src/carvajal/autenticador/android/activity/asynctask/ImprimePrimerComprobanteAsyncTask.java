package carvajal.autenticador.android.activity.asynctask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;
import android.app.Activity;
import android.os.AsyncTask;

public class ImprimePrimerComprobanteAsyncTask extends
		AsyncTask<Void, Void, Void> {

	/*
	 * Wrapper que envía como respuesta, después de ejecutar la función del
	 * servicio web el cual, retorna la lista de novedades de una cédula
	 */
	ListaNovedadSyncWrapper listaNovedadesResponseWrapper;

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
	 * Valor del dedo del elector que se autentica
	 */
	public Integer score;

	/*
	 * Valor de la huella del dedo del elector que se autentica
	 */
	public Integer tipoNovedad;

	/*
	 * Se instancia un objeto de novedades para guardar las novedades en cada
	 * caso
	 */
	public NovedadesBL novedadesBl = null;

	public Integer YA_AUTENTICADO;
	public Integer NO_SE_PUDO_AUTENTICAR;
	public Integer SIN_INFORMACION_BIOMETRICA;
	public Integer ELECTOR_IMPEDIDO;

	public ImprimePrimerComprobanteAsyncTask(Activity actividad,
			Integer tipoNovedad, Integer score) {
		this.actividad = actividad;
		this.tipoNovedad = tipoNovedad;
		this.score = score;
		NO_SE_PUDO_AUTENTICAR = Integer.parseInt(actividad.getResources()
				.getString(R.string.no_se_pudo_autenticar));
		SIN_INFORMACION_BIOMETRICA = Integer.parseInt(actividad.getResources()
				.getString(R.string.sin_informacion_biometrica));
		YA_AUTENTICADO = Integer.parseInt(actividad.getResources().getString(
				R.string.ya_autenticado));
		
		ELECTOR_IMPEDIDO = Integer.parseInt(actividad.getResources().getString(
				R.string.elector_impedido));
		try {
			this.novedadesBl = new NovedadesBL(actividad);
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarImprimePrimerComprobanteAsyncTask:doInBackground:",
							e);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			// Declaración e instancia del objeto AutenticadorSyncBL
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(actividad,
					actividad.getResources().getString(
							R.string.metodo_insertar_novedad));
			// Se obtiene la MAC del dispositivo móvil
			String androidId = Util.obtenerMAC(actividad
					.getApplicationContext());
			// Se asigna el tipo de novedad.
			int tipoNovedad = Integer.parseInt(actividad
					.getApplicationContext().getString(
							R.string.certificado_impreso));
			// para las novedades de impresión el score y hint son cero.
			int idHuella = 0;
			score = 0;
			// Asigno las variables a insertar en el servidor central al wrapper
			// NovSyncWrapper
			NovSyncWrapper novedadSynWrapper = new NovSyncWrapper(androidId,
					AutenticacionActivity.censo.getCedula(), new Date(), score,
					idHuella, AutenticacionActivity.censo.getTipoElector(),
					tipoNovedad, 0, AutenticacionActivity.censo.getCodMesa());
			// Después este wrapper NovSyncWrapper, se almacena en la lista de
			// wrappers
			List<NovSyncWrapper> listanovedades = new ArrayList<NovSyncWrapper>();
			listanovedades.add(novedadSynWrapper);
			// La lista de wrappers de NovSyncWrapper, se almacena en el wrapper
			// ListaNovedadSyncWrapper
			ListaNovedadSyncWrapper listaNovedadesWrapper = new ListaNovedadSyncWrapper(
					listanovedades);
			// Se ejecuta la función que inserta las novedades en el servidor
			// central.
			listaNovedadesResponseWrapper = autSyncBL
					.insertaNovedad(listaNovedadesWrapper);
		} catch (AutenticadorSyncBLException e) {// Exepción no controlada de
													// AutenticadorSyncBLException
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarImprimePrimerComprobanteAsyncTask:doInBackground:",
							e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// Sincroniza novedad “Comprobante se imprimió satisfactoriamente”
		// La respuesta de sincronización llega al objeto
		// listaNovedadesResponseWrapper
		int sincronizado = 0; // Por defecto el valro sincro es Cero
		if (listaNovedadesResponseWrapper != null) { // La lista de novedades NO
														// viene nula
			for (NovSyncWrapper novSyncWrapper : listaNovedadesResponseWrapper
					.getListanovedades()) {// Se verifica para cada novedad,
				// las respuestas en el servidor central

				if (novSyncWrapper.getRespuesta() == Integer.parseInt(actividad
						.getString(R.string.novedad_no_sincronizada))) {// No
																		// Sincronizado
					sincronizado = novSyncWrapper.getRespuesta(); // Valor es
																	// Cero: 0.
					AutenticacionActivity.cambiarEstadoDesconectado(actividad
							.getApplicationContext());
				} else if (novSyncWrapper.getRespuesta() == Integer
						.parseInt(actividad
								.getString(R.string.novedad_sincronizada))) {// Sincronizado
					sincronizado = novSyncWrapper.getRespuesta(); // Valor es
																	// Uno: 1.
					AutenticacionActivity.cambiarEstadoConectado(actividad
							.getApplicationContext());
				}
			}
		} else {
			AutenticacionActivity.cambiarEstadoDesconectado(actividad
					.getApplicationContext());
		}
		try {
			// Comprobante se imprimió satisfactoriamente.
			if (novedadesBl.notificarCertificadoImpreso(
					AutenticacionActivity.censo.getCedula(),
					AutenticacionActivity.censo.getCodProv(),
					AutenticacionActivity.censo.getCodMpio(),
					AutenticacionActivity.censo.getCodZona(),
					AutenticacionActivity.censo.getCodColElec(),
					AutenticacionActivity.censo.getCodMesa(),
					AutenticacionActivity.censo.getTipoElector(), sincronizado)) {
				AutenticacionActivity.actualizarPorcentajeSincro(actividad
						.getApplicationContext());
			}
		} catch (NovedadesBLException e) {// Excepción inesperadan al guarda la
											// novedad
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarImprimePrimerComprobanteAsyncTask:onPostExecute:",
							e);

		} finally {
			// Retorna al activity anterior de autenticación
			AutenticacionActivity.removerDatos();
			if ((tipoNovedad == NO_SE_PUDO_AUTENTICAR)
					|| (tipoNovedad == YA_AUTENTICADO)
					|| (tipoNovedad == ELECTOR_IMPEDIDO)) {
				actividad.finish();
			}
		}
	}

}
