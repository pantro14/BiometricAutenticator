package carvajal.autenticador.android.activity;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.bl.AutenticacionBL;
import carvajal.autenticador.android.bl.exception.AutenticacionBLException;
import carvajal.autenticador.android.bl.exception.CensoBLException;
import carvajal.autenticador.android.log4j.AndroidLog4jConfig;
import carvajal.autenticador.android.log4j.exception.AndroidLog4jException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.exception.UtilException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText txtUsuario;
	private EditText txtPassword;
	private Intent intent;

	/**
	 * Android Log4j
	 */
	private final Logger log4jDroid = Logger.getLogger(LoginActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		validarFechaPermitida();
		validarBD();

		// se da memoria a los objetos
		txtUsuario = (EditText) findViewById(R.id.txtUsuario);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		// se configura el componente log4J
		try {
			// se establece la escritura y ruta del archivo que contendrá el log
			// de la app.
			AndroidLog4jConfig.configure(Util.obtenerAlmacenamientoSecundario().concat(getResources().getString(R.string.carpeta_base_de_datos)), "autenticador.log");
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:onCreate:", e);
		} catch (AndroidLog4jException e) {
			// TODO Auto-generated catch block
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:onCreate:", e);
		}
	}

	/**
	 * Metodo encargado de la logica de realizar el Login con el usuario y
	 * password ingresados
	 * 
	 * @param vista
	 *            Login
	 */
	public void ingresar(View v) {

		AutenticacionBL autenBL = new AutenticacionBL(this.getApplicationContext());
		try {
			// se valida que los campos del login no estén vacios
			if ((txtUsuario.getText() != null && txtUsuario.getText().toString().equals("")) || (txtPassword.getText() != null && txtPassword.getText().toString().equals(""))) {
				AlertDialog cuadDialogo = Util.mensajeAceptar(this, R.style.TemaDialogo, getResources().getString(R.string.title_activity_login),
						getResources().getString(R.string.M14), Util.DIALOG_ERROR, null);
				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

			} else {
				// se lanza a llamar al metodo de validar usuario de la capa BL
				String rtaLogin = autenBL.validarLogin(txtUsuario.getText().toString(), txtPassword.getText().toString());
				if (rtaLogin != null) {
					intent = new Intent(this, AutenticacionActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("perfil", rtaLogin);
					startActivity(intent);
					this.finish();
				} else {
					AlertDialog cuadDialogo = Util.mensajeAceptar(this, R.style.TemaDialogo, getResources().getString(R.string.title_activity_login),
							getResources().getString(R.string.M08), Util.DIALOG_ERROR, null);
					cuadDialogo.show();

					Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

				}
			}
		} catch (AutenticacionBLException e) {
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:ingresar:", e);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:ingresar:", e);
		}
	}

	public void mostrarMensajeValidacionFecha() {
		try {
			View vistaLogin;
			vistaLogin = findViewById(R.id.pruebalogin);
			vistaLogin.setVisibility(View.INVISIBLE);
			OnDismissListener eventoCan = new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			};
			AlertDialog dialogo = Util.mensajeAceptar(this, R.style.TemaDialogo, getResources().getString(R.string.title_activity_login), getResources().getString(R.string.M01), Util.DIALOG_ERROR, null);
			dialogo.setOnDismissListener(eventoCan);
			dialogo.show();
			Util.cambiarLineaDialogos(dialogo, getApplicationContext());
		}
		catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:mostrarMensajeValidacionFecha:", e);
		}
	}
	
	/**
	 * Metodo encargado de la logica, al presionar "Validar Elector" de la
	 * vista. Basicamente se llama al mismo metodo de consultar elector, solo
	 * que validando que no esté vacío el campo.
	 * 
	 * @param v
	 *            vista actual
	 */
	public void validarFechaPermitida() {
		String validaFecha = null;
		String validaHoraDesde = null;
		String validaHoraHasta = null;
		try {
			try {
				validaFecha = Util.obtenerValidaFecha(this.getApplicationContext());
				validaHoraDesde = Util.obtenerValidaHoraDesde(this.getApplicationContext());
				validaHoraHasta = Util.obtenerValidaHoraHasta(this.getApplicationContext());
			}
			catch (Exception e) {
				e.printStackTrace();
				log4jDroid.error("AutenticadorAndroidProject:LoginActivity:validarFechaPermitida:", e);
			}
			if (validaFecha != null && !validaFecha.isEmpty() && validaHoraDesde != null && !validaHoraDesde.isEmpty() && validaHoraHasta != null && !validaHoraHasta.isEmpty()) {
				if (!Util.validarFechaPermitida(validaFecha, validaHoraDesde, validaHoraHasta)) {
					mostrarMensajeValidacionFecha();
				}
			}
			else {
				mostrarMensajeValidacionFecha();
			}
		}
		catch (UtilException e) {
			e.printStackTrace();
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:validarFechaPermitida:", e);
		}
		catch (Exception e) {
			e.printStackTrace();
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:validarFechaPermitida:", e);
		}

	}

	/**
	 * Valida que exista censo almacenado en el dispositivo, de lo contrario no
	 * permit eusar la aplicaci&oacute;n.
	 */
	public void validarBD() {
		try {
			if (!Util.verificarBD(this.getApplicationContext())) {
				View vistaLogin;
				vistaLogin = findViewById(R.id.pruebalogin);
				vistaLogin.setVisibility(View.INVISIBLE);
				// vistaLogin.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				OnDismissListener eventoCan = new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						// al hacer click en el boton del dialgo, se cierra la
						// app.
						finish();
					}
				};
				AlertDialog dialogo = Util.mensajeAceptar(this, R.style.TemaDialogo, getResources().getString(R.string.title_activity_login), getResources()
						.getString(R.string.M42), Util.DIALOG_ERROR, null);
				dialogo.setOnDismissListener(eventoCan);
				dialogo.show();
				Util.cambiarLineaDialogos(dialogo, getApplicationContext());
			}
		} catch (UtilException e) {
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:validarFechaPermitida:", e);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:LoginActivity:validarFechaPermitida:", e);
		}

	}

}
