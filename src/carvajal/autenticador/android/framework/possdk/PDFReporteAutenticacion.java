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
 * La Clase PDFReporteAutenticacion encapsula los atributos y métodos necesarios
 * para generar el Documento en formato PDF del reporte de autenticaciones 
 * y de las aprobadas por el delegado, con la información requeria para imprimir.
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
	/** varaible globlal que representa el ancho de página */
	private final static Integer ANCHO_TOTAL_PAGINA = 800;
	/** varaible globlal que representa el ancho del documento PDF */
	private final static Integer ANCHO = 850;
	/** varaible globlal que representa el alto del comuento PDF */
	private final static Integer ALTO = 612;
	
	/** Título del Documento PDF */
	private String txtTituloDoc;
	
	/** Título de la Elecciòn */
	private String txtEleccion;
	
	/** Título de la fecha de la elección */
	private String txtFecha;
	
	/** Lista de cédulas autenticadas y aprobadas por delegado */
	private ArrayList<String> listaCedula;
	
	/** Contexto de la actividad que instancia esta clase */
	private Context context;
	
	/** Número total de páginas a imprimir */
	private int paginasTotal;
	
	/**
	 * Método construtor de la clase PDFReporteAutenticacion
	 * 
	 * @param context
	 * 			  Contexto de la actividad que instancia esta clase
	 * @param txtTituloDoc
	 *            varaible de miembro, es el título del Documento PDF
	 * @param txtEleccion
	 *            varaible de miembro, es el título de la Elección.
	 * @param txtFecha
	 *            varaible de miembro, es el título de la fecha de elección.
	 * @param listaCedula
	 *            Lista de cédulas autenticadas y aprobadas por delegado
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
	 * Función que Genera el documento PDF del reporte de Autenticaciones
	 * y lo almacena en memoria del dispositivo móvil.
	 * 
	 * @return resultado
	 * 				atributo booleano. True si generó el PDF, False si falló.
	 */
	public boolean generarPDF() throws PDFReporteAutenticacionException{
		// resultado de la función.
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
			 * A partir de este punto se realiza una validación con respecto
			 * al tamaño de la lista de cédulas que se vana imprimir, gracias a 
			 * pruebas de concepto realizadas anteriormente, calculamos que en una 
			 * página del PDF, se pueden alojar como máximo 380 cédulas.
			 * Por lo tanto, cuando la lista de cédulas exceda este tamaño, se 
			 * generará una nueva página en el documento.
			 */
			if(listaCedula.size()>380){
				//	La lista de cédulas supera las 380
				//	Aumenta el número de páginas a 2
				paginasTotal = 2;
				/*
				 * Se generan dos listas auxiliares, cada una llenará
				 * una página de las 2 hasta ahora generadas.
				 */
				//	Lista auxiliar 1 para las primeras 380 cédulas - Página 1
				ArrayList<String>listaCedulaAux1 = new ArrayList<String>();
				//	Lista auxiliar 2 para las demás cédulas - Página 2
				ArrayList<String>listaCedulaAux2 = new ArrayList<String>();
				//	Se llenan ambas lista auxiliares con la lista de cédulas.
				for(int i=0; i<listaCedula.size(); i++){
					if(i<380){
						listaCedulaAux1.add(listaCedula.get(i));
					}else{
						listaCedulaAux2.add(listaCedula.get(i));
					}
				}
				//	Llamamos la función para generar la Página 1
				generarPaginaPDF(documento, listaCedulaAux1, 1);
				// Se crea una nueva página en el documento PDF
				documento.newPage();
				// Se crea una nueva pagina en el documento PDF
				generarPaginaPDF(documento, listaCedulaAux2, 2);
			}else{
				//	La lista de cédulas no excede 380
				//	Se mantiene el documento en 1 página
				paginasTotal = 1;
				//	Se crea una página única con la lista de cédulas original
				generarPaginaPDF(documento, listaCedula, 1);
			}
			//	Resultado de la generación del documento satisfactorio.
			resultado = true;

		} catch (DocumentException e) {
			//	Exepción no controlada de DocumentException
			resultado = false;
			throw new PDFReporteAutenticacionException(e.getMessage());
		} catch (IOException e) {
			//	Exepción no controlada de IOException
			resultado = false;
			throw new PDFReporteAutenticacionException(e.getMessage());
		} finally {	
			//	Finalmente cerramos el documento.
			documento.close();
		}
		return resultado;
	}
	
	/**
	 * Función que Genera una página del documento PDF
	 * 
	 * @param documento 
	 * 				Documento que almacena el PDF
	 * @param listacCedulasAux 
	 * 				Lista de cédulas autenticadas y aprobadas por delegado
	 * @return numPagina
	 * 				númeración de la página a crear.
	 */
	private void generarPaginaPDF(Document documento,
			ArrayList<String> listacCedulasAux, int numPagina)
			throws PDFReporteAutenticacionException{
		try {
			/*
			 * En este punto se va a obtener el título de la
			 * página y le enviamos como parámetro la
			 * numeración de la página a crear.
			 */
			documento.add(obtenerTablaTitulo(numPagina));
			
			//	Agregamos un salto de línea.
			documento.add(new Paragraph("\n"));

			/*
			 * En este punto se va a generar la tabla con la información
			 * electoral del lugar donde se genera el Reporte.
			 */
			
			/*	Se crea una tabla, asignándole 6 columnas, con tamaños por pesos
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
			/* Se agregan el texto de la tabla de Información electoral
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
			 * cédulas autenticadas y aprobadas delegado.
			 */
			/*	Se crea una tabla, asignándole 10 columnas, con tamaños iguales */
			PdfPTable tablaCedulas = new PdfPTable(10);
			//	Se establece el ancho de la tabla	
			tablaCedulas.setTotalWidth(ANCHO_TOTAL_PAGINA);
			//	Se estabelce bloqueo de ancho de la tabla
			tablaCedulas.setLockedWidth(true);
			//	Se asigna una fuente diferente para mostrar las cédulas
			Font fuenteCedula = FontFactory.getFont(FUENTE_ARIAL, 7,
					Font.NORMAL);

			/*
			 * Se recorre la lista de cédulas con el fin de guardarla en cada 
			 * celda de la tabla tablaCedulas
			 */
			for (int i = 0; i < listacCedulasAux.size(); i++) {
				//	Se crea una celda donde se asigna el valor de la cédula
				//	de la lista, además de asignar la fuente.
				PdfPCell celdaCedula = new PdfPCell(new Phrase(listacCedulasAux.get(
						i).toString(), fuenteCedula));
				//	Se establece alineación derecha en la celda
				celdaCedula.setHorizontalAlignment(Element.ALIGN_RIGHT);
				//	Se agrega la celda a la tabla
				tablaCedulas.addCell(celdaCedula);
			}
			//	Si las cédulas no completan la tabla, se le hace
			//	 un autocompletado de filas a la tabla
			tablaCedulas.completeRow();
			
			//	Se agrega la tabla al documento
			documento.add(tablaCedulas);
			
			//	Se realiza un salto de página
			documento.add(new Paragraph("\n"));
		} catch (DocumentException e) {
			//	Exepción no controlada de DocumentException
			throw new PDFReporteAutenticacionException(e.getMessage());
		} catch (IOException e) {
			//	Exepción no controlada de IOException
			throw new PDFReporteAutenticacionException(e.getMessage());
		}
	}
	
	/**
	 * Función que Abre el documento PDF generado
	 * 
	 */
	public void abrirPDF() throws PDFReporteAutenticacionException{
		//	Se genera la ruta en donde se alamecnó el Documento
		File archivoPDF = new File(Util.obtenerAlmacenamientoSecundario()
				+ "/" + NOMBRE_DOCUMENTO);
		//	Hacemos un Intent a la aplicación de tercero para imprimir
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
	 * Función para generar una Celda del título de Información electoral
	 * 
	 * @param titulo
	 * 			es el título de la celda
	 * @param nombre
	 * 			es el campo de nombre de la celda
	 * @return celda
	 * 			la celda que se genera
	 */
	private Paragraph generarTituloCeldas(String titulo, String nombre){
		//	Se asigna un tipo de fuente al título principal
		Font fuenteTitulo = FontFactory.getFont(FUENTE_ARIAL, 10, Font.BOLD);
		//	Se asigna un tipo de fuente al nombre de la celda
		Font fuenteNombre = FontFactory.getFont(FUENTE_ARIAL, 8, Font.NORMAL);
		//	Se crea la celda
		Paragraph celda = new Paragraph();
		//	Se crea el título
		Phrase tituloPhrase = new Phrase(titulo+"\n", fuenteTitulo);
		// Se cre el nombre
		Phrase nombrePhrase = new Phrase(nombre, fuenteNombre);
		//	Se agrega el título y el nombre a la celda.
		celda.add(tituloPhrase);
		celda.add(nombrePhrase);
		//	Retorna la celda.
		return celda;
	}
	/**
	 * Función encargada de Crear el título del documento PDF
	 * 
	 * @return celda
	 * 			la celda que se genera
	 */
	private Paragraph generarTituloDoc(){
		//	Se asigna el tipo de fuente al titulo de la cleda
		Font fuenteTitulo = FontFactory.getFont(FUENTE_ARIAL, 14, Font.BOLD);
		//	Se asigna el tipo de fuente al nombre de la Elección de la cleda
		Font fuenteEleccion = FontFactory.getFont(FUENTE_ARIAL, 10, Font.BOLD);
		//	Se asigna el tipo de fuente a la fecha de la celda
		Font fuenteFecha = FontFactory.getFont(FUENTE_ARIAL, 8, Font.NORMAL);
		//	Se crea la celda
		Paragraph celda = new Paragraph();
		//	Se crea el título del Documento
		Phrase tituloPhrase = new Phrase(txtTituloDoc+"\n", fuenteTitulo);
		//	Se crea el nombre de la elección
		Phrase nombrePhrase = new Phrase(txtEleccion+"\n", fuenteEleccion);
		//	Se crea la fecha de la elección
		Phrase fechaPhrase = new Phrase(txtFecha+"\n", fuenteFecha);
		//	Se agrega el título a la celda
		celda.add(tituloPhrase);
		//	Se agrega el nombre de la eleción a la celda
		celda.add(nombrePhrase);
		//	Se agrega la fecha de la elección a la celda
		celda.add(fechaPhrase);
		//	Se retorna la celda
		return celda;
	}
	
	/**
	 * Esta función Parametriza los campos del título por página
	 * en el documento PDF.
	 * 
	 * @param numPagina
	 * 				Es el indice de la página 
	 * @return PdfPTable
	 * 				La talba en la cual va el título con la
	 * 				información electoral.
	 * 
	 */
	private PdfPTable obtenerTablaTitulo(int numPagina) throws BadElementException,
			MalformedURLException, IOException {
		/*
		 * En este punto creamos una tabla con 3 columnas asignado por pesos
		 * el tamaño de la celda como se muestra a continuación:
		 * Columna 1: Peso de 2.
		 * Columna 2: Peso de 2.
		 * Columan 3: Peso de 1.
		 */
		PdfPTable tablaTitulo = new PdfPTable(new float[] { 2, 2, 1 });
		
		//	Se establece el ancho de la tabla
		tablaTitulo.setTotalWidth(ANCHO_TOTAL_PAGINA);
		//	Se estabelce bloqueo de ancho de la tabla
		tablaTitulo.setLockedWidth(true);
		//	Se crea la celda del título del Documento.
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
		
		//	Se asigna una fuente para el texto del título del Documento PDF.
		Font piePaginaFont = FontFactory.getFont(FUENTE_ARIAL, 8,
				Font.BOLDITALIC);
		
		//	Se crea una celda con la información de la paginación
		PdfPCell celdaPagina = new PdfPCell(new Phrase(
				  "Página 0"+String.valueOf(numPagina)+
				  " de 0"+String.valueOf(paginasTotal),
					piePaginaFont));
		
		//	Se establece que las celdas no tengan borde
		celdatituloDoc.setBorder(Rectangle.NO_BORDER);
		celdaImagen.setBorder(Rectangle.NO_BORDER);
		celdaPagina.setBorder(Rectangle.NO_BORDER);
		//	Se alínean las celdas a la Derecha.
		celdaPagina.setHorizontalAlignment(Element.ALIGN_RIGHT);
		//	Se agregan las celdas a la tabla
		tablaTitulo.addCell(celdatituloDoc);
		tablaTitulo.addCell(celdaImagen);
		tablaTitulo.addCell(celdaPagina);
		//	Se retorna la tabla del título del documento
		return tablaTitulo;
	}
	
	/**
	 * Función que se encarga de crear el fichero PDF
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
	 * Función que sirve para obtener la ruta del fichero PDF
	 * @return
	 * 		El fichero PDF
	 */
	public static File obtenerRuta() {
		/* El fichero será almacenado en un directorio dentro del directorio
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
			//	Implementación si no se encuentra la ruta.
		}
		return ruta;
	}
}
