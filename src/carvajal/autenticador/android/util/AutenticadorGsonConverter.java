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
	 * En esta función estática, se realiza la conversión del Json que viene de la respuesta al método
	 * GetTest del servidor, y el cual se convierte al objeto TestWrapper.
	 * @param jSONText  parámetro con el string del Json desencriptado
	 * @return TestWrapper 
	 * @throws excepción AutenticadorGsonConverterException
	 */
	public static TestWrapper convertJSONToTestWrapper(final String jSONText)
			throws AutenticadorGsonConverterException {
		//	Declaración del objeto TestWrapper
		TestWrapper object = null;
		try {
			// Creación del objeto Gson para ejecutar conversión
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<TestWrapper>() {
			}.getType();
			//	Se asigna al objeto TestWrapper, la colección de datos que venía del Json y que se mapeó 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepción no controlada al realizar la conversión
			throw new AutenticadorGsonConverterException(e.getLocalizedMessage().toString(), e);
		}
		//	Se retorna el objeto TestWrapper
		return object;
	}
	
	/**
	 * En esta función estática, se realiza la conversión del objeto AutSyncWrapper
	 * que se desea enviar al método GetNovedadesByCed del servidor, y el cual se convierte 
	 * a un String con formato Json.
	 * 
	 * @param AutSyncWrapper  
	 * @return JsonString 
	 * @throws excepción AutenticadorGsonConverterException
	 */
	public static String convertAutSyncWrapperToJson(
			AutSyncWrapper objectWrapper)
			throws AutenticadorGsonConverterException {
		// variable string en formato Json
		String jsonString = null;
		try {
			// Creación del objeto Gson para ejecutar conversión
			final Gson gson = new Gson();
			// se realiza la conversión de AutSyncWrapper a JsonString
			jsonString = gson.toJson(objectWrapper);
		} catch (Exception e) {
			throw new AutenticadorGsonConverterException(e
					.getLocalizedMessage().toString(), e);
		}
		// se retorna el jsonString
		return jsonString;
	}
	
	/**
	 * En esta función estática, se realiza la conversión del Json que viene de la respuesta al método
	 * GetNovedadesByCed del servidor, y el cual se convierte al objeto AutResponseSyncWrapper.
	 * 
	 * @param jSONText
	 * @return AutResponseSyncWrapper
	 * @throws AutenticadorGsonConverterException
	 */
	public static AutResponseSyncWrapper convertJSONToAutResponseSyncWrapper(final String jSONText)
			throws AutenticadorGsonConverterException {
		//	Declaración del objeto AutResponseSyncWrapper
		AutResponseSyncWrapper object = null;
		try {
			// Creación del objeto Gson para ejecutar conversión
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<AutResponseSyncWrapper>() {
			}.getType();
			//	Se asigna al objeto AutResponseSyncWrapper, la colección de datos que venía del Json y que se mapeó 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepción no controlada al realizar la conversión
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
			// Creación del objeto Gson para ejecutar conversión
			final Gson gson = new Gson();
			// se realiza la conversión de ListaNovedadSyncWrapper a JsonString
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
//		Declaración del objeto ListaNovedadSyncWrapper
		ListaNovedadSyncWrapper object = null;
		try {
			// Creación del objeto Gson para ejecutar conversión
			final Gson gson = new Gson();
			//	Se crea objeto con el mapeo del objeto wrapper en formato Json equivalente
			final Type collectionType = new TypeToken<ListaNovedadSyncWrapper>() {
			}.getType();
			//	Se asigna al objeto ListaNovedadSyncWrapper, la colección de datos que venía del Json y que se mapeó 
			//	con el collectionType anterior.
			object = gson.fromJson(jSONText, collectionType);
		}catch (Exception e) {// Excepción no controlada al realizar la conversión
			throw new AutenticadorGsonConverterException(e.getLocalizedMessage().toString(), e);
		}
		//	Se retorna el objeto ListaNovedadSyncWrapper
		return object;
	}

}
