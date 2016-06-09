package carvajal.autenticador.android.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import carvajal.autenticador.android.util.exception.AutenticadorGsonConverterException;
import carvajal.autenticador.android.util.wrappers.AutResponseSyncWrapper;
import carvajal.autenticador.android.util.wrappers.AutSyncWrapper;
import carvajal.autenticador.android.util.wrappers.ListaNovedadSyncWrapper;
import carvajal.autenticador.android.util.wrappers.TestWrapper;

/**
 * Clase encargada de gestionar la conversion de datos en dos tipos de formas, por una parte
 * los abtributo de un objeto wrapper se pueden convertir a Stringe en formato Json y por otra parte
 * de forma viceveza, convertir un archivo String en formato Json a un objeto wrapper.
 * 
 * @author davparpa
 * @version 1.0
 * @since   17 de Abril de 2015
 */
public class AutenticadorGsonConverter {

	/**
	 * En esta funci�n est�tica, se realiza la conversi�n del Json que viene de la respuesta al m�todo
	 * GetTest del servidor, y el cual se convierte al objeto TestWrapper.
	 * @param jSONText  par�metro con el string del Json desencriptado
	 * @return TestWrapper 
	 * @throws excepci�n AutenticadorGsonConverterException
	 */
	public static TestWrapper convertJSONToTestWrapper(final String jSONText)
			throws AutenticadorGsonConverterException {
		//	Declaraci�n del objeto TestWrapper
		TestWrapper object = null;
		try {
			// Creaci�n del objeto Gson para ejecutar conversi�n
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<TestWrapper>() {
			}.getType();
			//	Se asigna al objeto TestWrapper, la colecci�n de datos que ven�a del Json y que se mape� 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepci�n no controlada al realizar la conversi�n
			throw new AutenticadorGsonConverterException(e.getLocalizedMessage().toString(), e);
		}
		//	Se retorna el objeto TestWrapper
		return object;
	}
	
	/**
	 * En esta funci�n est�tica, se realiza la conversi�n del objeto AutSyncWrapper
	 * que se desea enviar al m�todo GetNovedadesByCed del servidor, y el cual se convierte 
	 * a un String con formato Json.
	 * 
	 * @param AutSyncWrapper  
	 * @return JsonString 
	 * @throws excepci�n AutenticadorGsonConverterException
	 */
	public static String convertAutSyncWrapperToJson(
			AutSyncWrapper objectWrapper)
			throws AutenticadorGsonConverterException {
		// variable string en formato Json
		String jsonString = null;
		try {
			// Creaci�n del objeto Gson para ejecutar conversi�n
			final Gson gson = new Gson();
			// se realiza la conversi�n de AutSyncWrapper a JsonString
			jsonString = gson.toJson(objectWrapper);
		} catch (Exception e) {
			throw new AutenticadorGsonConverterException(e
					.getLocalizedMessage().toString(), e);
		}
		// se retorna el jsonString
		return jsonString;
	}
	
	/**
	 * En esta funci�n est�tica, se realiza la conversi�n del Json que viene de la respuesta al m�todo
	 * GetNovedadesByCed del servidor, y el cual se convierte al objeto AutResponseSyncWrapper.
	 * 
	 * @param jSONText
	 * @return AutResponseSyncWrapper
	 * @throws AutenticadorGsonConverterException
	 */
	public static AutResponseSyncWrapper convertJSONToAutResponseSyncWrapper(final String jSONText)
			throws AutenticadorGsonConverterException {
		//	Declaraci�n del objeto AutResponseSyncWrapper
		AutResponseSyncWrapper object = null;
		try {
			// Creaci�n del objeto Gson para ejecutar conversi�n
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<AutResponseSyncWrapper>() {
			}.getType();
			//	Se asigna al objeto AutResponseSyncWrapper, la colecci�n de datos que ven�a del Json y que se mape� 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepci�n no controlada al realizar la conversi�n
			throw new AutenticadorGsonConverterException(e.getLocalizedMessage().toString(), e);
		}
		//	Se retorna el objeto AutResponseSyncWrapper
		return object;
	}

	public static String convertListaNovedadSyncWrapperToJson(
			ListaNovedadSyncWrapper objectWrapper) 
			throws AutenticadorGsonConverterException {
		// variable string en formato Json
		String jsonString = null;
		try {
			// Creaci�n del objeto Gson para ejecutar conversi�n
			final Gson gson = new Gson();
			// se realiza la conversi�n de ListaNovedadSyncWrapper a JsonString
			jsonString = gson.toJson(objectWrapper);
		} catch (Exception e) {
			throw new AutenticadorGsonConverterException(e
					.getLocalizedMessage().toString(), e);
		}
		// se retorna el jsonString
		return jsonString;
	}

	public static ListaNovedadSyncWrapper 
		convertJSONToListaNovedadSyncWrapper(String jSONText) 
			throws AutenticadorGsonConverterException {
//		Declaraci�n del objeto ListaNovedadSyncWrapper
		ListaNovedadSyncWrapper object = null;
		try {
			// Creaci�n del objeto Gson para ejecutar conversi�n
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<ListaNovedadSyncWrapper>() {
			}.getType();
			//	Se asigna al objeto ListaNovedadSyncWrapper, la colecci�n de datos que ven�a del Json y que se mape� 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepci�n no controlada al realizar la conversi�n
			throw new AutenticadorGsonConverterException(e.getLocalizedMessage().toString(), e);
		}
		//	Se retorna el objeto ListaNovedadSyncWrapper
		return object;
	}

}
