package carvajal.autenticador.android.activity;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import carvajal.autenticador.android.activity.asynctask.GenerarBackupTask;
import carvajal.autenticador.android.framework.morphosmart.ConexionHuellero;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;
import carvajal.autenticador.android.util.Util;

/**
 * Menu de configuracion.
 * 
 * @author grasotos
 * 
 */
public class ConfiguracionActivity extends Activity {

	/**
	 * ProgressBar de carga
	 */
	ProgressBar progressBar;
	/**
	 * Boton de configuración de estación.
	 */
	LinearLayout layoutBtnConfiguracion;

	/**
	 * Actividad actual
	 */
	Activity actividad = this;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(ConfiguracionActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_configuracion);
		
		((TextView)findViewById(R.id.lbl_fecha_conf)).setText(Util.obtenerFecha());

		layoutBtnConfiguracion = (LinearLayout) findViewById(R.id.layoutBtnConfiguracion);
		String perfil = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			perfil = extras.getString("perfil");
			if (perfil != null && perfil.length() > 0 && perfil.equalsIgnoreCase(getString(R.string.rol_operario))) {
				layoutBtnConfiguracion.setVisibility(View.GONE);
			}
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBarBackup);

	}

	/**
	 * Instacia y muestra el DialogFragment encargado de generar el backup y
	 * mostrar el progreso mediante una barra de progreso.
	 * 
	 * @param v
	 *            View
	 */
	public void generarBackup(View v) {
		AlertDialog cuadDialogo = Util.mensajeConfirmacion(ConfiguracionActivity.this, R.style.TemaDialogo, getString(R.string.titulo_M30), getString(R.string.M30), Util.DIALOG_ADVERTENCIA,
				clickListenerIniciarBackup, null);
		cuadDialogo.show();

		Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());
	}

	/**
	 * Permite iniciar el diagnostico de la aplicación.
	 * 
	 * @param view
	 */
	public void iniciarDiagnostico(View view) {
		try {
			if (!ProcessInfo.getInstance().isStarted()) {
				

				if (!AutenticacionActivity.huelleroConectado) {
					try {
						AutenticacionActivity.huelleroConectado = ConexionHuellero.conectarHuellero(AutenticacionActivity.contexto);
					} catch (ConexionHuelleroException e) {
						log4jDroid.error("AutenticadorAndroidProject:ConfiguracionActivity:iniciarDiagnostico:", e);
					}
				}

			}

			AlertDialog alertDialog = Util.mensajeConfirmacion(ConfiguracionActivity.this, R.style.TemaDialogo, getString(R.string.titulo_M30), getString(R.string.M30), Util.DIALOG_ADVERTENCIA,
					clickListenerIniciarDiagnostico, null);
			alertDialog.show();
			Util.cambiarLineaDialogos(alertDialog, getApplicationContext());

		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:ConfiguracionActivity:iniciarDiagnostico:", e);
		}
	}

	/**
	 * Permite llamar al inicio del diagnostico de la actividad
	 * DiagnosticoActivity
	 * 
	 * @author grasotos
	 */
	public OnClickListener clickListenerIniciarDiagnostico = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			Intent intent = new Intent(ConfiguracionActivity.this, DiagnosticoActivity.class);
			startActivity(intent);

		}
	};

	/**
	 * 
	 */
	public OnClickListener clickListenerIniciarBackup = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {

				GenerarBackupTask task = new GenerarBackupTask();
				LayoutInflater inflater = getLayoutInflater();
				final View v = inflater.inflate(R.layout.dialogo_generar_backup, null);
				task.setProgressBar(progressBar, actividad, v);
				task.execute();
			} catch (Exception e) {
				log4jDroid.error("AutenticadorAndroidProject:ConfiguracionActivity:generarBackup:", e);
			}

		}
	};

	/**
	 * Lanza el activity correspondiente a configuraci&oacute;n de la
	 * estaci&oacute;n
	 * 
	 * @param v
	 */
	public void configurarEstacion(View v) {
		Intent intent = new Intent(this, ConfiguracionFormActivity.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		AutenticacionActivity.removerDatos();
		super.onBackPressed();
	}

}
