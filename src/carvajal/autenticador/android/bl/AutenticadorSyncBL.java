package carvajal.autenticador.android.bl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.bl.exception.ConfiguracionBLException;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.write.Novedades;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.AutenticadorGsonConverter;
import carvajal.autenticador.android.util.exception.AutenticadorGsonConverterException;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import carvajal.autenticador.android.util.keyczar.autenticador.AutenticadorKeyczarCrypterException;
import carvajal.autenticador.android.util.wrappers.AutResponseSyncWrapper;
import carvajal.autenticador.android.util.wrappers.AutSyncWrapper;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.NovSyncWrapper;
import carvajal.autenticador.android.util.wrappers.TestWrapper;
import android.content.Context;

/**
 * Clase encargada de la gestión de la comunicación entre el dispositivo móvil
 * y los métodos disponibles para consumir en el servicio web, a través del
 * método post utilizando el protocolo httprequest.
 * 
 * @author davparpa
 * @version 1.0
 * @since   17 de Abril de 2015
 *
 */
public class AutenticadorSyncBL {

	/**
	 * localizador de recursos uniformes del servidor central
	 */
	private String url;
	
	/**
	 * método específico alojado en el servicio web
	 */
	public String metodo;
	
	/**
	 * Instancia para realizar el proceso de encriptación y desencriptación de Strings
	 */
	private AutenticadorKeyczarCrypter crypter;
	
	private final int TIMEOUT;

	/**
	 * Constructor de la clase AutenticadorSyncBL, permite parametrizar el método que 
	 * se quiere implementar al momento de consumir el servicio web.
	 * 
	 * @param actividad
	 * @param meotodo
	 * @throws AutenticadorSyncBLException
	 */
	public AutenticadorSyncBL(Context context, String meotodo)
			throws AutenticadorSyncBLException {
		//	Se asigna el valor de la url que viene de la configuración
		try {
			/*
			 * El valor de la url del servidor, se toma de la
			 * configuración activa del dispositivo móvil.
			 */
			Configuracion configuracion = new Configuracion();
			ConfiguracionBL configuracionBL = new ConfiguracionBL(
					context);
			configuracion = configuracionBL.obtenerConfiguracionActiva();
			TIMEOUT = Integer.parseInt(context.getResources().getString(R.string.milisegundos_timeout));
			if(configuracion.getIPServidor().equals("0")){
				this.url = context.getResources().getString(R.string.url_servicio);
			}
			else{
				this.url = configuracion.getIPServidor();
			}			
		} catch (AutenticadorDaoMasterSourceException e) { 
			//	Exepción no controlada de tipo AutenticadorDaoMasterSourceException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ConfiguracionBLException e) {
			//	Exepción no controlada de tipo ConfiguracionBLException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		// se asigna el nombre del método alojado en el servicio web
		this.metodo = meotodo;
		try {
			// Se inicializa el objeto Crypter
			this.crypter = AutenticadorKeyczarCrypter.getInstance(context
					.getResources());
		} catch (AutenticadorKeyczarCrypterException e) {
			// Exepción no controlada al inicializar el objeto crypter
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
	}

	/**
	 * Función que realiza un TEST utilizando el protocolo HttpRequest, 
	 * con el fin de establecer la coneción con el servidor central.
	 * 
	 * @throws AutenticadorSyncBLException
	 */
	public TestWrapper obtenerTestMetodo() throws AutenticadorSyncBLException {
		TestWrapper testWrapper = null;
		// Variable response de la ejecución del método al servicio web.
		String responseString = "-1";
		try {
			// Creación de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			//	Se instancian los parámetros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
		    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		    //	Se asignan dichos parmétros al client
		    client = new DefaultHttpClient(httpParams);
		    //	Definición del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Ejecución de la petición al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petición
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto TestWrapper
			testWrapper = AutenticadorGsonConverter
					.convertJSONToTestWrapper(responseStringDesencriptado);
		} catch (ClientProtocolException e) {// Exepción no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepción no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorGsonConverterException e) {// Exepción no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepción no controlada de AutenticadorKeyczarCrypterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		return testWrapper;
	}

	/**
	 * Función que obtiene un objeto de tipo AutResponseSyncWrapper utilizando el protocolo HttpRequest, 
	 * con el fin de establecer la coneción con el servidor central. con este objeto se puede validar
	 * que un elector, a partir de su número de cedula, se ha Autenticado en otra estación.
	 * 
	 * @param cedula
	 * @return autResponseSyncWrapper
	 * @throws AutenticadorSyncBLException
	 */
	public AutResponseSyncWrapper obtenerAutenticadoByCed (String cedula) throws 
		AutenticadorSyncBLException{
		// Variable de retorno
		AutResponseSyncWrapper autResponseSyncWrapper = null;
		// Variable response de la ejecución del método al servicio web.
		String responseString = "-1";
		try {
			//	El servicio web recibe como parámetro un objeto de tipo AutSyncWrapper
			AutSyncWrapper autSyncWrapper = new AutSyncWrapper();
			//	Se asigna el valor de la cédula del elector al objeto AutSyncWrapper
			autSyncWrapper.setCedula(cedula);
			/* Eventualmente convertimos el objeto AutSyncWrapper en un String en ofrmato Json
			 * después, dicho String debe ser encriptado.
			 */
			String JsonStringEncriptado = crypter
					.encrypt(AutenticadorGsonConverter
							.convertAutSyncWrapperToJson(autSyncWrapper));
			// Creación de variable cliente http.
			HttpClient client = new DefaultHttpClient();
            //	Se instancian los parámetros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			//	Se asignan dichos parmétros al client
		    client = new DefaultHttpClient(httpParams);
			//	Definición del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Asignación del Json encriptado en la petición
			post.setEntity(new StringEntity(JsonStringEncriptado, "UTF-8"));
			//	Ejecución de la petición al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petición
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto AutResponseSyncWrapper
			autResponseSyncWrapper = AutenticadorGsonConverter
					.convertJSONToAutResponseSyncWrapper(responseStringDesencriptado);
		} catch (AutenticadorGsonConverterException e) {// Exepción no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (UnsupportedEncodingException e) {// Exepción no controlada de UnsupportedEncodingException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ClientProtocolException e) {// Exepción no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepción no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepción no controlada de AutenticadorKeyczarCrypterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		//	Se retorna el objeto AutResponseSyncWrapper
		return autResponseSyncWrapper;
	}
	
	/**
	 * función implementada para convertir la instancia de tipo novSyncWrapper
	 * al tipo de instancia Novedades, con el fin de obtener los datos
	 * que vienen de una consulta del servidor
	 * 
	 * @param novSyncWrapper instancia que viene del servidor central
	 * @return Novedades objeto del BL que gestiona la novedad 
	 */
	public static Novedades convertirNovedades(NovSyncWrapper novSyncWrapper){
		//	Instancia de retorno
		Novedades novedad = new Novedades();
		//	Se asignan los campos del novSyncWrapper a la novedad
		novedad.setAndroidId(novSyncWrapper.getandroidId());
		novedad.setCedula(novSyncWrapper.getCedula());
		novedad.setFechaNovedad(String.valueOf(novSyncWrapper.getFechaNovedad()));
		novedad.setScore(String.valueOf(novSyncWrapper.getScore()));
		novedad.setTemplateHit(String.valueOf(novSyncWrapper.getTemplateHit()));
		novedad.setTipoNovedad(String.valueOf(novSyncWrapper.getTipoNovedad()));
		novedad.setTipoElector(String.valueOf(novSyncWrapper.getTipoElector()));
		novedad.setCodMesa(novSyncWrapper.getCodMesa());
		return novedad;
	}

	/**
	 * Función que utiliza la función del servidor para insertar una lista de
	 * novedades pendientes a la base de datos del servidor central. la lista de
	 * novedades se envia en una instancia de la clase ListaNovedadSyncWrapper
	 * compuesta de una lista de novedades tipo NovSyncWrapper
	 * 
	 * @param listaNovedadesWrapper instancia con la lista de novedades.
	 * @return listaNovedadesWrapper instancia con la respuesta de cada inserción de novedades.
	 * @throws AutenticadorSyncBLException
	 */
	public ListaNovedadSyncWrapper insertaNovedad(
			ListaNovedadSyncWrapper listaNovedadesWrapper) throws 
			AutenticadorSyncBLException{
		// Variable de retorno
		ListaNovedadSyncWrapper listaNovedadesResponseWrapper = null;
		// Variable response de la ejecución del método al servicio web.
		String responseString = "-1";	
		try {

			/* Eventualmente convertimos el objeto ListaNovedadSyncWrapper en un String en ofrmato Json
			 * después, dicho String debe ser encriptado.
			 */
			String JsonStringEncriptado = crypter
					.encrypt(AutenticadorGsonConverter
							.convertListaNovedadSyncWrapperToJson(listaNovedadesWrapper));
			// Creación de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			// Se instancian los parámetros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			//	Se asignan dichos parmétros al client
			client = new DefaultHttpClient(httpParams);
			//	Definición del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Asignación del Json encriptado en la petición
			post.setEntity(new StringEntity(JsonStringEncriptado, "UTF-8"));
			//	Ejecución de la petición al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petición
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto ListaNovedadSyncWrapper
			listaNovedadesResponseWrapper = AutenticadorGsonConverter
					.convertJSONToListaNovedadSyncWrapper(responseStringDesencriptado);
		} catch (AutenticadorGsonConverterException e) {// Exepción no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (UnsupportedEncodingException e) {// Exepción no controlada de UnsupportedEncodingException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ClientProtocolException e) {// Exepción no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepción no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepción no controlada de AutenticadorKeyczarCrypterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		//	Se retorna el objeto ListaNovedadSyncWrapper
		return listaNovedadesResponseWrapper;
	}
	
	public String imprimirReportePDF(File archivoPDF) throws AutenticadorSyncBLException{
		String responseString = "-1";	
		FileInputStream fileInputStream=null;
        byte[] byeArrayPDF = new byte[(int) archivoPDF.length()];
        try {
		    fileInputStream = new FileInputStream(archivoPDF);
		    fileInputStream.read(byeArrayPDF);
		    fileInputStream.close();
		    try {
		    	// Creación de variable cliente http.
	        	HttpClient client = new DefaultHttpClient();
				// Se instancian los parámetros del request
				final HttpParams httpParams = new BasicHttpParams();
				//	se define un timeout de 4 segundos
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
				//	Se asignan dichos parmétros al client
				client = new DefaultHttpClient(httpParams);
				//	Definición del servicio post
				HttpPost post = new HttpPost(url + metodo);
				//	Asignación del Json encriptado en la petición
				post.setEntity(new ByteArrayEntity(byeArrayPDF));
				//	Ejecución de la petición al servicio web
				HttpResponse response = client.execute(post);
				//	Captura de la respuesta a la petición
				responseString = EntityUtils.toString(response.getEntity());
				return responseString;
	        } catch (IOException e) {// Exepción no controlada de IOException
				throw new AutenticadorSyncBLException(e.getLocalizedMessage()
						.toString(), e);
			}
        }catch(Exception e){
        	throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
        }
	}
	
	/**
	 * Función que realiza un TEST al servicio de impresión 
	 * 
	 * @return respuesta de la petición al servicio.
	 * @throws AutenticadorSyncBLException
	 */
	public String testImpresoraSync() throws AutenticadorSyncBLException{
		//	Valor por defecto de falla
		String responseString = "3";
		try {
			// Creación de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			// Se instancian los parámetros del request
			final HttpParams httpParams = new BasicHttpParams();
			// se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			// Se asignan dichos parmétros al client
			client = new DefaultHttpClient(httpParams);
			// Definición del servicio post
			HttpPost post = new HttpPost(url + metodo);
			// Ejecución de la petición al servicio web
			HttpResponse response = client.execute(post);
			// Captura de la respuesta a la petición
			responseString = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {// Exepción no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		//	se retorna el resultado del test.
		return responseString;
	}

}
