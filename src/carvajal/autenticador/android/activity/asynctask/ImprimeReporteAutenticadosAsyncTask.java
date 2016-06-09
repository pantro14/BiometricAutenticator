package carvajal.autenticador.android.activity.asynctask;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.framework.possdk.PDFReporteAutenticacion;
import carvajal.autenticador.android.framework.possdk.exception.PDFReporteAutenticacionException;
import carvajal.autenticador.android.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

public class ImprimeReporteAutenticadosAsyncTask extends AsyncTask<Void, Void, String> {
	Activity actividad;
	ArrayList<String> listaCedulas;
	
	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	public ImprimeReporteAutenticadosAsyncTask(ArrayList<String> listaCedulas, Activity actividad) {
		this.listaCedulas = listaCedulas;
		this.actividad = actividad;
	}

	@Override
	protected String doInBackground(Void... param) {
		String respuestaServicio = "-1"; 
			//	Título del documento PDF
			String tituloDocumento = actividad.getString(R.string.label_titulo_documento);
			//	Título del nombre de la elección.
			String tituloElecciones = actividad.getString(R.string.label_titulo_eleccion);
			//	Título de la fecha de elecciones.
			String tituloFecha = actividad.getString(R.string.label_fecha_eleccion);
			try{
				//	Creación de instancia de tipo PDFReporteAutenticacion
				PDFReporteAutenticacion pdfReporte = 
						new PDFReporteAutenticacion(actividad, tituloDocumento, tituloElecciones, tituloFecha, listaCedulas);
				//	Se genera el reporte
				if(pdfReporte.generarPDF()){// Si el reporte se genera correctamente
					//	Se abre el PDF
					//pdfReporte.abrirPDF();
					respuestaServicio = pdfReporte.ServicioImprimirReporteSync();
				}
			}catch(PDFReporteAutenticacionException e){
				//	Exepción no controlada de tipo PDFReporteAutenticacionException
				log4jDroid
				.error("AutenticadorAndroidProject:ImprimeReporteAutenticadosAsyncTask:doInBackground:",
						e);
			}
		return respuestaServicio;
	}

	@Override
	protected void onPostExecute(String resultado) {
		if(resultado.equals("-1")){
			AlertDialog cuadDialogo = Util.mensajeAceptar(actividad,
					R.style.TemaDialogo,
					actividad.getResources()
					.getString(R.string.title_activity_login),
					actividad.getResources()
					.getString(R.string.M31), Util.DIALOG_ERROR, null);
			cuadDialogo.show();
			Util.cambiarLineaDialogos(cuadDialogo, actividad);
		}
	}	

}
