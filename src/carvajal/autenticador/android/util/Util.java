package carvajal.autenticador.android.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.CensoBL;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.ParametrosBL;
import carvajal.autenticador.android.dal.greendao.read.Censo;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.read.Parametros;
import carvajal.autenticador.android.util.AsyncTaskUtilities.VerificarConexionIpLocal;
import carvajal.autenticador.android.util.exception.UtilException;
import carvajal.framework.utilidades.cifrado.Cryptography;
import carvajal.framework.utilidades.excepcion.FrameworkException;

/**
 * Clase con métodos utilitarios.
 * 
 * @author jorortru
 * 
 */
public class Util {

	/**
	 * Tipo de dialogos
	 */
	public final static int DIALOG_INFORMATIVO = android.R.drawable.ic_menu_info_details;
	public final static int DIALOG_ERROR = android.R.drawable.ic_delete;
	public final static int DIALOG_ADVERTENCIA = android.R.drawable.ic_dialog_alert;
	public final static int DIALOG_CONFIRMACION = android.R.drawable.ic_menu_help;

	/**
	 * Estados de conexion
	 */
	public final static short CONEXION_VALIDA = 200; // HTTP 200: OK
	public final static short CONEXION_INVALIDA = 503; // HTTP 503: Service
														// Unavailable

	/**
	 * Resultados de la autenticacion
	 */
	public final static short AUTENTICACION_EXITOSA = 1;
	public final static short AUTENTICACION_CREDENCIALES_INVALIDAS = 14;
	public final static short AUTENTICACION_NO_PERTENECE_CURSO_ACTIVO = 115;
	public final static short AUTENTICACION_USUARIO_INACTIVO = 158;
	public final static short AUTENTICACION_CLASE_NO_INICIADA = 163;

	/**
	 * Resultados de descarga de la evaluación
	 */
	public final static short EVALUACION_ZIP_DESCARGADO = 1;
	public final static short EVALUACION_ERROR_DESCARGANDO = 2;
	public final static short EVALUACION_FTP_SIN_CONEXION = 3;
	public final static short EVALUACION_NO_ALMACENAMIENTO = 4;
	public final static short EVALUACION_ERROR_SERVIDOR = 5;
	public final static short EVALUACION_ZIP_DESCOMPRIMIDO = 6;
	public final static short EVALUACION_ZIP_ERROR_PROCESANDO = 7;
	public final static short EVALUACION_ZIP_ERROR_HASH = 8;
	public final static short EVALUACION_ERROR_ESPACIO_DISPONIBLE = 9;

	/**
	 * Resultados del envío de las respuestas de la evaluacion
	 */
	public static final short RESPUESTAS_ZIP_SUBIDO = 1;
	public static final short RESPUESTAS_ERROR_ENVIANDO = 2;
	public static final short RESPUESTAS_FTP_SIN_CONEXION = 3;
	public static final short RESPUESTAS_CONFIRMACION_OBTENIDA = 4;

	/**
	 * Unidades de medida de bytes
	 */
	public static final long BYTES = 1;
	public static final long KILOBYTES = 1024;
	public static final long MEGABYTES = 1048576;
	public static final long GIGABYTES = 1073741824;

	/**
	 * Resultados de contenidos
	 */
	public static final short CONTENIDOS_HASH_ERROR = 1;
	public static final short CONTENIDOS_HASH_CORRECTO = 2;
	public static final short CONTENIDOS_ERROR_INSERTANDO = 3;
	public static final short CONTENIDOS_VERIFICACION_CORRECTA = 4;
	public static final short CONTENIDOS_VERIFICACION_ERROR = 5;
	public final static short CONTENIDOS_FTP_SIN_CONEXION = 6;
	public final static short CONTENIDOS_ERROR_DESCARGANDO = 7;
	public static final short CONTENIDOS_DESCARGADO = 8;
	public static final short CONTENIDOS_NO_ALMACENAMIENTO = 9;
	public static final short CONTENIDOS_ERROR_SERVIDOR = 10;
	public static final short CONTENIDOS_INICIA_DESCARGA = 11;

	public static final short CONTENIDOS_VISOR_EXTERNO = 12; // (video, office,
																// PDF)
	public static final short CONTENIDOS_MP3 = 13;
	public static final short CONTENIDOS_IMAGEN = 14;
	public static final short CONTENIDOS_HTML = 15;
	public static final short CONTENIDOS_URL_EXTERNA = 16;
	public static final short CONTENIDOS_NO_SOPORTADO = 17;
	public static final short CONTENIDOS_VISOR_ERROR = 18;

	public static final String NO_CEDULA = "NO_CEDULA";

	private static Pattern pattern;
	private static Matcher matcher;

	// Etiqueta por defecto que irá en el botón del dialog
	public static String mensajeAceptar = "Aceptar";

	// Etiqueta por defecto que irá en el botón del dialog
	public static String mensajeCancelar = "Cancelar";

	public final static String ENCABEZADO_URL = "http://";

	public final static String SEPARADOR_IP = ":";

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(Util.class);

	/**
	 * Listado de servidores leidos desde el archivo plano
	 */
	public static ArrayList<String> listLinesServers = null;
	
	/**
	 * Parametros
	 */
	public static final String NOMBRE_ELECCION = "nombre_eleccion";
	public static final String VALIDA_FECHA = "valida_fecha";
	public static final String VALIDA_HORA_DESDE = "valida_hora_desde";
	public static final String VALIDA_HORA_HASTA = "valida_hora_hasta";
	
	/**
	 * Reinicia los valores de las etiquetas para los dialogs
	 */
	public static void resetEtiquetas() {
		mensajeAceptar = "Aceptar";
		mensajeCancelar = "Cancelar";
	}

	/**
	 * Expresión regular para dirección IP
	 */
	public static final String EXPRESION_REGULAR_IP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	/**
	 * Valida las expresiones regulares
	 * 
	 * @param expresionRegular
	 *            Expresión regular
	 * @param valor
	 *            Valor a evaluar con la expresión regular
	 * @return <code>true</code> si <code>valor</code> es cumple con la
	 *         expresión regular <code>expresionRegular</code>
	 */
	public static boolean validarExpresionRegular(String expresionRegular,
			String valor) {
		pattern = Pattern.compile(expresionRegular);
		matcher = pattern.matcher(valor);
		return matcher.matches();
	}

	/**
	 * Valida si un campo está vacio
	 * 
	 * @param campo
	 *            EditText que se va a evaluar.
	 * @param mensajeError
	 *            Mensaje que se va a mostrar en caso de que el campo esté
	 *            vacio. Si es <code>null</code>, no se muestra mensaje, sólo se
	 *            valida obligatoriedad.
	 * @return <code>true</code> si el campo está vacio. <code>false</code> en
	 *         caso contrario.
	 */
	public static boolean validarCampoVacio(final EditText campo,
			String mensajeError) {
		if (!(campo.getText().toString() != null && !campo.getText().toString()
				.trim().isEmpty())) {
			if (mensajeError != null && !mensajeError.trim().isEmpty()) {
				campo.setError(mensajeError);
				campo.requestFocus();
			} else {
				campo.setError(null);
			}
			// Esta vacio
			return true;
		}

		// No esta vacio
		return false;
	}

	/**
	 * Construye un dialog con los parámetros ingresados.
	 * 
	 * @param context
	 * @param titulo
	 *            Titulo del dialog. Si es <code>null</code>, no ajusta título.
	 * @param mensaje
	 *            Mensaje a mostrar
	 * @param tipoMensaje
	 *            Tipo de dialog.
	 * @param eventoAceptar
	 *            Evento que ejecutaría cuando se de clic en aceptar. Se puede
	 *            enviar <code>null</code> para no ejecutar evento en
	 *            particular.
	 * @return <code>Dialog</code>
	 */
	public static AlertDialog mensajeAceptar(Context context, int tema,
			String titulo, String mensaje, int tipoMensaje,
			OnClickListener eventoAceptar) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context, tema);

		if (titulo != null && !titulo.trim().isEmpty()) {
			builder.setTitle(titulo);
		}

		builder.setIconAttribute(android.R.attr.alertDialogIcon)
				.setMessage(mensaje)
				.setNeutralButton(mensajeAceptar, eventoAceptar)
				.setIcon(tipoMensaje);

		dialog = builder.create();

		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}

	/**
	 * Construye un dialog de confirmación con los parámetros ingresados.
	 * 
	 * @param context
	 *            Contexto android
	 * @param titulo
	 *            Titulo del dialog. Si es <code>null</code>, no ajusta título.
	 * @param mensaje
	 *            Mensaje a mostrar
	 * @param tipoMensaje
	 *            Tipo de Dialog
	 * @param eventoAceptar
	 *            Evento que ejecutaría cuando se de clic en aceptar. Se puede
	 *            enviar <code>null</code> para no ejecutar evento en
	 *            particular.
	 * @param eventoCancelar
	 *            Evento que ejecutaría cuando se de clic en cancelar. Se puede
	 *            enviar <code>null</code> para no ejecutar evento en
	 *            particular.
	 * @return <code>Dialog</code>
	 */
	public static AlertDialog mensajeConfirmacion(Context context, int tema,
			String titulo, String mensaje, int tipoMensaje,
			OnClickListener eventoAceptar, OnClickListener eventoCancelar) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context, tema);

		if (titulo != null && !titulo.trim().isEmpty()) {
			builder.setTitle(titulo);
		}

		builder.setIconAttribute(android.R.attr.alertDialogIcon).setMessage(
				mensaje);

		builder.setPositiveButton(mensajeAceptar, eventoAceptar);
		builder.setNegativeButton(mensajeCancelar, eventoCancelar);
		builder.setIcon(tipoMensaje).setCancelable(false);

		dialog = builder.create();

		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	/**
	 * Obtiene la dirección MAC del adaptador Wi-Fi del dispositivo.
	 * 
	 * @param context
	 *            Contexto Android
	 * @return Dirección MAC
	 */
	public static String obtenerMAC(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * Obtiene el Android ID de las preferencias del sistema.
	 * 
	 * @param context
	 * @return Android ID
	 */
	public static String obtenerAndroidId(Context context) {
		String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return androidId;
	}

	/**
	 * Obtiene la ruta del almacenamiento externo. Primer intenta obtener la
	 * ruta de la SD externa, y luego del almacenamiento externo del
	 * dispositivo.
	 * 
	 * @param resources
	 * @param sdCardExterna
	 *            indica si se debe buscar o no una SD card externa
	 * @return <code>String</code> con la ruta del almacenamiento externo
	 */
	public static String obtenerAlmacenamientoExterno(Resources resources,
			boolean sdCardExterna) {
		String rutaAlmacenamiento = null;
		File archivoAlmacenamiento;

		if (sdCardExterna) {
			// Obtiene las rutas posibles (parametrizadas) de las ubicaciones de
			// las SDcard externa.
			// TODO:poner ubicacion memoria externa
			String ubicacionesSDCardExterna = "";

			if (ubicacionesSDCardExterna != null) {
				String[] sdcard = ubicacionesSDCardExterna.split(";");
				for (int i = 0; i < sdcard.length; i++) {
					archivoAlmacenamiento = new File(sdcard[i]);
					// Verifica si existe, si es un directorio y si tiene
					// permisos de lectura/escritura
					if (archivoAlmacenamiento.exists()
							&& archivoAlmacenamiento.isDirectory()
							&& archivoAlmacenamiento.canRead()
							&& archivoAlmacenamiento.canWrite()) {
						rutaAlmacenamiento = archivoAlmacenamiento
								.getAbsolutePath();
						break; // Con la primera ubicacion valida, rompe el
								// ciclo.
					} else {
						rutaAlmacenamiento = null;
					}
				}
			}
		}

		if (rutaAlmacenamiento == null) {
			// No fue encontrada una ruta de SDCard externa
			rutaAlmacenamiento = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}

		return rutaAlmacenamiento;
	}

	/**
	 * Obtiene la ruta de almacenamiento secundario
	 * 
	 * @return Ruta de almacenamiento secundario si existe, de lo contrario la
	 *         ruta de almacenamiento interno.
	 */
	public static String obtenerAlmacenamientoSecundario() {
		String interno = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (System.getenv("SECONDARY_STORAGE") != null) {

			File archivoAlmacenamiento = new File(
					System.getenv("SECONDARY_STORAGE"));

			if (archivoAlmacenamiento.exists()
					&& archivoAlmacenamiento.isDirectory()
					&& archivoAlmacenamiento.canRead()
					&& archivoAlmacenamiento.canWrite()) {
				return archivoAlmacenamiento.getAbsolutePath().concat(
						File.separator);
			} else {
				return interno.concat(File.separator);
			}

		} else {
			return interno.concat(File.separator);
		}
	}

	/**
	 * Valida el hash <code>md5</code> contra el hash md5 generado del archivo
	 * representado por <code>archivo</code>
	 * 
	 * @param md5
	 *            Hash de referencia
	 * @param archivo
	 *            Archivo a comparar
	 * @return <code>true</code> si corresponde al hash. <code>false</code> en
	 *         caso contrario
	 * @throws FrameworkException
	 */
	public static boolean validarHash(String md5, File archivo)
			throws FrameworkException {
		String md5Calculado = Cryptography.calcularHash(archivo);

		if (md5.equalsIgnoreCase(md5Calculado)) {
			return true;
		}

		return false;
	}

	/**
	 * Escribe el contenido de <code>contenido</code> en disco en el archivo
	 * <code>archivoDestino</code>
	 * 
	 * @param archivoDestino
	 *            Archivo donde se guarda la cadena
	 * @param contenido
	 *            Contenido que se escribirá en disco
	 * @return <code>true</code> si el archivo es escrito a disco.
	 *         <code>false</code> si ocurre un error.
	 */
	public static boolean escribirArchivoTexto(File archivoDestino,
			String contenido) {
		boolean resultado = false;

		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(archivoDestino);

			// Si el archivo no existe, lo crea
			if (!archivoDestino.exists()) {
				archivoDestino.createNewFile();
			}

			// Obtiene el contenido en bytes
			byte[] contentInBytes = contenido.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			resultado = true;
		} catch (IOException e) {
			resultado = false;
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				resultado = false;
			}
		}
		return resultado;
	}

	/**
	 * Obtiene el espacio de almacenamiento total en una ubicación dada
	 * 
	 * @param path
	 *            Ubicación de almacenamiento
	 * @param medida
	 *            Medida: <code>Util.BYTES</code>, <code>Util.KILOBYTES</code>,
	 *            <code>Util.MEGABYTES</code> o <code>Util.GIGABYTES</code>
	 * @return Tamano total de la unidad
	 */
	public static double obtenerEspacioTotal(String path, long medida) {
		StatFs stat = new StatFs(path);
		long bytesDisponibles = (long) stat.getBlockSize()
				* (long) stat.getBlockCount();
		if (medida == Util.BYTES) {
			return bytesDisponibles;
		}
		double disponibles = (double) bytesDisponibles / (double) medida;
		return disponibles;
	}

	/**
	 * Obtiene el espacio de almacenamiento disponible (libre) en una ubicación
	 * dada
	 * 
	 * @param path
	 *            Ubicación de almacenamiento
	 * @param medida
	 *            Medida: <code>Util.BYTES</code>, <code>Util.KILOBYTES</code>,
	 *            <code>Util.MEGABYTES</code> o <code>Util.GIGABYTES</code>
	 * @return Tamano disponible
	 */
	public static double obtenerEspacioDisponible(String path, long medida) {
		StatFs stat = new StatFs(path);
		long bytesDisponibles = (long) stat.getBlockSize()
				* (long) stat.getAvailableBlocks();
		if (medida == Util.BYTES) {
			return bytesDisponibles;
		}
		double disponibles = (double) bytesDisponibles / (double) medida;
		return disponibles;
	}

	public static String obtenerExtensionArchivo(File archivo) {
		String extension;

		int posPunto = archivo.getName().lastIndexOf(".");
		if (posPunto != -1) {
			extension = archivo.getName().substring(posPunto + 1);
		} else {
			extension = "";
		}

		return extension;
	}

	/**
	 * Este metodo es el encargado de conusltar la fecha del sistema y colocarlo
	 * en el label definido
	 */
	public static StringBuilder obtenerFecha() {
		final Calendar c = Calendar.getInstance();
		int yy = c.get(Calendar.YEAR);
		String mm = c.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale(
				"es"));
		int dd = c.get(Calendar.DAY_OF_MONTH);

		return new StringBuilder().append(dd).append(" ").append(" ")
				.append(mm).append(" ").append(yy);
	}

	/**
	 * Este metodo es el encargado de conusltar la fecha del sistema y colocarlo
	 * en el label definido para imprimirlo en el comprobante
	 */
	public static String obtenerFechaDeImpresion(Date fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		return sdf.format(fecha);
	}

	/**
	 * 
	 * Meotodo encargado de recibir la fecha, en el formato que entrega la
	 * registraduria, para retornarlo de la misma forma como aparece en la
	 * cedula.
	 * 
	 * @param fechaO
	 *            Fecha de origen entregada en el censo.
	 * @return Retorna el String armado de la forma '05/06/1995'
	 * @throws ParseException
	 *             Excepción al no poder realizar el cast de la fecha entregada.
	 * @author johgrame
	 * @since 19-02-2015
	 */
	public static StringBuilder convertirFecha(String fechaO)
			throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy",
				new Locale("en"));
		Date date = formatter.parse(fechaO);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// se realiza esta validación, para obteneer el mes con el 0 adelante
		// para los meses menores a Septiembre.
		String mes = (calendar.get(Calendar.MONTH) + 1) <= 9 ? "0"
				.concat(String.valueOf(calendar.get(Calendar.MONTH) + 1))
				: String.valueOf(calendar.get(Calendar.MONTH) + 1);

		return new StringBuilder().append(calendar.get(Calendar.DAY_OF_MONTH))
				.append("/").append(mes).append("/")
				.append(calendar.get(Calendar.YEAR));
	}

	/**
	 * Metodo encargado, de tomar la trama obtenida de la lectura 2D Para luego
	 * buscar el numero cedula en la posición indicada.
	 * 
	 * @param trama
	 *            Cadena obtenida de la captura del code 2D
	 * @return retorna el valor de la cedula, en caso contrario retorna
	 *         "NO CEDULA"
	 * @author johgrame
	 */
	public static String ObtenerCedulaDesdeTrama(String trama) {
		String valorFound = "";
		int cedula = 0;

		// se valida que la trama tenga una longitud mayor a 58, en caso de no
		// leer una cedula
		if (trama.length() > 58) {
			try {
				cedula = Integer.parseInt(trama.substring(48, 58).trim());
				valorFound = String.valueOf(cedula);
			} catch (NumberFormatException ex) {
				// en caso de que el valor obtenido no sea un numéro, se retorna
				// el string "NO CEDULA"
				valorFound = NO_CEDULA;
			}
		} else
			valorFound = NO_CEDULA; // Si el valor leido, no contiene la
									// longitud esperada se retorna el
									// string "NO CEDULA"

		return valorFound;
	}

	/**
	 * Valida la fecha permitida de la aplicación contra la fecha actual del
	 * sistema.
	 * 
	 * @param fechaValida
	 *            fecha configurada para el uso de la aplicacion
	 * @param horaDesde
	 *            Hora configurada para validar DESDE que hora se puede entrar a
	 *            la app.
	 * @param horaHasta
	 *            Hora configurada para validar HASTA que hora se puede entrar a
	 *            la app.
	 * @return True en caso de que se pueda entrar a la app. False si no se
	 *         encuentra en la fecha y hora permitida.
	 */
	public static boolean validarFechaPermitida(String fechaValida, String horaDesde, String horaHasta) throws UtilException {
		try {
			Calendar calendarHoy = Calendar.getInstance();
			int horaDesdeInt = Integer.parseInt(horaDesde);
			int horaHastaInt = Integer.parseInt(horaHasta);
			int dia = Integer.parseInt(fechaValida.substring(0, 2));
			int mes = Integer.parseInt(fechaValida.substring(3, 5));
			int year = Integer.parseInt(fechaValida.substring(6, 10));
			if (calendarHoy.get(Calendar.DAY_OF_MONTH) == dia && calendarHoy.get(Calendar.MONTH) + 1 == mes && calendarHoy.get(Calendar.YEAR) == year) {
				if (calendarHoy.get(Calendar.HOUR_OF_DAY) >= horaDesdeInt && calendarHoy.get(Calendar.HOUR_OF_DAY) < horaHastaInt) {
					return true;
				}
			}
		}
		catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Construye un dialog de confirmación con los parámetros ingresados.
	 * 
	 * @param context
	 *            Contexto android
	 * @param titulo
	 *            Titulo del dialog. Si es <code>null</code>, no ajusta título.
	 * @param mensaje
	 *            Mensaje a mostrar
	 * @param tipoMensaje
	 *            Tipo de Dialog
	 * @param eventoAceptar
	 *            Evento que ejecutaría cuando se de clic en aceptar. Se puede
	 *            enviar <code>null</code> para no ejecutar evento en
	 *            particular.
	 * @param eventoCancelar
	 *            Evento que ejecutaría cuando se de clic en cancelar. Se puede
	 *            enviar <code>null</code> para no ejecutar evento en
	 *            particular.
	 * @return <code>Dialog</code>
	 */
	public static AlertDialog mensajeConfirmacionPersonalizado(Context context,
			int tema, String titulo, String mensaje, String textoBotonPositivo,
			String textoBotonNegativo, int tipoMensaje,
			OnClickListener eventoAceptar, OnClickListener eventoCancelar) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,tema);

		if (titulo != null && !titulo.trim().isEmpty()) {
			builder.setTitle(titulo);
		}

		builder.setIconAttribute(android.R.attr.alertDialogIcon).setMessage(
				mensaje);

		if (textoBotonPositivo == null || textoBotonPositivo.trim().isEmpty()) {
			builder.setPositiveButton(mensajeAceptar, eventoAceptar);
		} else {
			builder.setPositiveButton(textoBotonPositivo, eventoAceptar);
		}
		if (textoBotonNegativo == null || textoBotonNegativo.trim().isEmpty()) {
			builder.setNegativeButton(mensajeCancelar, eventoCancelar);
		} else {
			builder.setNegativeButton(textoBotonNegativo, eventoCancelar);
		}

		builder.setIcon(tipoMensaje).setCancelable(false);

		dialog = builder.create();

		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	public static boolean isCodDivipolIgual(String divipolPuesto,
			String codDepto, String codMpio, String codZona, String codPuesto) {

		boolean isCodIgual = false;
		String divipolPuestoElector = codDepto.concat(codMpio).concat(codZona)
				.concat(codPuesto);
		if (divipolPuesto.equals(divipolPuestoElector)) {
			isCodIgual = true;
		}
		return isCodIgual;
	}

	public String obtenerCodPuesto(String divipolElector) {

		String codPuesto = divipolElector.substring(0, 9);

		return codPuesto;
	}

	public String obtenerCodMesa(String divipolElector) {

		String codMesa = divipolElector.substring(9, 11);

		return codMesa;
	}

	/**
	 * Este metodo se encarga de validar, la mesa de la novedad para extraer los
	 * ceros que tenga a la izquierda
	 * 
	 * @param mesaNov
	 *            Numero de la mesa original
	 * @return El numero de la mesa sin ceros a la izquierda.
	 * @author johgrame
	 * @since 26-02-2015
	 */
	public static String obtenerMesa(String mesaNov) {
		int num = Integer.parseInt(mesaNov);
		return Integer.toString(num);
	}

	/**
	 * Este metodo se encarga de recibir una fecha Date y devolver la fecha y
	 * hora de la forma '25-02-2015 4:16 PM'
	 * 
	 * @param fecha
	 *            fecha original
	 * @return Retorna la cadena formada.
	 */
	public static String getFechayHora(Date fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd K:mm:s a");
		return sdf.format(fecha);
	}

	public static int obtenerIdHuella(List<Integer> ids, int posicion) {

		return ids.get(posicion);
	}

	/**
	 * Obtiene los valores configurados en la tablet para <b>Codigo de
	 * Departamento</b>, <b>Codigo de Municipio</b>, <b>Codigo de Zona</b> y
	 * <b>Codigo de Puesto</b>
	 * 
	 * @param codDpto
	 * @param codMpio
	 * @param codZona
	 * @param codPuesto
	 * @param context
	 * @return <b>Map</b> con los valores configurados
	 */
	public static String obtenerDivipolString(Context context) {
		StringBuilder stringdivipol = new StringBuilder();
		try {
			ConfiguracionBL configuracionBL = new ConfiguracionBL(context);
			Configuracion configuracionActiva = configuracionBL
					.obtenerConfiguracionActiva();

			if (configuracionActiva != null) {
				stringdivipol.append(configuracionActiva.getCodProv());
				stringdivipol.append(configuracionActiva.getCodMpio());
				stringdivipol.append(configuracionActiva.getCodZona());
				stringdivipol.append(configuracionActiva.getCodColElec());
			}
		} catch (Exception e) {

		}
		return stringdivipol.toString();
	}

	/**
	 * Obtiene los valores configurados en la tablet para <b>Codigo de
	 * Departamento</b>, <b>Codigo de Municipio</b>, <b>Codigo de Zona</b> y
	 * <b>Codigo de Puesto</b>
	 * 
	 * @param codDpto
	 * @param codMpio
	 * @param codZona
	 * @param codPuesto
	 * @param context
	 * @return <b>Map</b> con los valores configurados
	 */
	public Map<String, String> obtenerDivipolPuesto(Context context) {

		Map<String, String> divipol = new HashMap<String, String>();
		try {
			ConfiguracionBL configuracionBL = new ConfiguracionBL(context);
			Configuracion configuracionActiva = configuracionBL
					.obtenerConfiguracionActiva();

			if (configuracionActiva != null) {
				divipol.put("codDpto", configuracionActiva.getCodProv());

				divipol.put("codMpio", configuracionActiva.getCodMpio());

				divipol.put("codZona", configuracionActiva.getCodZona());

				divipol.put("codPuesto", configuracionActiva.getCodColElec());
				
				divipol.put("codMesa", configuracionActiva.getCodMesa());
				
			}

		} catch (Exception e) {

		}
		return divipol;
	}

	/**
	 * Verifica si existe la base de datos en el dispositivo y si existen datos
	 * de Censo
	 * 
	 * @param actividad
	 * @return <b>true</b> si existe y contiene datos, <b>false</b> en caso
	 *         contrario
	 * @throws UtilException
	 */
	public static boolean verificarBD(Context context) throws UtilException {
		boolean flag = false;
		try {
			String rutaBD = obtenerAlmacenamientoSecundario();
			rutaBD = rutaBD.concat(
					context.getString(R.string.carpeta_base_de_datos)).concat(
					context.getString(R.string.nombre_base_de_datos));

			File bd = new File(rutaBD);
			if (bd.exists()) {
				CensoBL censoBL = new CensoBL(context);
				long tamañoCenso = censoBL.getSize();
				if (tamañoCenso > 0) {
					flag = true;
				}
			}
		} catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
		return flag;
	}

	/**
	 * Elimina un directorio, incluido su contenido
	 * 
	 * @param directorio
	 * @return <b>true</b> si se pudo eliminar el directorio, <b>false</b> en
	 *         caso contrario.
	 */
	public static boolean eliminarDirectorio(File directorio) {
		if (directorio.isDirectory()) {
			String[] children = directorio.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = eliminarDirectorio(new File(directorio,
						children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return directorio.delete();
	}

	/**
	 * Elimina un archivo teniendo en cuenta el nombre ingresado y el directorio
	 * contendor.
	 * 
	 * @param nombreArchivoSinFecha
	 * @param directorio
	 */
	public static void eliminarArchivo(String nombreArchivoSinFecha,
			File directorio) {

		if (directorio.isDirectory()) {
			String[] children = directorio.list();
			for (int i = 0; i < children.length; i++) {
				if (children[i].startsWith(nombreArchivoSinFecha)) {
					File file = new File(directorio, children[i]);
					file.delete();
				}
			}
		}

	}

	/**
	 * Verifica la conexión a una IP local haciendo ping.
	 * 
	 * @param direccionIP
	 *            Dirección IP .
	 * @param timeOut
	 *            Tiempo de espera de respuesta.
	 * @return Estado de la conexión a Internet.
	 * @author luimunpi
	 * @throws UtilitiesException
	 * @since 11-Oct-2012
	 */
	public static boolean verificarConexionIpLocal(final String direccionIP,
			final String timeOut) {
		boolean conexion = Boolean.FALSE;
		try {
			/* [luimunpi]: Verifica si hay conexión a Intranet. */
			final AsyncTaskUtilities asyncTaskUtil = new AsyncTaskUtilities();
			final VerificarConexionIpLocal verConIpLocal = asyncTaskUtil.new VerificarConexionIpLocal();
			final String[] datos = { direccionIP, timeOut };
			final AsyncTask<String, Void, Boolean> pruebaConexion = verConIpLocal
					.execute(datos);
			conexion = pruebaConexion.get();
		} catch (InterruptedException e) {
		} catch (Exception e) {
		}
		return conexion;
	}

	/**
	 * Obtiene el nombre del servidor que se está usando en la configuración
	 * actual
	 * 
	 * @param ip
	 * @return nombre del servidor configurado
	 */
	public static String obtenerNombreServidor(Context context, String ip) {
		try {
			String urlServidor = context.getString(R.string.direccion_servidor_n_mask);
			String nameServer = "";
			if (listLinesServers != null && !listLinesServers.isEmpty()) {
				if (ip.equalsIgnoreCase(urlServidor.replace("dir_ip", context.getString(R.string.direccion_servidor_0)))) {
					nameServer = context.getString(R.string.nombre_servidor_0);
				}
				else if (ip.equalsIgnoreCase(urlServidor.replace("dir_ip", listLinesServers.get(0)))) {
					nameServer = context.getString(R.string.nombre_servidor_1);
				}
				else if (ip.equalsIgnoreCase(urlServidor.replace("dir_ip", listLinesServers.get(1)))) {
					nameServer = context.getString(R.string.nombre_servidor_2);
				}
				else if (ip.equalsIgnoreCase(urlServidor.replace("dir_ip", listLinesServers.get(2)))) {
					nameServer = context.getString(R.string.nombre_servidor_3);
				}
			}
			return nameServer;
		}
		catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConfiguracionFormActivity:obtenerNombreServidor:", e);
			return "";
		}
	}

	/**
	 * Añade ceros a la izquierda de la cadena
	 * 
	 * @param tamaño
	 *            tamaño de la cadena resultante
	 * @param cadena
	 *            cadena a completar con ceros a la izquierda
	 * @return cadena original con ceros a la izquierda para ajustarse al tamaño
	 *         solicitado
	 */
	public static String ponerCerosIzquierda(int tamaño, String cadena) {
		return String.format("%0" + tamaño + "d", Integer.parseInt(cadena));
	}

	/**
	 * Valida si existen configuracion, y si existe una configuracion marcada
	 * como activa
	 * 
	 * @param configuracionBL
	 * @return <b>true</b> si existe configuracion activa, <b>false</b> en caso
	 *         contrario
	 */
	public static boolean existeConfiguracion(ConfiguracionBL configuracionBL) {
		try {
			boolean res = true;
			if (configuracionBL != null) {
				if (configuracionBL.getConfiguracionCount() > 0) {
					Configuracion configuracionActiva = configuracionBL
							.obtenerConfiguracionActiva();
					// si no hay una configuración activa
					if (configuracionActiva == null) {
						res = false;
					}
				} else {
					res = false;
				}
			} else {
				res = false;
			}
			return res;
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:existeConfiguracion:",
							e);
			return false;
		}

	}

	/**
	 * Permite obtener la ip desde el string de una url.
	 * 
	 * @param url
	 *            ejemplo "http://172.94.47.5:9000/Novedades"
	 * @return string con la ip.
	 */
	public static String obtenerIpDeUrl(String url) throws UtilException {
		String ipCompleta = null;
		try {
			String resultado = url.replaceAll(ENCABEZADO_URL, "");
			ipCompleta = resultado
					.substring(0, resultado.indexOf(SEPARADOR_IP));

		} catch (Exception e) {
			throw new UtilException(e);
		}
		return ipCompleta;

	}

	public static void cambiarLineaDialogos(Dialog dialogo, Context contexto) {

		int divierId = dialogo.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = dialogo.findViewById(divierId);
		divider.setBackgroundColor(contexto.getResources().getColor(
				android.R.color.white));

	}

	public static void logsDiagnostico(String dispositivo, String mensaje) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");

		String fecha = sdf.format(new Date());

		String text = fecha.concat("-").concat(dispositivo).concat("-")
				.concat(mensaje);

		String path = Util.obtenerAlmacenamientoSecundario().concat(
				"logs_diagnostico.txt");

		File logFile = new File(path);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.write("\n" + text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean validarDireccionIPPrivada(String ip) {
		String pattern0To255 = null;
		String pattern16To31 = null;
		try {
			if (ip != null && !ip.isEmpty()) {
				/**
				 * patrón para los números de .0 a .255  
				 */
				pattern0To255 = "\\.(([1-9]?[0-9])|(1[0-9]{2})|(2([0-4][0-9]|5[0-5])))";  
				/**
				 * patrón para los números de .16 a .31
				 */
				pattern16To31 = "\\.((1[6-9])|(2[0-9])|(3[01]))";  
				/**
				 * Range 1
				 * 192.168.0.0 a 192.168.255.255
				 */  
				if (ip.matches("^192\\.168(" + pattern0To255 + "){2}$")) {
					Log.d("Util:", "validarDireccionIPPrivada:ip: " + ip + " valid match 1");
					return true;
				}  
				/**
				 * Range 2
				 * 10.0.0.0 a 10.255.255.255
				 */  
				else if (ip.matches("^10(" + pattern0To255 + "){3}$")) {
					Log.d("Util:", "validarDireccionIPPrivada:ip: " + ip + " valid match 2");
					return true;
				}
				/**
				 * Range 2
				 * 172.16.0.0 a 172.31.255.255
				 */  
				else if (ip.matches("^172" + pattern16To31 + "(" + pattern0To255 + "){2}$")) {
					Log.d("Util:", "validarDireccionIPPrivada:ip: " + ip + " valid match 3");
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * la dirección no coincidió con ninguna de las anteriores
		 */
		Log.d("Util:", "validarDireccionIPPrivada:ip: " + ip + " no valid");
		return false;
	}
	
	/**
	 * Obtiene el array de bytes correspondiente a la foto del elector, o el
	 * array de bytes correpsondiente a la imagen por defecto en caso de no
	 * encontrar foto en el censo.
	 * 
	 * @param censo
	 * @param context
	 * @return Array de bytes correpsondiente a la imagen a mostrar
	 */
	public static byte[] obtenerFotoElector(Censo censo, Context context) {

		byte[] foto = censo.getFoto();

		if (foto == null || foto.length < 1) {
			Drawable d = context.getResources().getDrawable(R.drawable.no_foto);
			Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			foto = stream.toByteArray();
		}

		return foto;

	}
	
	public static String obtenerNombreEleccion(Context context) throws UtilException {
		try {
			String nombreEleccion = null;
			ParametrosBL parametrosBL = new ParametrosBL(context);
			nombreEleccion = ((Parametros) parametrosBL.obtenerParametroPorNombre(Util.NOMBRE_ELECCION)).getValParam();
			return nombreEleccion;
		}
		catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
	}
	
	public static String obtenerValidaFecha(Context context) throws UtilException {
		try {
			String validaFecha = null;
			ParametrosBL parametrosBL = new ParametrosBL(context);
			validaFecha = ((Parametros) parametrosBL.obtenerParametroPorNombre(Util.VALIDA_FECHA)).getValParam();
			return validaFecha;
		}
		catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
	}
	
	public static String obtenerValidaHoraDesde(Context context) throws UtilException {
		try {
			String validaHoraDesde = null;
			ParametrosBL parametrosBL = new ParametrosBL(context);
			validaHoraDesde = ((Parametros) parametrosBL.obtenerParametroPorNombre(Util.VALIDA_HORA_DESDE)).getValParam();
			return validaHoraDesde;
		}
		catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
	}
	
	public static String obtenerValidaHoraHasta(Context context) throws UtilException {
		try {
			String validaHoraHasta = null;
			ParametrosBL parametrosBL = new ParametrosBL(context);
			validaHoraHasta = ((Parametros) parametrosBL.obtenerParametroPorNombre(Util.VALIDA_HORA_HASTA)).getValParam();
			return validaHoraHasta;
		}
		catch (Exception e) {
			throw new UtilException(e.getMessage(), e);
		}
	}
}