package carvajal.autenticador.android.activity.asynctask;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.AutenticacionActivity;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.ColegiosElectoralesBL;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectorales;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.util.BackupUtil;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.ZipUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GenerarBackupTask extends AsyncTask<Void, Integer, Void> {

	private final int TAMANO_NOMBRE_ARCHIVO = 200;
	private final int PASOS = 4;
	private int backups = 0;
	private int pasoActual = 0;
	private int backupActual = 0;
	public boolean existeUSB = false;

	ProgressBar progressBar;
	TextView txtProgreso;
	TextView txtProgresoBackup;
	TextView txtMensaje;

	Activity actividad;
	View v;
	AlertDialog.Builder builder;

	// Declaración del dialogo que se retorna en el método
	AlertDialog dialog = null;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	public void setProgressBar(ProgressBar progressBar, Activity actividad,
			View v) {
		this.progressBar = progressBar;
		this.actividad = actividad;
		this.v = v;
		// Construcción del diálogo.
		builder = new AlertDialog.Builder(actividad);

		// Se asigna el view al dialog y se bloquea el uso del botón back
		builder.setView(v).setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});

		// Título del dialogo
		builder.setTitle(R.string.titulo_dialogo_backup);
		// Creación del diálogo a través del builder
		dialog = builder.create();
		// Se evita que se cierre el diálogo dando tap atrás de este mismo.
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();

	}

	@Override
	protected Void doInBackground(Void... params) {

		progressBar = (ProgressBar) v.findViewById(R.id.progressBarBackup);
		txtProgreso = (TextView) v.findViewById(R.id.txtProgreso);
		txtProgreso.setText(pasoActual + "/" + PASOS);
		txtProgresoBackup = (TextView) v.findViewById(R.id.txtProgresoBackups);
		txtMensaje = (TextView) v.findViewById(R.id.txtMensajeGenerarBackup);

		try {
			// Inicialización de variables
			ConfiguracionBL configuracionBL = new ConfiguracionBL(
					actividad.getApplicationContext());
			ColegiosElectoralesBL puestosBL = new ColegiosElectoralesBL(
					actividad.getApplicationContext());

			String nombreArchivoResultante = "";

			String carpeta_bd = Util.obtenerAlmacenamientoSecundario().concat(
					actividad.getApplicationContext().getString(
							R.string.carpeta_base_de_datos));

			String carpetaTemporal = Util.obtenerAlmacenamientoSecundario()
					.concat(actividad.getApplicationContext().getString(
							R.string.carpeta_temporal_backup));

			String carpetaBackup = Util.obtenerAlmacenamientoSecundario()
					.concat(actividad.getApplicationContext().getString(
							R.string.carpeta_backup));

			String carpetaUSB = actividad.getApplicationContext().getString(
					R.string.ruta_usb);

			// Se obtiene la configuración activa
			// Configuracion configuracionActiva = configuracionBL
			// .obtenerConfiguracionActiva();

			List<Configuracion> configuraciones = configuracionBL
					.obtenerConfiguraciones();
			if (configuraciones != null && configuraciones.size() > 0) {

				backups = configuraciones.size();

				for (Configuracion configuracionActual : configuraciones) {

					ZipUtil zipUtil = new ZipUtil(actividad);

					backupActual++;
					pasoActual = 0;
					publishProgress(0, pasoActual);

					// Se crea un colegio con los parámetros de la
					// configuración
					// activa
					ColegiosElectorales colegio = puestosBL.obtenerPuesto(
							configuracionActual.getCodProv(),
							configuracionActual.getCodMpio(),
							configuracionActual.getCodZona(),
							configuracionActual.getCodColElec());

					// Se obtiene la fecha del sistema
					Calendar calendar = Calendar.getInstance();
					String año = String.valueOf(calendar.get(Calendar.YEAR));
					año = año.substring(año.length() - 2);
					String mes = String
							.valueOf(calendar.get(Calendar.MONTH) + 1);
					String dia = String.valueOf(calendar.get(Calendar.DATE));
					String hora = String.valueOf(calendar
							.get(Calendar.HOUR_OF_DAY));
					String min = String.valueOf(calendar.get(Calendar.MINUTE));
					String sec = String.valueOf(calendar.get(Calendar.SECOND));

					// Se asegura que los campos de la fecha sean siempre de
					// dos
					// (2) dígitos, excepto el año;
					if (mes.length() < 2) {
						mes = "0".concat(mes);
					}
					if (dia.length() < 2) {
						dia = "0".concat(dia);
					}
					if (hora.length() < 2) {
						hora = "0".concat(hora);
					}
					if (min.length() < 2) {
						min = "0".concat(min);
					}
					if (sec.length() < 2) {
						sec = "0".concat(sec);
					}

					// Se crea el nombre del archivo según lo especifica el
					// Documento de Especificación de Requisitos
					nombreArchivoResultante = configuracionActual
							.getCodProv()
							.concat(configuracionActual.getCodMpio())
							.concat(configuracionActual.getCodZona())
							.concat(configuracionActual.getCodColElec())
							.concat("-")
							.concat(String.valueOf(configuracionActual
									.getCodMesa()))
							.concat("-")
							.concat(colegio.getNomColElec().replaceAll("\\-",
									"")).concat("-").concat(año).concat(mes)
							.concat(dia).concat(hora).concat(min).concat(sec);

					// Se valida que el nombre del archivo no supere los 200
					// caracteres, de ser así se trunca el nombre del colegio
					// según el Documento de Especificación de Requisitos
					if (nombreArchivoResultante.length() > TAMANO_NOMBRE_ARCHIVO) {
						// Si el tamaño del nombre del archivo supera el
						// maximo
						// permitido

						int tamanoNombre = nombreArchivoResultante.length();
						int diferencia = tamanoNombre - TAMANO_NOMBRE_ARCHIVO;
						int tamanoNomPuesto = colegio.getNomColElec()
								.replaceAll("\\-", "").length();
						String nomPuesto = colegio.getNomColElec()
								.replaceAll("\\-", "")
								.substring(0, tamanoNomPuesto - diferencia);

						nombreArchivoResultante = configuracionActual
								.getCodProv()
								.concat(configuracionActual.getCodMpio())
								.concat(configuracionActual.getCodZona())
								.concat(configuracionActual.getCodColElec())
								.concat("-")
								.concat(String.valueOf(configuracionActual
										.getCodMesa())).concat("-")
								.concat(nomPuesto).concat("-")
								.concat(String.valueOf(año)).concat(mes)
								.concat(dia).concat(hora).concat(min)
								.concat(sec);
					}

					// se eliminan caracteres especiales del nombre del archivo
					nombreArchivoResultante = nombreArchivoResultante
							.replaceAll("[\\\\\\/\\?\\:\\*\\\"\\<\\>\\|]", " ");

					String nombreArchivoSplit[] = nombreArchivoResultante
							.split("-");
					String nombreArchivoSinFecha = nombreArchivoSplit[0]
							.concat("-").concat(nombreArchivoSplit[1])
							.concat("-").concat(nombreArchivoSplit[2]);

					// Elimina la carpeta temporal para asegurar que los
					// archivos a comprimir son los correctos
					Util.eliminarDirectorio(new File(carpetaTemporal));

					// Se hace copia de la base de datos y se mueve a una
					// carpeta temporal
					BackupUtil.copyFile(carpeta_bd,
							configuracionActual.getNombreBD(), carpetaTemporal,
							nombreArchivoResultante.concat(".db"));
					publishProgress(25, ++pasoActual);

					// Se hace copia de la base de datos y se mueve a una
					// carpeta temporal
					BackupUtil.copyFile(
							carpeta_bd,
							actividad.getApplicationContext().getString(
									R.string.nombre_archivo_log),
							carpetaTemporal,
							nombreArchivoResultante.concat(".log"));
					publishProgress(50, ++pasoActual);

					// Se elimina un backup anterior (si existe)
					Util.eliminarArchivo(nombreArchivoSinFecha, new File(
							carpetaBackup));
					// Se comprime la carpeta con los archivos del backup
					zipUtil.comprimirDirectorio(nombreArchivoResultante
							.concat(".sqlt"));
					publishProgress(75, ++pasoActual);

					// Elimina la carpeta temporal
					Util.eliminarDirectorio(new File(carpetaTemporal));
					// valida que exista usb conectada en formato FAT32
					File usb = new File(carpetaUSB);
					if (usb.exists() && usb.isDirectory() && usb.canWrite()) {

						// Se elimina un backup anterior (si existe)
						Util.eliminarArchivo(nombreArchivoSinFecha, usb);

						// copia el backup a la usb
						BackupUtil.copyFile(carpetaBackup,
								nombreArchivoResultante.concat(".sqlt"),
								carpetaUSB,
								nombreArchivoResultante.concat(".sqlt"));
						existeUSB = true;
					}
					publishProgress(100, ++pasoActual);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log4jDroid
					.error("AutenticadorAndroidProject:GenerarBackupDialogFragment:ejecutarThread:",
							e);
		}

		// Se cierra el dialogo después de terminar
		dialog.dismiss();
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (!existeUSB) {
			AlertDialog cuadDialogo = Util.mensajeAceptar(actividad,
					R.style.TemaDialogo, actividad.getApplicationContext()
							.getString(R.string.titulo_M30), actividad
							.getApplicationContext().getString(R.string.M43),
					Util.DIALOG_ADVERTENCIA, null);

			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, actividad);

		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (this.progressBar != null) {
			progressBar.setProgress(values[0]);
			txtProgreso.setText(values[1] + "/" + PASOS);
			txtProgresoBackup.setText(backupActual + "/" + backups);
		}
	}

}
