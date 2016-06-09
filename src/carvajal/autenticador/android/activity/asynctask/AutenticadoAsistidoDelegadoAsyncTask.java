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
import carvajal.autenticador.android.framework.possdk.POSSDKManager;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import carvajal.autenticador.android.util.keyczar.autenticador.AutenticadorKeyczarCrypterException;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;

public class AutenticadoAsistidoDelegadoAsyncTask extends
		AsyncTask<Void, Void, Void> {
	
	/*
	 * 	Wrapper que envía como respuesta, después de ejecutar la función del servicio web
	 * 	el cual, retorna la lista de novedades de una cédula
	 */
	ListaNovedadSyncWrapper listaNovedadesResponseWrapper;
	
	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);
	
	/*
	 * 	Actividad predecesora.
	 */
	public Activity actividad;
	
	/*
	 * Valor del dedo del elector que se autentica
	 */
	public Integer score;
	
	/*
	 * Valor de la huella del dedo del elector que se autentica
	 */
	public Integer hit;
	
	/*
	 * Valor de la huella del dedo del elector que se autentica
	 */
	public Integer tipoNovedad;
	
	/* Se instancia un objeto de novedades para guardar las
	 * novedades en cada caso
	 */
	public NovedadesBL novedadesBl = null;
	
	/* instancia única de la clase Singleton POSSDKManager. permite imprimir */
	private POSSDKManager possdkImprimir;
	
	/**
	 * Clase para encriptar y desencriptar
	 */
	private AutenticadorKeyczarCrypter crypter;
	
	/** Campo requerido para saber si el comprobante se imprime duplicado o no. */
	public boolean duplicado;

	public AutenticadoAsistidoDelegadoAsyncTask(Activity actividad, Integer tipoNovedad,
			Integer score, Integer hit, boolean duplicado) {
		this.actividad = actividad;
		this.tipoNovedad = tipoNovedad;
		this.score = score;
		this.hit = hit;
		this.duplicado = duplicado;
		try {
			this.novedadesBl = new NovedadesBL(actividad);
			this.possdkImprimir = POSSDKManager.getInstance(actividad);
			this.crypter = AutenticadorKeyczarCrypter.getInstance(actividad.getResources());
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid.
			error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
					e);
		} catch (AutenticadorKeyczarCrypterException e) {
			log4jDroid.
			error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
					e);
		}
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			
			//	Declaración e instancia del objeto AutenticadorSyncBL
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
					actividad, actividad.getResources().getString(
							R.string.metodo_insertar_novedad));
			//	Cuando se inserta la novedad 11: Autorizado Delegado, los valores hit y score, pueden ser nulos
			//	Por lo tanto se les asigna el valor de cero, si no se asigna el valor agregado por constructor.
			int localhit = 0;
			if(hit == null ){
				localhit =0;
			}else{
				localhit = hit;
			}
			int localScore = 0;
			if(score == null){
				localScore = 0;
			}
			else{
				localScore = score;
			}
			//	Se obtiene la MAC del dispositivo móvil
			String androidId = Util.obtenerMAC(actividad);
			//	Se asigna el tipo de novedad, para este caso 11: Autenticacion Autorizada Delegado
			int tipoNovedad = Integer.parseInt(actividad.getString(
							R.string.autenticacion_asistida_delegado));
			//	Asigno las variables a insertar en el servidor central al wrapper NovSyncWrapper
			NovSyncWrapper novedadSynWrapper = new NovSyncWrapper(
					androidId, AutenticacionActivity.censo.getCedula(),
					new Date(), localScore,
					localhit, AutenticacionActivity.censo.getTipoElector(),
					tipoNovedad, 0, AutenticacionActivity.censo.getCodMesa());
			//	Después este wrapper NovSyncWrapper, se almacena en la lista de wrappers
			List<NovSyncWrapper> listanovedades = new ArrayList<NovSyncWrapper>();
			listanovedades.add(novedadSynWrapper);
			//	La lista de wrappers de NovSyncWrapper, se almacena en el wrapper ListaNovedadSyncWrapper
			ListaNovedadSyncWrapper listaNovedadesWrapper = new ListaNovedadSyncWrapper(
					listanovedades);
			//	Se ejecuta la función que inserta las novedades en el servidor central.
			listaNovedadesResponseWrapper = autSyncBL
					.insertaNovedad(listaNovedadesWrapper);
		} catch (AutenticadorSyncBLException e) {// Exepción no controlada de AutenticadorSyncBLException
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:doInBackground:",
							e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		int sincronizado = 0; // Por defecto el valro sincro es Cero
		if(listaNovedadesResponseWrapper!=null){	// La lista de novedades NO viene nula
			for (NovSyncWrapper novSyncWrapper : listaNovedadesResponseWrapper
					.getListanovedades()) { // Se verifica para cada novedad, 
				//	las respuestas en el servidor central
				if(novSyncWrapper.getRespuesta()==Integer.parseInt(actividad.getString(
						R.string.novedad_no_sincronizada))){// No Sincronizado
					sincronizado = novSyncWrapper.getRespuesta();
					AutenticacionActivity.cambiarEstadoDesconectado(actividad.getApplicationContext());
				}else if(novSyncWrapper.getRespuesta()==Integer.parseInt(actividad.getString(
						R.string.novedad_sincronizada))){// Sincronizado
					sincronizado = novSyncWrapper.getRespuesta();
					AutenticacionActivity.cambiarEstadoConectado(actividad.getApplicationContext());
				}	
			}
		}else{
			AutenticacionActivity.cambiarEstadoDesconectado(actividad.getApplicationContext());
		}
		try {
			// Se guarda la novedad de autenticación, con el hit y score
	 		if (novedadesBl.notificarAutenticadoDelegado(
					AutenticacionActivity.censo.getCedula(),
					AutenticacionActivity.censo.getCodProv(),
					AutenticacionActivity.censo.getCodMpio(),
					AutenticacionActivity.censo.getCodZona(),
					AutenticacionActivity.censo.getCodColElec(),
					AutenticacionActivity.censo.getCodMesa(),
					AutenticacionActivity.censo.getTipoElector(), hit,
					score,sincronizado)){
	 			AutenticacionActivity.actualizarPorcentajeSincro
				(actividad.getApplicationContext());
				try {
					// Variable que captura el éxito al imprimir
					boolean exitoImprimir;
					if (AutenticacionActivity.censo.getTipoElector() == 1) {// ¿El elector es jurado de  votación?
						// Elector SI ES JURADO Se imprime el comprobante con la información requerida
						exitoImprimir = possdkImprimir
								.imprimirComprobante(actividad.getResources().getString(R.string.nombre_eleccion),
										AutenticacionActivity.censo.getCedula(),
										String.valueOf(Integer.valueOf(AutenticacionActivity.censo.getCodMesa())),
										Util.obtenerFechaDeImpresion(new Date(
												Long.parseLong(crypter.decrypt(novedadesBl.obtenerAutenticado(
																		AutenticacionActivity.censo.getCedula())
																.getFechaNovedad())))),duplicado, true);

					} else {
						// Elector NO Jurado Se imprime el comprobante con la información requerida
						exitoImprimir = possdkImprimir
								.imprimirComprobante(
										actividad.getResources().getString(R.string.nombre_eleccion),
										AutenticacionActivity.censo.getCedula(),
										String.valueOf(Integer.valueOf(AutenticacionActivity.censo.getCodMesa())),
										Util.obtenerFechaDeImpresion(new Date(
												Long.parseLong(crypter.decrypt(novedadesBl.obtenerAutenticado(
																		AutenticacionActivity.censo
																				.getCedula())
																.getFechaNovedad())))),duplicado, false);
					}
					if (exitoImprimir) { // ¿Imprime comprobanteb de autenticación'
						// Sincroniza novedad “Comprobante se imprimió satisfactoriamente” hacia base de datos de centralización
						ImprimePrimerComprobanteAsyncTask task = new ImprimePrimerComprobanteAsyncTask(actividad, tipoNovedad, score);
						task.execute();
					}else {
						// No se pudo imprimir comp0robante, se muestra el mensaje M27
						AlertDialog cuadDialogo=	Util.mensajeAceptar(
								actividad,R.style.TemaDialogo,
								actividad.getResources()
										.getString(
												R.string.title_activity_login),
								actividad.getResources().getString(
										R.string.M27),
								Util.DIALOG_ERROR, null);
						
						cuadDialogo.show();

						Util.cambiarLineaDialogos(cuadDialogo, actividad);
					}
				} catch (POSSDKManagerException e) { // Error
					// inesperado al comprobar la conexión con la impresora No accede correctamente a la impresora
					// se muestra el mensaje M27
					AlertDialog cuadDialogo=Util.mensajeAceptar(actividad, R.style.TemaDialogo,actividad.getResources()
								.getString(
									R.string.title_activity_login), actividad.getResources().getString(R.string.M27),
										Util.DIALOG_ERROR, null);
					
					cuadDialogo.show();

					Util.cambiarLineaDialogos(cuadDialogo, actividad);
					log4jDroid.
					error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
				} catch (NovedadesBLException e) { // Exepción no0 controlada NovedadesBLException
					log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
				} catch (NumberFormatException e) { // Exepción no0 controlada NumberFormatException
					log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e); 
				} catch (NotFoundException e) { // Exepción no0 controlada NotFoundException
					log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
				} catch (AutenticadorKeyczarCrypterException e) { // Exepción no0 controlada AutenticadorKeyczarCrypterException
					log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
				}
			}
		} catch (NovedadesBLException e) {// error	inesperado al comprobar el tipo de autorización  que se pide
			log4jDroid
					.error("AutenticadorAndroidProject:InsetarAutenticadoAsistidoDelegadoAsyncTask:onPostExecute:",
							e);
		}
	}
	
	

}
