package carvajal.autenticador.android.activity;

import org.apache.log4j.Logger;

import carvajal.autenticador.android.activity.dialogo.AutorizaDelegadoDialogFragment;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.ProvinciasBL;
import carvajal.autenticador.android.bl.MunicipiosBL;
import carvajal.autenticador.android.bl.NovedadesBL;
import carvajal.autenticador.android.bl.ColegiosElectoralesBL;
import carvajal.autenticador.android.bl.exception.ConfiguracionBLException;
import carvajal.autenticador.android.bl.exception.ProvinciasBLException;
import carvajal.autenticador.android.bl.exception.MunicipiosBLException;
import carvajal.autenticador.android.bl.exception.ColegiosElectoralesBLException;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Esta clase se utiliza para gestionar la vista del Reporte de autenticaciones
 * 
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since 15 de abril de 2015
 *
 */
public class ReporteActivity extends FragmentActivity {
	/**
	 * Objetos tipo TextView y EditText de esta interfaz.
	 */
	private TextView lblFecha;
	/**
	 * Label para mostrar el nombre del departamento configurado en el
	 * dispositivo.
	 */
	private TextView lblElectoresAutenticados;
	/**
	 * Label para mostrar el nombre del departamento configurado en el
	 * dispositivo.
	 */
	private TextView lblProv;
	/**
	 * Label para mostrar el nombre del municipio configurado en el dispositivo.
	 */
	private TextView lblMpio;
	/**
	 * Label para mostrar el nombre del colegio configurado en el dispositivo.
	 */
	private TextView lblColegio;
	/**
	 * Label para mostrar el nombre del texto label de la estación.
	 */
	private TextView textolblMesa;
	/**
	 * Label para mostrar el nombre de la estación configurado en el
	 * dispositivo.
	 */
	private TextView lblMesa;
	
	public static String provincia;
	public static String municipio;
	public static String nombreColegioElectoral;
	public static String mesa;
	public static String zona;

	public String codigoColegio = null;

	public static String colegio = null;

	public static String colegioDivipol;

	/**
	 * ConfiguracionBL para acceder a los métodos de la tabla Configuracion
	 */
	public ConfiguracionBL configuracionBL;

	/**
	 * Generacion de Logs en android
	 */
	private final Logger log4jDroid = Logger
			.getLogger(NovedadAutenticacionActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reporte);
		try {
			mostrarInfoReporte();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Carga la información del reporte que se va a mostrar en la interfaz
	 * gráfica del ReporteActivity
	 * 
	 * @throws Exception
	 */
	public void mostrarInfoReporte() throws Exception {
		try {
			colegioDivipol = Util.obtenerDivipolString(ReporteActivity.this);
			// Innstancia de los labels
			lblFecha = (TextView) findViewById(R.id.lbl_fecha);
			lblFecha.setText(Util.obtenerFecha());
			lblElectoresAutenticados = (TextView) findViewById(R.id.textViewTitulo);
			lblProv = (TextView) findViewById(R.id.textViewVarDpto);
			lblMpio = (TextView) findViewById(R.id.textViewVarMpio);
			lblColegio = (TextView) findViewById(R.id.textViewVarPuesto);
			textolblMesa = (TextView) findViewById(R.id.textViewNoMesa);
			lblMesa = (TextView) findViewById(R.id.textViewVarNoMesa);
			// Se oculta el campo de Número de Mesa, que para esta activity es
			// Innecesaria
			((TextView) findViewById(R.id.textViewVarHoraReg))
					.setVisibility(View.GONE);
			((TextView) findViewById(R.id.textViewHoraReg))
					.setVisibility(View.GONE);
			textolblMesa.setText("Mesa:");
			if (validarConfiguracion()) {
				/*
				 * Aquí se hace la consulta en la tabla configuración Los
				 * resultados se asignan a cada uno de los labels de la activity
				 */
				NovedadesBL novedades = new NovedadesBL(
						ReporteActivity.this.getApplicationContext());
				Configuracion configuracion = new Configuracion();
				ConfiguracionBL configuracionBL = new ConfiguracionBL(
						ReporteActivity.this.getApplicationContext());
				ProvinciasBL departamentosBL = new ProvinciasBL(
						ReporteActivity.this.getApplicationContext());
				MunicipiosBL municipiosBL = new MunicipiosBL(
						ReporteActivity.this.getApplicationContext());
				ColegiosElectoralesBL puestosBL = new ColegiosElectoralesBL(
						ReporteActivity.this.getApplicationContext());
				configuracion = configuracionBL.obtenerConfiguracionActiva();

				// Finalmente se asignan cada una de las consultas en los labels
				lblElectoresAutenticados.setText(String.valueOf(novedades
						.obtenerConteoElectoresAutenticados())
						+ " Electores Autenticados en:");
				// Se obtienen los datos de la configuración activa
				codigoColegio = configuracion.getCodColElec();
				colegio = puestosBL.obtenerPuesto(configuracion.getCodProv(),
						configuracion.getCodMpio(), configuracion.getCodZona(),
						configuracion.getCodColElec()).getNomColElec();
				zona = puestosBL.obtenerZona(configuracion.getCodProv(),
						configuracion.getCodMpio(), colegio);
				lblProv.setText((departamentosBL.obtenerProvincia(configuracion
						.getCodProv())).getNomProv());
				lblMpio.setText(municipiosBL.obtenerMunicipio(
						configuracion.getCodProv(), configuracion.getCodMpio())
						.getNomMpio());
				lblColegio.setText(colegio);
				lblMesa.setText(String.valueOf(configuracion.getCodMesa()));
				colegioDivipol = colegioDivipol + configuracion.getCodMesa();
			} else {
				lblElectoresAutenticados
						.setText("0 Electores Autenticados en:");
				lblProv.setText("");
				lblMpio.setText("");
				lblColegio.setText("");
				lblMesa.setText("");
			}
			establecerDatosReporte();
		} catch (ConfiguracionBLException e) { // Exepción inesperada de tipo
												// ConfiguracionBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ReporteActivity:mostrarInfoReporte:",
							e);
		} catch (ProvinciasBLException e) { // Exepción inesperada de tipo
											// DepartamentosBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ReporteActivity:mostrarInfoReporte:",
							e);
		} catch (MunicipiosBLException e) { // Exepción inesperada de tipo
											// MunicipiosBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ReporteActivity:mostrarInfoReporte:",
							e);
		} catch (ColegiosElectoralesBLException e) { // Exepción inesperada de
														// tipo
														// PuestosBLException
			log4jDroid
					.error("AutenticadorAndroidProject:ReporteActivity:mostrarInfoReporte:",
							e);
		} catch (Exception e) { // Exepción inesperada genérica.
			log4jDroid
					.error("AutenticadorAndroidProject:ReporteActivity:mostrarInfoReporte:",
							e);
		}

	}

	private void establecerDatosReporte() {
		provincia = lblProv.getText().toString();
		municipio = lblMpio.getText().toString();
		nombreColegioElectoral = lblColegio.getText().toString();
		mesa = lblMesa.getText().toString();
	}

	/**
	 * Método del botón de impresión del reporte
	 * 
	 * @param view
	 */
	public void btnImprimirReporte_Click(View view) {
		// Al dar click en el botón imprimir, se abre el dialogo de Autorización
		// de delegado
		FragmentManager fm = getSupportFragmentManager();
		AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
				Integer.parseInt(getString(R.string.reporte_autenticacion_delegado)),
				true, null, null);
		dgAutorizaDelegado.show(fm, "Dialog Fragment");

	}

	public void btnImprimirReporteAutenticados_Click(View view) {
		// Al dar click en el botón imprimir, se abre el dialogo de Autorización
		// de delegado
		FragmentManager fm = getSupportFragmentManager();
		AutorizaDelegadoDialogFragment dgAutorizaDelegado = new AutorizaDelegadoDialogFragment(
				Integer.parseInt(getString(R.string.reporte_total_autenticacion)),
				true, null, null);
		dgAutorizaDelegado.show(fm, "Dialog Fragment");
		/*TestImpresoraAsyncTask task = new TestImpresoraAsyncTask(ReporteActivity.this);
		task.execute();*/

	}

	private boolean validarConfiguracion() {
		try {
			boolean res = true;
			configuracionBL = new ConfiguracionBL(
					ReporteActivity.this.getApplicationContext());
			if (!Util.existeConfiguracion(configuracionBL)) {
				res = false;
			}
			return res;
		} catch (AutenticadorDaoMasterSourceException e) {
			log4jDroid
					.error("AutenticadorAndroidProject:AutorizaDelegadoDialogFragment:validarConfiguracion:",
							e);
			return false;
		}
	}

}
