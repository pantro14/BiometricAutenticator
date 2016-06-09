package carvajal.autenticador.android.framework.possdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.activity.ReporteActivity;
import carvajal.autenticador.android.bl.AutenticadorSyncBL;
import carvajal.autenticador.android.bl.exception.AutenticadorSyncBLException;
import carvajal.autenticador.android.framework.possdk.exception.PDFReporteAutenticacionException;
import carvajal.autenticador.android.util.Util;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

/**
 * La Clase PDFReporteAutenticacion encapsula los atributos y m�todos necesarios
 * para generar el Documento en formato PDF del reporte de autenticaciones 
 * y de las aprobadas por el delegado, con la informaci�n requeria para imprimir.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since 18 de Junio de 2015
 */
public class PDFReporteAutenticacion {
	
	/** varaible globlal que representa el nombre 
	 * del documento en la tableta donde se aloja el PDF */
	private static String NOMBRE_DOCUMENTO;
	/** varaible globlal que representa la fuente del texto*/
	private final static String FUENTE_ARIAL = "ARIAL";
	/** varaible globlal que representa el ancho de p�gina */
	private final static Integer ANCHO_TOTAL_PAGINA = 800;
	/** varaible globlal que representa el ancho del documento PDF */
	private final static Integer ANCHO = 850;
	/** varaible globlal que representa el alto del comuento PDF */
	private final static Integer ALTO = 612;
	
	/** T�tulo del Documento PDF */
	private String txtTituloDoc;
	
	/** T�tulo de la Elecci�n */
	private String txtEleccion;
	
	/** T�tulo de la fecha de la elecci�n */
	private String txtFecha;
	
	/** Lista de c�dulas autenticadas y aprobadas por delegado */
	private ArrayList<String> listaCedula;
	
	/** Contexto de la actividad que instancia esta clase */
	private Context context;
	
	/** N�mero total de p�ginas a imprimir */
	private int paginasTotal;
	
	/**
	 * M�todo construtor de la clase PDFReporteAutenticacion
	 * 
	 * @param context
	 * 			  Contexto de la actividad que instancia esta clase
	 * @param txtTituloDoc
	 *            varaible de miembro, es el t�tulo del Documento PDF
	 * @param txtEleccion
	 *            varaible de miembro, es el t�tulo de la Elecci�n.
	 * @param txtFecha
	 *            varaible de miembro, es el t�tulo de la fecha de elecci�n.
	 * @param listaCedula
	 *            Lista de c�dulas autenticadas y aprobadas por delegado
	 */
	public PDFReporteAutenticacion(Context context,String txtTituloDoc, String txtEleccion,
			String txtFecha, ArrayList<String> listaCedula)
		throws PDFReporteAutenticacionException{
		super();
		this.context = context;
		this.txtTituloDoc = txtTituloDoc;
		this.txtEleccion = txtEleccion;
		this.txtFecha = txtFecha;
		this.listaCedula = listaCedula;
		NOMBRE_DOCUMENTO = context.getResources().getString(
				R.string.nombre_reporte_total_autenticacion);
	}
	
	/**
	 * Funci�n que Genera el documento PDF del reporte de Autenticaciones
	 * y lo almacena en memoria del dispositivo m�vil.
	 * 
	 * @return resultado
	 * 				atributo booleano. True si gener� el PDF, False si fall�.
	 */
	public boolean generarPDF() throws PDFReporteAutenticacionException{
		// resultado de la funci�n.
		boolean resultado = false;
		// Se crea el documento que vamos a generar.
		Document documento = new Document(new Rectangle(ANCHO, ALTO));
		try {		
			// Se crea el fichero con el nombre que deseemos.
			File f = crearFichero(NOMBRE_DOCUMENTO);
			// Se crea el flujo de datos de salida para el fichero donde
			// guardaremos el pdf.
			FileOutputStream ficheroPdf = new FileOutputStream(
					f.getAbsolutePath());
			// Se asocia el flujo que acabamos de crear al documento.
			PdfWriter.getInstance(documento, ficheroPdf);

			// Se abre el documento.
			documento.open();
			
			/**
			 * A partir de este punto se realiza una validaci�n con respecto
			 * al tama�o de la lista de c�dulas que se vana imprimir, gracias a 
			 * pruebas de concepto realizadas anteriormente, calculamos que en una 
			 * p�gina del PDF, se pueden alojar como m�ximo 380 c�dulas.
			 * Por lo tanto, cuando la lista de c�dulas exceda este tama�o, se 
			 * generar� una nueva p�gina en el documento.
			 */
			if(listaCedula.size()>380){
				//	La lista de c�dulas supera las 380
				//	Aumenta el n�mero de p�ginas a 2
				paginasTotal = 2;
				/*
				 * Se generan dos listas auxiliares, cada una llenar�
				 * una p�gina de las 2 hasta ahora generadas.
				 */
				//	Lista auxiliar 1 para las primeras 380 c�dulas - P�gina 1
				ArrayList<String>listaCedulaAux1 = new ArrayList<String>();
				//	Lista auxiliar 2 para las dem�s c�dulas - P�gina 2
				ArrayList<String>listaCedulaAux2 = new ArrayList<String>();
				//	Se llenan ambas lista auxiliares con la lista de c�dulas.
				for(int i=0; i<listaCedula.size(); i++){
					if(i<380){
						listaCedulaAux1.add(listaCedula.get(i));
					}else{
						listaCedulaAux2.add(listaCedula.get(i));
					}
				}
				//	Llamamos la funci�n para generar la P�gina 1
				generarPaginaPDF(documento, listaCedulaAux1, 1);
				// Se crea una nueva p�gina en el documento PDF
				documento.newPage();
				// Se crea una nueva pagina en el documento PDF
				generarPaginaPDF(documento, listaCedulaAux2, 2);
			}else{
				//	La lista de c�dulas no excede 380
				//	Se mantiene el documento en 1 p�gina
				paginasTotal = 1;
				//	Se crea una p�gina �nica con la lista de c�dulas original
				generarPaginaPDF(documento, listaCedula, 1);
			}
			//	Resultado de la generaci�n del documento satisfactorio.
			resultado = true;

		} catch (DocumentException e) {
			//	Exepci�n no controlada de DocumentException
			resultado = false;
			throw new PDFReporteAutenticacionException(e.getMessage());
		} catch (IOException e) {
			//	Exepci�n no controlada de IOException
			resultado = false;
			throw new PDFReporteAutenticacionException(e.getMessage());
		} finally {	
			//	Finalmente cerramos el documento.
			documento.close();
		}
		return resultado;
	}
	
	/**
	 * Funci�n que Genera una p�gina del documento PDF
	 * 
	 * @param documento 
	 * 				Documento que almacena el PDF
	 * @param listacCedulasAux 
	 * 				Lista de c�dulas autenticadas y aprobadas por delegado
	 * @return numPagina
	 * 				n�meraci�n de la p�gina a crear.
	 */
	private void generarPaginaPDF(Document documento,
			ArrayList<String> listacCedulasAux, int numPagina)
			throws PDFReporteAutenticacionException{
		try {
			/*
			 * En este punto se va a obtener el t�tulo de la
			 * p�gina y le enviamos como par�metro la
			 * numeraci�n de la p�gina a crear.
			 */
			documento.add(obtenerTablaTitulo(numPagina));
			
			//	Agregamos un salto de l�nea.
			documento.add(new Paragraph("\n"));

			/*
			 * En este punto se va a generar la tabla con la informaci�n
			 * electoral del lugar donde se genera el Reporte.
			 */
			
			/*	Se crea una tabla, asign�ndole 6 columnas, con tama�os por pesos
			*	de la siguiente forma:
			*	Columna 1: Peso de 3.
			*	Columna 2: Peso de 4.
			*	Columna 3: Peso de 6.
			*	Columna 4: Peso de 1.
			*	Columna 5: Peso de 2.
			*	Columna 6: Peso de 1.
			*/
			PdfPTable tablaInfoElectoral = new PdfPTable(new float[] { 3, 4, 6,
					1, 2, 1 });
			
			//	Se establece el ancho de la tabla
			tablaInfoElectoral.setTotalWidth(ANCHO_TOTAL_PAGINA);
			//	Se estabelce bloqueo de ancho de la tabla
			tablaInfoElectoral.setLockedWidth(true);
			/* Se agregan el texto de la tabla de Informaci�n electoral
			 * por cada una de las columans de esta tabla
			 *
			 * Columna 1: Campos de Provincia */
			tablaInfoElectoral.addCell(generarTituloCeldas("PROVINCIA",
					ReporteActivity.provincia));
			/* Columna 2: Campos de Municipio */
			tablaInfoElectoral.addCell(generarTituloCeldas("MUNICIPIO",
					ReporteActivity.municipio));
			/* Columan 3: Campos de Colegio Electoral */
			tablaInfoElectoral.addCell(generarTituloCeldas(
					"NOMBRE COLEGIO ELECTORAL", ReporteActivity.colegio));
			/* Columna 4: Campos de Zona */
			tablaInfoElectoral.addCell(generarTituloCeldas("ZONA", ReporteActivity.zona));
			/* Columna 5: Campos de Mesa */
			tablaInfoElectoral.addCell(generarTituloCeldas("MESA", ReporteActivity.mesa));
			/* Columna 6: Campos de Mesa */
			tablaInfoElectoral.addCell(generarTituloCeldas("HOJA", "0"+String.valueOf(numPagina)));
			// Se agrega la talba al documento PDF
			documento.add(tablaInfoElectoral);

			/*
			 * En este punto se va a generar la tabla con la lista
			 * c�dulas autenticadas y aprobadas delegado.
			 */
			/*	Se crea una tabla, asign�ndole 10 columnas, con tama�os iguales */
			PdfPTable tablaCedulas = new PdfPTable(10);
			//	Se establece el ancho de la tabla	
			tablaCedulas.setTotalWidth(ANCHO_TOTAL_PAGINA);
			//	Se estabelce bloqueo de ancho de la tabla
			tablaCedulas.setLockedWidth(true);
			//	Se asigna una fuente diferente para mostrar las c�dulas
			Font fuenteCedula = FontFactory.getFont(FUENTE_ARIAL, 7,
					Font.NORMAL);

			/*
			 * Se recorre la lista de c�dulas con el fin de guardarla en cada 
			 * celda de la tabla tablaCedulas
			 */
			for (int i = 0; i < listacCedulasAux.size(); i++) {
				//	Se crea una celda donde se asigna el valor de la c�dula
				//	de la lista, adem�s de asignar la fuente.
				PdfPCell celdaCedula = new PdfPCell(new Phrase(listacCedulasAux.get(
						i).toString(), fuenteCedula));
				//	Se establece alineaci�n derecha en la celda
				celdaCedula.setHorizontalAlignment(Element.ALIGN_RIGHT);
				//	Se agrega la celda a la tabla
				tablaCedulas.addCell(celdaCedula);
			}
			//	Si las c�dulas no completan la tabla, se le hace
			//	 un autocompletado de filas a la tabla
			tablaCedulas.completeRow();
			
			//	Se agrega la tabla al documento
			documento.add(tablaCedulas);
			
			//	Se realiza un salto de p�gina
			documento.add(new Paragraph("\n"));
		} catch (DocumentException e) {
			//	Exepci�n no controlada de DocumentException
			throw new PDFReporteAutenticacionException(e.getMessage());
		} catch (IOException e) {
			//	Exepci�n no controlada de IOException
			throw new PDFReporteAutenticacionException(e.getMessage());
		}
	}
	
	/**
	 * Funci�n que Abre el documento PDF generado
	 * 
	 */
	public void abrirPDF() throws PDFReporteAutenticacionException{
		//	Se genera la ruta en donde se alamecn� el Documento
		File archivoPDF = new File(Util.obtenerAlmacenamientoSecundario()
				+ "/" + NOMBRE_DOCUMENTO);
		//	Hacemos un Intent a la aplicaci�n de tercero para imprimir
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(archivoPDF),"application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Intent intent = Intent.createChooser(target, "Open File");
		
		/*Uri uri = Uri.fromFile(file);
	    Intent intent = new Intent ("org.androidprinting.intent.action.PRINT");
	    intent.setDataAndType(uri, "application/pdf");*/
		try {
		    context.startActivity(intent);
			//startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException e) {
			throw new PDFReporteAutenticacionException(e.getMessage());
		}
	}
	
	public String ServicioImprimirReporteSync() throws PDFReporteAutenticacionException{
		String respuestaServicio = "-1";
		try {
			AutenticadorSyncBL autSyncBL = new AutenticadorSyncBL(
					context, context.getResources().getString(
							R.string.metodo_imprimir_reporte));
			File archivoPDF = new File(Util.obtenerAlmacenamientoSecundario()
					+ "/" + NOMBRE_DOCUMENTO);
			respuestaServicio = autSyncBL.imprimirReportePDF(archivoPDF);			
		} catch (NotFoundException e) {
			throw new PDFReporteAutenticacionException(e.getMessage());
		} catch (AutenticadorSyncBLException e) {
			throw new PDFReporteAutenticacionException(e.getMessage());
		}
		return respuestaServicio;
	}

	/**
	 * Funci�n para generar una Celda del t�tulo de Informaci�n electoral
	 * 
	 * @param titulo
	 * 			es el t�tulo de la celda
	 * @param nombre
	 * 			es el campo de nombre de la celda
	 * @return celda
	 * 			la celda que se genera
	 */
	private Paragraph generarTituloCeldas(String titulo, String nombre){
		//	Se asigna un tipo de fuente al t�tulo principal
		Font fuenteTitulo = FontFactory.getFont(FUENTE_ARIAL, 10, Font.BOLD);
		//	Se asigna un tipo de fuente al nombre de la celda
		Font fuenteNombre = FontFactory.getFont(FUENTE_ARIAL, 8, Font.NORMAL);
		//	Se crea la celda
		Paragraph celda = new Paragraph();
		//	Se crea el t�tulo
		Phrase tituloPhrase = new Phrase(titulo+"\n", fuenteTitulo);
		// Se cre el nombre
		Phrase nombrePhrase = new Phrase(nombre, fuenteNombre);
		//	Se agrega el t�tulo y el nombre a la celda.
		celda.add(tituloPhrase);
		celda.add(nombrePhrase);
		//	Retorna la celda.
		return celda;
	}
	/**
	 * Funci�n encargada de Crear el t�tulo del documento PDF
	 * 
	 * @return celda
	 * 			la celda que se genera
	 */
	private Paragraph generarTituloDoc(){
		//	Se asigna el tipo de fuente al titulo de la cleda
		Font fuenteTitulo = FontFactory.getFont(FUENTE_ARIAL, 14, Font.BOLD);
		//	Se asigna el tipo de fuente al nombre de la Elecci�n de la cleda
		Font fuenteEleccion = FontFactory.getFont(FUENTE_ARIAL, 10, Font.BOLD);
		//	Se asigna el tipo de fuente a la fecha de la celda
		Font fuenteFecha = FontFactory.getFont(FUENTE_ARIAL, 8, Font.NORMAL);
		//	Se crea la celda
		Paragraph celda = new Paragraph();
		//	Se crea el t�tulo del Documento
		Phrase tituloPhrase = new Phrase(txtTituloDoc+"\n", fuenteTitulo);
		//	Se crea el nombre de la elecci�n
		Phrase nombrePhrase = new Phrase(txtEleccion+"\n", fuenteEleccion);
		//	Se crea la fecha de la elecci�n
		Phrase fechaPhrase = new Phrase(txtFecha+"\n", fuenteFecha);
		//	Se agrega el t�tulo a la celda
		celda.add(tituloPhrase);
		//	Se agrega el nombre de la eleci�n a la celda
		celda.add(nombrePhrase);
		//	Se agrega la fecha de la elecci�n a la celda
		celda.add(fechaPhrase);
		//	Se retorna la celda
		return celda;
	}
	
	/**
	 * Esta funci�n Parametriza los campos del t�tulo por p�gina
	 * en el documento PDF.
	 * 
	 * @param numPagina
	 * 				Es el indice de la p�gina 
	 * @return PdfPTable
	 * 				La talba en la cual va el t�tulo con la
	 * 				informaci�n electoral.
	 * 
	 */
	private PdfPTable obtenerTablaTitulo(int numPagina) throws BadElementException,
			MalformedURLException, IOException {
		/*
		 * En este punto creamos una tabla con 3 columnas asignado por pesos
		 * el tama�o de la celda como se muestra a continuaci�n:
		 * Columna 1: Peso de 2.
		 * Columna 2: Peso de 2.
		 * Columan 3: Peso de 1.
		 */
		PdfPTable tablaTitulo = new PdfPTable(new float[] { 2, 2, 1 });
		
		//	Se establece el ancho de la tabla
		tablaTitulo.setTotalWidth(ANCHO_TOTAL_PAGINA);
		//	Se estabelce bloqueo de ancho de la tabla
		tablaTitulo.setLockedWidth(true);
		//	Se crea la celda del t�tulo del Documento.
		PdfPCell celdatituloDoc = new PdfPCell(generarTituloDoc());

		// Se crea la imagen de la junta Electoral
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_junta_central_imagen);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		Image imagen = Image.getInstance(stream.toByteArray());
		imagen.scaleToFit(150, 40);
		
		//Se crea la celda de la imagen del PDF
		PdfPCell celdaImagen = new PdfPCell(imagen);
		
		//	Se asigna una fuente para el texto del t�tulo del Documento PDF.
		Font piePaginaFont = FontFactory.getFont(FUENTE_ARIAL, 8,
				Font.BOLDITALIC);
		
		//	Se crea una celda con la informaci�n de la paginaci�n
		PdfPCell celdaPagina = new PdfPCell(new Phrase(
				  "P�gina 0"+String.valueOf(numPagina)+
				  " de 0"+String.valueOf(paginasTotal),
					piePaginaFont));
		
		//	Se establece que las celdas no tengan borde
		celdatituloDoc.setBorder(Rectangle.NO_BORDER);
		celdaImagen.setBorder(Rectangle.NO_BORDER);
		celdaPagina.setBorder(Rectangle.NO_BORDER);
		//	Se al�nean las celdas a la Derecha.
		celdaPagina.setHorizontalAlignment(Element.ALIGN_RIGHT);
		//	Se agregan las celdas a la tabla
		tablaTitulo.addCell(celdatituloDoc);
		tablaTitulo.addCell(celdaImagen);
		tablaTitulo.addCell(celdaPagina);
		//	Se retorna la tabla del t�tulo del documento
		return tablaTitulo;
	}
	
	/**
	 * Funci�n que se encarga de crear el fichero PDF
	 * 
	 * @param nombreFichero
	 * 				valor del nombre del archivo
	 * @return File
	 * 				El archivo creado
	 * 
	 */
	public static File crearFichero(String nombreFichero) throws IOException {
		File ruta = obtenerRuta();
		File fichero = null;
		if (ruta != null)
			fichero = new File(ruta, nombreFichero);
		return fichero;
	}
	
	/**
	 * Funci�n que sirve para obtener la ruta del fichero PDF
	 * @return
	 * 		El fichero PDF
	 */
	public static File obtenerRuta() {
		/* El fichero ser� almacenado en un directorio dentro del directorio
		* Descargas
		*/
		File ruta = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			ruta = new File(Util.obtenerAlmacenamientoSecundario());
			if (ruta != null) {
				if (!ruta.mkdirs()) {
					if (!ruta.exists()) {
						return null;
					}
				}
			}
		} else {
			//	Implementaci�n si no se encuentra la ruta.
		}
		return ruta;
	}
}
