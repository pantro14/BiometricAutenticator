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
 * Clase encargada de la gesti�n de la comunicaci�n entre el dispositivo m�vil
 * y los m�todos disponibles para consumir en el servicio web, a trav�s del
 * m�todo post utilizando el protocolo httprequest.
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
	 * m�todo espec�fico alojado en el servicio web
	 */
	public String metodo;
	
	/**
	 * Instancia para realizar el proceso de encriptaci�n y desencriptaci�n de Strings
	 */
	private AutenticadorKeyczarCrypter crypter;
	
	private final int TIMEOUT;

	/**
	 * Constructor de la clase AutenticadorSyncBL, permite parametrizar el m�todo que 
	 * se quiere implementar al momento de consumir el servicio web.
	 * 
	 * @param actividad
	 * @param meotodo
	 * @throws AutenticadorSyncBLException
	 */
	public AutenticadorSyncBL(Context context, String meotodo)
			throws AutenticadorSyncBLException {
		//	Se asigna el valor de la url que viene de la configuraci�n
		try {
			/*
			 * El valor de la url del servidor, se toma de la
			 * configuraci�n activa del dispositivo m�vil.
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
			//	Exepci�n no controlada de tipo AutenticadorDaoMasterSourceException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ConfiguracionBLException e) {
			//	Exepci�n no controlada de tipo ConfiguracionBLException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		// se asigna el nombre del m�todo alojado en el servicio web
		this.metodo = meotodo;
		try {
			// Se inicializa el objeto Crypter
			this.crypter = AutenticadorKeyczarCrypter.getInstance(context
					.getResources());
		} catch (AutenticadorKeyczarCrypterException e) {
			// Exepci�n no controlada al inicializar el objeto crypter
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
	}

	/**
	 * Funci�n que realiza un TEST utilizando el protocolo HttpRequest, 
	 * con el fin de establecer la coneci�n con el servidor central.
	 * 
	 * @throws AutenticadorSyncBLException
	 */
	public TestWrapper obtenerTestMetodo() throws AutenticadorSyncBLException {
		TestWrapper testWrapper = null;
		// Variable response de la ejecuci�n del m�todo al servicio web.
		String responseString = "-1";
		try {
			// Creaci�n de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			//	Se instancian los par�metros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
		    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		    //	Se asignan dichos parm�tros al client
		    client = new DefaultHttpClient(httpParams);
		    //	Definici�n del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Ejecuci�n de la petici�n al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petici�n
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto TestWrapper
			testWrapper = AutenticadorGsonConverter
					.convertJSONToTestWrapper(responseStringDesencriptado);
		} catch (ClientProtocolException e) {// Exepci�n no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepci�n no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorGsonConverterException e) {// Exepci�n no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepci�n no controlada de AutenticadorKeyczarCrypterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		return testWrapper;
	}

	/**
	 * Funci�n que obtiene un objeto de tipo AutResponseSyncWrapper utilizando el protocolo HttpRequest, 
	 * con el fin de establecer la coneci�n con el servidor central. con este objeto se puede validar
	 * que un elector, a partir de su n�mero de cedula, se ha Autenticado en otra estaci�n.
	 * 
	 * @param cedula
	 * @return autResponseSyncWrapper
	 * @throws AutenticadorSyncBLException
	 */
	public AutResponseSyncWrapper obtenerAutenticadoByCed (String cedula) throws 
		AutenticadorSyncBLException{
		// Variable de retorno
		AutResponseSyncWrapper autResponseSyncWrapper = null;
		// Variable response de la ejecuci�n del m�todo al servicio web.
		String responseString = "-1";
		try {
			//	El servicio web recibe como par�metro un objeto de tipo AutSyncWrapper
			AutSyncWrapper autSyncWrapper = new AutSyncWrapper();
			//	Se asigna el valor de la c�dula del elector al objeto AutSyncWrapper
			autSyncWrapper.setCedula(cedula);
			/* Eventualmente convertimos el objeto AutSyncWrapper en un String en ofrmato Json
			 * despu�s, dicho String debe ser encriptado.
			 */
			String JsonStringEncriptado = crypter
					.encrypt(AutenticadorGsonConverter
							.convertAutSyncWrapperToJson(autSyncWrapper));
			// Creaci�n de variable cliente http.
			HttpClient client = new DefaultHttpClient();
            //	Se instancian los par�metros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			//	Se asignan dichos parm�tros al client
		    client = new DefaultHttpClient(httpParams);
			//	Definici�n del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Asignaci�n del Json encriptado en la petici�n
			post.setEntity(new StringEntity(JsonStringEncriptado, "UTF-8"));
			//	Ejecuci�n de la petici�n al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petici�n
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto AutResponseSyncWrapper
			autResponseSyncWrapper = AutenticadorGsonConverter
					.convertJSONToAutResponseSyncWrapper(responseStringDesencriptado);
		} catch (AutenticadorGsonConverterException e) {// Exepci�n no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (UnsupportedEncodingException e) {// Exepci�n no controlada de UnsupportedEncodingException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ClientProtocolException e) {// Exepci�n no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepci�n no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepci�n no controlada de AutenticadorKeyczarCrypterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		//	Se retorna el objeto AutResponseSyncWrapper
		return autResponseSyncWrapper;
	}
	
	/**
	 * funci�n implementada para convertir la instancia de tipo novSyncWrapper
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
	 * Funci�n que utiliza la funci�n del servidor para insertar una lista de
	 * novedades pendientes a la base de datos del servidor central. la lista de
	 * novedades se envia en una instancia de la clase ListaNovedadSyncWrapper
	 * compuesta de una lista de novedades tipo NovSyncWrapper
	 * 
	 * @param listaNovedadesWrapper instancia con la lista de novedades.
	 * @return listaNovedadesWrapper instancia con la respuesta de cada inserci�n de novedades.
	 * @throws AutenticadorSyncBLException
	 */
	public ListaNovedadSyncWrapper insertaNovedad(
			ListaNovedadSyncWrapper listaNovedadesWrapper) throws 
			AutenticadorSyncBLException{
		// Variable de retorno
		ListaNovedadSyncWrapper listaNovedadesResponseWrapper = null;
		// Variable response de la ejecuci�n del m�todo al servicio web.
		String responseString = "-1";	
		try {

			/* Eventualmente convertimos el objeto ListaNovedadSyncWrapper en un String en ofrmato Json
			 * despu�s, dicho String debe ser encriptado.
			 */
			String JsonStringEncriptado = crypter
					.encrypt(AutenticadorGsonConverter
							.convertListaNovedadSyncWrapperToJson(listaNovedadesWrapper));
			// Creaci�n de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			// Se instancian los par�metros del request
			final HttpParams httpParams = new BasicHttpParams();
			//	se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			//	Se asignan dichos parm�tros al client
			client = new DefaultHttpClient(httpParams);
			//	Definici�n del servicio post
			HttpPost post = new HttpPost(url + metodo);
			//	Asignaci�n del Json encriptado en la petici�n
			post.setEntity(new StringEntity(JsonStringEncriptado, "UTF-8"));
			//	Ejecuci�n de la petici�n al servicio web
			HttpResponse response = client.execute(post);
			//	Captura de la respuesta a la petici�n
			responseString = EntityUtils.toString(response.getEntity());
			// Se desencripta la respuesta del servicio
			String responseStringDesencriptado = crypter.decrypt(responseString);
			//	Se serializa la reespuesta JSon al objeto ListaNovedadSyncWrapper
			listaNovedadesResponseWrapper = AutenticadorGsonConverter
					.convertJSONToListaNovedadSyncWrapper(responseStringDesencriptado);
		} catch (AutenticadorGsonConverterException e) {// Exepci�n no controlada de AutenticadorGsonConverterException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (UnsupportedEncodingException e) {// Exepci�n no controlada de UnsupportedEncodingException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (ClientProtocolException e) {// Exepci�n no controlada de ClientProtocolException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (IOException e) {// Exepci�n no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		} catch (AutenticadorKeyczarCrypterException e) {// Exepci�n no controlada de AutenticadorKeyczarCrypterException
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
		    	// Creaci�n de variable cliente http.
	        	HttpClient client = new DefaultHttpClient();
				// Se instancian los par�metros del request
				final HttpParams httpParams = new BasicHttpParams();
				//	se define un timeout de 4 segundos
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
				//	Se asignan dichos parm�tros al client
				client = new DefaultHttpClient(httpParams);
				//	Definici�n del servicio post
				HttpPost post = new HttpPost(url + metodo);
				//	Asignaci�n del Json encriptado en la petici�n
				post.setEntity(new ByteArrayEntity(byeArrayPDF));
				//	Ejecuci�n de la petici�n al servicio web
				HttpResponse response = client.execute(post);
				//	Captura de la respuesta a la petici�n
				responseString = EntityUtils.toString(response.getEntity());
				return responseString;
	        } catch (IOException e) {// Exepci�n no controlada de IOException
				throw new AutenticadorSyncBLException(e.getLocalizedMessage()
						.toString(), e);
			}
        }catch(Exception e){
        	throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
        }
	}
	
	/**
	 * Funci�n que realiza un TEST al servicio de impresi�n 
	 * 
	 * @return respuesta de la petici�n al servicio.
	 * @throws AutenticadorSyncBLException
	 */
	public String testImpresoraSync() throws AutenticadorSyncBLException{
		//	Valor por defecto de falla
		String responseString = "3";
		try {
			// Creaci�n de variable cliente http.
			HttpClient client = new DefaultHttpClient();
			// Se instancian los par�metros del request
			final HttpParams httpParams = new BasicHttpParams();
			// se define un timeout de 4 segundos
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
			// Se asignan dichos parm�tros al client
			client = new DefaultHttpClient(httpParams);
			// Definici�n del servicio post
			HttpPost post = new HttpPost(url + metodo);
			// Ejecuci�n de la petici�n al servicio web
			HttpResponse response = client.execute(post);
			// Captura de la respuesta a la petici�n
			responseString = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {// Exepci�n no controlada de IOException
			throw new AutenticadorSyncBLException(e.getLocalizedMessage()
					.toString(), e);
		}
		//	se retorna el resultado del test.
		return responseString;
	}

}
