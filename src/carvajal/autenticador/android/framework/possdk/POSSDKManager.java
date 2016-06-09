package carvajal.autenticador.android.framework.possdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;





import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.framework.possdk.exception.PDFReporteAutenticacionException;
import carvajal.autenticador.android.framework.possdk.exception.POSSDKManagerException;

import com.lvrenyang.pos.Pos;
import com.lvrenyang.rw.PL2303Driver;
import com.lvrenyang.rw.TTYTermios;
import com.lvrenyang.rw.USBPort;
import com.lvrenyang.rw.USBSerialPort;
import com.lvrenyang.rw.TTYTermios.FlowControl;
import com.lvrenyang.rw.TTYTermios.Parity;
import com.lvrenyang.rw.TTYTermios.StopBits;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;


/**
 * La Clase POSSDKManager implementa los atributos
 * y funciones de la librería possdk.jar con la cual
 * se imprime tanto el texto como imagenes en la 
 * impresora configurada en el dispositivo móvil.
 * 
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   17 de Febrero de 2015
 */
public class POSSDKManager {

	/** instancia única estática de la clase Singleton POSSDKManager. */
	private static POSSDKManager instancia = null;
	/** variable Activity que implementa las funciones de impresión. */
	private Context context;
	/** variable puerto serial al cual se asigna la conexión con la impresora. */
	private static USBSerialPort serialPort;
	/** variable estandar con la cual de identifica un puerto configurado a la impresora */
	private static PL2303Driver mSerial;
	/** variable asistente que implementa las funciones de la impresora. */
	private static Pos mPos;

	/**
	 * Método constructor de la clase POSSDKManager
	 */
	private POSSDKManager(Context context) {
		this.context = context;
		mSerial = new PL2303Driver();
		serialPort = new USBSerialPort(null, null);
		mPos = new Pos(serialPort, mSerial);
	}

	/**
	 * Método que obtiene la Instancia estática Singleton
	 * @return instancia estática de la clase POSSDKManager
	 */
	public static POSSDKManager getInstance(Context context) {
		if (instancia == null) {
			instancia = new POSSDKManager(context);
		}
		return instancia;
	}
	
	/**
	 * Este método consulta en la lista de puertos periféricos conectados
	 * al dispositivo móvil, cual de estos está asociado a la impresora,
	 * finalmente prueba que dicha conexión esté disponible.
	 * @return variable boolean en la cual se almacena el resultado de 
	 * la verificación de puertos. True: el puerto de la impresora está conectado. 
	 * False: ningún puerto está conectado a la impresora.
	 * @throws Exception: Lanza una exepcion si falla la conexión con el puerto.
	 */
	public boolean verificarConexionPuertos() throws POSSDKManagerException {
		boolean reultadoVerificacion = false;
		try{
			//	Construcción del Manejador de USB conectadas al dispositivo móvil.
			final UsbManager mUsbManager = (UsbManager) context
					.getSystemService(Context.USB_SERVICE);
			//	Se obtiene la lista de conexiones USB al dispositivo móvil.
			HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
			//	Iterador de la lista de conexiones USB
			Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
			// Si la lista de conxiones USB es mayor a cero)
			if (deviceList.size() > 0) {
				//	Recorre el iterador de cada conexión USB
				while (deviceIterator.hasNext()) {
					//	device: cada c9onexión USB del dispositivo móvil
					final UsbDevice device = deviceIterator.next();
					// Permisos para interactuar con conexión.
					PendingIntent mPermissionIntent = PendingIntent.getBroadcast(
							context, 0, new Intent(
									context.getApplicationInfo().packageName), 0);
					//	Se asigna al puerto serial, la conexión USB presente en esa iteración 
					serialPort.port = new USBPort(mUsbManager, context, device,
							mPermissionIntent);
					//	Se prueba el puerto serial
					int ret = mSerial.pl2303_probe(serialPort);
					if (ret == 0) {
						//	Éxtio en la conexión
						reultadoVerificacion = true;
						// Si encuentra el puerto serial conctado a la impresora, termina el ciclo while
						break;
					} else {
						//	Falló la conexión y sigue buscando, no hace nada
					}
				}
			} else {
				System.out.println("No hay dispositivos conectados");
			}
			return reultadoVerificacion;
			
		}catch(Exception e)
		{
			//	Excepción inesperada al momento de verificar puerto
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
			//	Retorna falso, ya que no tuvo éxito al verificar los puertos
			return reultadoVerificacion;
		}
	}
	

	/**
	 * Este método permite imprimir un texto y configurar sus propiedades.
	 * 
	 * @param texto en caracteres.
	 * @param coordX origen en el eje x del texto con respecto al plano.
	 * @param ancho ancho de la letra del texto.
	 * @param alto alto de la letra del texto.
	 * @param tipoFuente tipo de fuente del texto.
	 * @param estiloFuente estilo de fuente del texto.
	 */
	public void imprimirTexto(String texto, int coordX, int ancho, int alto,
			int tipoFuente, int estiloFuente) throws POSSDKManagerException{
		try {
			//	Conexión abierta.
			abrirConexion(115200, Parity.NONE);
			//	Instrucción de imprimir texto con los parámetros del estilo.
			mPos.POS_S_TextOut(texto, coordX, ancho, alto, tipoFuente, estiloFuente);
			//	Imprime una linea en blanco en el papel.
			mPos.POS_FeedLine();
			//	Conexión cerrada.
			cerrarConexion();
		} catch (Exception e) {
//			Excepción inesperada al momento de imprimir texto.
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
		}
	}
	
	/**
	 * Este método permite imprimir una imagen 
	 * @param bitmap formato de la imagen en mapa de bits que se imprime
	 */
	public void imprimirImagen(Bitmap bitmap) throws POSSDKManagerException{
		try {
			//	Conexión abierta
			abrirConexion(115200, Parity.NONE);
			//	Instrucción de imprimir imagen.
			mPos.POS_PrintPicture(bitmap, 360, 2);
			//	Imprime una linea en blanco en el papel.
			mPos.POS_FeedLine();
			//	Imprime una linea en blanco en el papel.
			mPos.POS_FeedLine();
			//	Imprime una linea en blanco en el papel.
			mPos.POS_FeedLine();
			//	cONEXIÓN CERRADA.
			cerrarConexion();
		} catch (Exception e) {
			//	Excepción inesperada al momento de imprimir imagen.
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
		}
	}
	
	/**
	 * Método con el cual se abre la conexión al puerto serial que
	 * está conectado a la impresora.
	 * @param baudrate 
	 * @param parity
	 */
	public void abrirConexion(final int baudrate, final Parity parity) throws POSSDKManagerException{
		try {
			TTYTermios termios = serialPort.termios;
			serialPort.termios = new TTYTermios(baudrate, FlowControl.NONE,
					parity, StopBits.ONE, 8);
			//	Se abre el puerto serial conectado al hardware de la impresora
			mSerial.pl2303_open(serialPort, termios);
		} catch (Exception e) {
			//	Excepción inesperada al momento de abrir conexión
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
		}
	}

	/**
	 * Método con el cual se cierra la conexión con el puerto serial
	 * que está conectado a la impresora.
	 */
	private void cerrarConexion() throws POSSDKManagerException{
		try {
			// Se cierra el puerto serial conectado al hardware de la impresora
			mSerial.pl2303_close(serialPort);
		} catch (Exception e) {
			//	Excepción inesperada al momento de cerrar conexión
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
		}
	}

	/**
	 * Método con el cual se desconecta del puerto serial la impresora.
	 * funcion de la misma manera como si se apagara la impresora.
	 */
	public void desconectarImpresora() throws POSSDKManagerException {
		try {
			//	Conexión cerrada
			cerrarConexion();
			//	Desconectar el pueto serial conectado al hardware de la impresora
			mSerial.pl2303_disconnect(serialPort);
		} catch (Exception e) {
			//	Excepción inesperada al momento de desconectar impresora
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
		}
	}
	
	/**
	 * EL método imprimir comprobante tiene el fin de recibir los parámetros que se
	 * van a imprimir en el papel combrobante, dibujando a través de un objeto
	 * ImagenComprobanteView (que extiende de un ImagenView) todo el formato
	 * del tamaño y ubicación del texto que se va a imprimir. Después la imagen se
	 * situa sobre un layout lo cual activa el dibujado del canvas, el cual finalmente
	 * se convierte en un archivo bitmap el cual se envía finarlmente al método imprimirImagen
	 * @param nombreEleccion 
	 * @param cedulaElector
	 * @param numeroMesaElector
	 * @param fechaAutenticacion
	 * @param duplicado true si el comprobante se imprime como duplicado, false si es impreso por primera vez.
	 * @param esJurado true si es un jurado, false si es un elector normal.
	 * @return
	 * @throws POSSDKManagerException
	 */
	public boolean imprimirComprobante (String nombreEleccion,
			String cedulaElector, String numeroMesaElector,
			String fechaAutenticacion, boolean duplicado, boolean esJurado) throws POSSDKManagerException{
		//	Se crea un relative layout en el cual se va a agregar el ImagenComprobanteView.
		RelativeLayout layoutImagen = new RelativeLayout(context);
		//	Se define los parametros de altura y anchura del layout.
		layoutImagen.setLayoutParams(new ViewGroup.LayoutParams(300,200));		
		//	Se define un fondo en blanco para el comprobante.
		layoutImagen.setBackgroundColor(Color.WHITE);
		//	Se instancia el objeto ImagenComprobanteView, sobre el cual se va a generar
		//	el comprobante de autenticación con la información requerida.
		ImagenComprobanteView imagen = new ImagenComprobanteView(context,
				nombreEleccion, cedulaElector, numeroMesaElector, fechaAutenticacion, duplicado, esJurado);
		//	Creación de los parámetros del layout
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300,200);
		//	Definición del alineamiento a la izquierda y ea anchor.
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		//	Finalmente se agrega la imagen al layout, lo cual activa el método onDraw del ImagenComprobanteView.
		layoutImagen.addView(imagen, params);
		try{
			//	Verifica la conexión de los puertos con la impresora.
			if(verificarConexionPuertos()){ 
				//	Se define permiso para tomar de la memoria cache el proceso del dibujo del comprobante.
				layoutImagen.setDrawingCacheEnabled(true);
				//	Se definen las dimensiones del Canva en ImagenComprobanteView.
				layoutImagen.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
			            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				layoutImagen.layout(0, 0, layoutImagen.getMeasuredWidth(), layoutImagen.getMeasuredHeight());
				//	Se toma en la memoria cache del proceso el dibujo del comprobante.
				layoutImagen.buildDrawingCache(true);
				//	Creación del objeto de mapa de bits, a partir del layout del comprobante.
				Bitmap bitmap = layoutImagen.getDrawingCache(); 
				//	Finalmente se imprime el mapa de bit del comprobante.
				imprimirImagen(bitmap);
				//	Se desactiva permiso para tomar de la memoria cache el proceso del dibujo del comprobante.
				layoutImagen.setDrawingCacheEnabled(false);
				// retorna true, ya que se imprimió con éxtio.
				return true;
			}else{
				// retorna false, ya que no encontró puertos conectados a la impresora.
				return false;
			}
		}catch(Exception e){
			//	Excepción inesperada al momento de evaluar los puertps conextado a la impresora.
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
			//	REtorna falso, ya que no se pudo imprimir.
			return false;
		}					
	}
	
	/**
	 * EL método imprimir reporte tiene como fin, mostrar cada una de las cédulas
	 * Autorizadas por el delegado al momento de hacer la autenticación, además de mostrar
	 * la información sobre el puesto de votación, este debe imprimir
	 * @param codigoPuesto es el código del puesto de la estación de votación.
	 * @param puesto es el nombre del puesto de la estación de votación.
	 * @param listaCedulas Corresponde a las cédulas autorizadas por el delegado al autenticar.
	 * @return un valor booleano correspondiente a la respuesta del método.
	 * @throws POSSDKManagerException
	 */
	public boolean imprimirReporteAutorizaDelegado(String codigoPuesto, String puesto,
			ArrayList<Long> listaCedulas) throws POSSDKManagerException {
		/*
		 * Dependiendo del número de autenticaciones, defino el Tamaño "alto"
		 * del reporte después de hacer pruebas de concepto geométricas se llegó
		 * a la conclusión. - Alto: 300, soporta 18 cédulas. - Alto: 400,
		 * soporta 39 cédulas. - Alto: 800, soporta 106 cédulas
		 */
		int ancho = 300;
		int alto = 0;
		if(listaCedulas.size()>9){
				alto = (int) Math.ceil((5.0794 * listaCedulas.size())+206.9054)+10;
		}else{
			alto = 250;
		}

		RelativeLayout layoutReporte = new RelativeLayout(context);
		// Se define los parametros de altura y anchura del layout.
		layoutReporte.setLayoutParams(new ViewGroup.LayoutParams(ancho, alto));
		// Se define un fondo en blanco para el comprobante.
		layoutReporte.setBackgroundColor(Color.WHITE);
		// Se instancia el objeto ImagenComprobanteView, sobre el cual se va a
		// generar
		// el comprobante de autenticación con la información requerida.
		ImagenReporteView imagenReporte = new ImagenReporteView(context, ancho,
				alto, codigoPuesto, puesto, listaCedulas);
		// Creación de los parámetros del layout
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ancho, alto);
		// Definición del alineamiento a la izquierda y ea anchor.
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		// Finalmente se agrega la imagen al layout, lo cual activa el método
		// onDraw del ImagenComprobanteView.
		layoutReporte.addView(imagenReporte, params);
		try {
			// Verifica la conexión de los puertos con la impresora.
			if (verificarConexionPuertos()) {
				// Se define permiso para tomar de la memoria cache el proceso
				// del dibujo del comprobante.
				layoutReporte.setDrawingCacheEnabled(true);
				// Se definen las dimensiones del Canva en
				// ImagenComprobanteView.
				layoutReporte
						.measure(MeasureSpec.makeMeasureSpec(0,
								MeasureSpec.UNSPECIFIED), MeasureSpec
								.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				layoutReporte.layout(0, 0, layoutReporte.getMeasuredWidth(),
						layoutReporte.getMeasuredHeight());
				// Se toma en la memoria cache del proceso el dibujo del
				// comprobante.
				layoutReporte.buildDrawingCache(true);
				// Creación del objeto de mapa de bits, a partir del layout del
				// comprobante.
				Bitmap bitmap = layoutReporte.getDrawingCache();
				// Finalmente se imprime el mapa de bit del comprobante.
				imprimirImagen(bitmap);
				// Se desactiva permiso para tomar de la memoria cache el
				// proceso del dibujo del comprobante.
				layoutReporte.setDrawingCacheEnabled(false);
				// retorna true, ya que se imprimió con éxtio.
				return true;
			} else {
				// retorna false, ya que no encontró puertos conectados a la
				// impresora.
				return false;
			}
		} catch (Exception e) {
			// Excepción inesperada al momento de evaluar los puertps conextado
			// a la impresora.
			new POSSDKManagerException(e.getLocalizedMessage().toString(), e);
			// REtorna falso, ya que no se pudo imprimir.
			return false;
		}
	}
	
	public void conexionImpresora() throws POSSDKManagerException {
		try {

			verificarConexionPuertos();

			abrirConexion(115200, Parity.NONE);
		} catch (POSSDKManagerException e) {
			throw new POSSDKManagerException(e.getMessage());
		}
	}
}
