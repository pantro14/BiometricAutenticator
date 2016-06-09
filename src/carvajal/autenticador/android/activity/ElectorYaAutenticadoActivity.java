package carvajal.autenticador.android.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import carvajal.autenticador.android.activity.dialogo.AutorizaDelegadoDialogFragment;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;

public class ElectorYaAutenticadoActivity extends FragmentActivity {

	// private POSSDKManager possdkImprimir;

	/**
	 * Objetos de la interfaz grafica.
	 */
	public Button imprimir;
	/**
	 * label de la cedula del elector
	 */
	private TextView elector_cedula;
	/**
	 * Label con la Fecha de expedición de la cedula
	 */
	private TextView elector_fechaEx;
	/**
	 * label para los nombres del elector.
	 */
	private TextView elector_nombres;
	/**
	 * label apellidos
	 */
	private TextView elector_apellidos;
	/**
	 * label de la fecha del sistema.
	 */
	private TextView lblFecha;

	/**
	 * Label para mostrar el nombre del departamento configurado en el
	 * dispositivo.
	 */
	private TextView lblDpto;
	/**
	 * Label para mostrar el nombre del municipio configurado en el dispositivo.
	 */
	private TextView lblMpio;
	/**
	 * Label para mostrar el nombre del puesto configurado en el dispositivo.
	 */
	private TextView lblPuesto;
	/**
	 * label para la fecha del registro
	 */

	private TextView labelHoraReg;
	/**
	 * Label del numero de la mesa.
	 */
	private TextView labelNumMesa;

	/**
	 * Clase para encriptar y desencriptar
	 */
	AutenticadorKeyczarCrypter crypter;

	private int sincronizacion;
	
	private static ImageView imagenElector;

	/**
	 * Estos objetos no van en la vista, se instancian para volverlos
	 * invisibles.
	 */
	// private TextView label_textViewJurado;
	// private TextView label_textViewVarJur;

	/**
	 * Android Log4j
	 */
	private final Logger log4jDroid = Logger
			.getLogger(ElectorYaAutenticadoActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_elector_ya_autenticado);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		sincronizacion = Integer.parseInt(extras.getString("sincronizacion"));
		inicilizarInterfaz();
	}

	private void inicilizarInterfaz() {
		try {
			crypter = AutenticadorKeyczarCrypter.getInstance(getResources());
			// damos memoria a los objetos de la interfaz
			elector_cedula = (TextView) findViewById(R.id.textViewVarNoDoc);
			elector_fechaEx = (TextView) findViewById(R.id.textViewVarFechExp);
			elector_nombres = (TextView) findViewById(R.id.textViewVarNombres);
			elector_apellidos = (TextView) findViewById(R.id.textViewVarApell);
			lblDpto = (TextView) findViewById(R.id.textViewVarDpto);
			lblMpio = (TextView) findViewById(R.id.textViewVarMpio);
			lblPuesto = (TextView) findViewById(R.id.textViewVarPuesto);
			lblFecha = (TextView) findViewById(R.id.lbl_fecha);
			labelHoraReg = (TextView) findViewById(R.id.textViewVarHoraReg);
			labelNumMesa = (TextView) findViewById(R.id.textViewVarNoMesa);
			imagenElector = (ImageView) findViewById(R.id.fotoElectorYaAutenticado);
			// label_textViewJurado = (TextView)
			// findViewById(R.id.textViewJurado);
			// label_textViewVarJur = (TextView)
			// findViewById(R.id.textViewVarJur);

			// se vuelven invisibles los labels de jurado
			// label_textViewJurado.setVisibility(View.GONE);
			// label_textViewVarJur.setVisibility(View.GONE);

			// lblDpto.setText(getString(R.string.nombre_depto));
			// lblMpio.setText(getString(R.string.nombre_mpio));
			// lblPuesto.setText(getString(R.string.nombre_puesto));

			Configuracion configuracionActiva = AutenticacionActivity.configuracionBL
					.obtenerConfiguracionActiva();

			lblDpto.setText(AutenticacionActivity.provinciasBL
					.obtenerProvincia(configuracionActiva.getCodProv())
					.getNomProv());
			lblMpio.setText(AutenticacionActivity.municipiosBL
					.obtenerMunicipio(configuracionActiva.getCodProv(),
							configuracionActiva.getCodMpio()).getNomMpio());
			lblPuesto.setText(AutenticacionActivity.puestosBL.obtenerPuesto(
					configuracionActiva.getCodProv(),
					configuracionActiva.getCodMpio(),
					configuracionActiva.getCodZona(),
					configuracionActiva.getCodColElec()).getNomColElec());

			if (sincronizacion == 1) {
				DateFormat format = new SimpleDateFormat(
						"EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
				Date date = format.parse(AutenticacionActivity.novedades
						.getFechaNovedad());
				labelHoraReg.setText(Util.getFechayHora(date));
				labelNumMesa.setText(Util
						.obtenerMesa(AutenticacionActivity.novedades
								.getCodMesa()));
			} else {
				labelHoraReg.setText(Util.getFechayHora(new Date(Long
						.parseLong(crypter
								.decrypt(AutenticacionActivity.novedades
										.getFechaNovedad())))));
				labelNumMesa
						.setText(Util.obtenerMesa(crypter
								.decrypt(AutenticacionActivity.novedades
										.getCodMesa())));
			}

			lblFecha.setText(Util.obtenerFecha());
			// FIN damos memoria a los objetos de la interfaz

			// se extraen los nombres y apellidos del censo
			elector_nombres.setText(AutenticacionActivity.obtenerNombre());
			elector_apellidos.setText(AutenticacionActivity.obtenerApellido());
			// FIN se extraen los nombres y apellidos

			elector_fechaEx.setText(Util
					.convertirFecha(AutenticacionActivity.censo
							.getFecExpedicion()));
			elector_cedula.setText(AutenticacionActivity.censo.getCedula());

			imprimir = (Button) findViewById(R.id.buttonImprimir);

			imprimir.setOnClickListener(clickListenerImprimirComprobanteDuplicado);
			
			
			
			Bitmap profile = BitmapFactory.decodeByteArray(Util.obtenerFotoElector(AutenticacionActivity.censo, getApplicationContext()), 0,
					Util.obtenerFotoElector(AutenticacionActivity.censo, getApplicationContext()).length);

			imagenElector.setImageBitmap(Bitmap.createScaledBitmap(profile,
					185, 221, false));

			// possdkImprimir = POSSDKManager.getInstance(this);

		} catch (ParseException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ElectorYaAutencitcadoActivity:inicilizarInterfaz:",
							e);
			elector_fechaEx.setText("");
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ElectorYaAutencitcadoActivity:inicilizarInterfaz:",
							e);
		}
	}

	public View.OnClickListener clickListenerImprimirComprobanteDuplicado = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			FragmentManager fm = getSupportFragmentManager();
			AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
					Integer.parseInt(getString(R.string.ya_autenticado)), true,
					null, null);
			dgAutorizaDelegado.show(fm, "Dialog Fragment");
		}
	};

	@Override
	public void onBackPressed() {
		AutenticacionActivity.removerDatos();
		super.onBackPressed();
	}

}