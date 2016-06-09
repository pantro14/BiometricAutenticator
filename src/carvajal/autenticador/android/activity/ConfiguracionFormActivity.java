package carvajal.autenticador.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import carvajal.autenticador.android.adapter.ColegioElectoralSpinnerAdapter;
import carvajal.autenticador.android.adapter.MesaSpinnerAdapter;
import carvajal.autenticador.android.adapter.MunicipioSpinnerAdapter;
import carvajal.autenticador.android.adapter.ProvinciaSpinnerAdapter;
import carvajal.autenticador.android.adapter.ServidorSpinnerAdapter;
import carvajal.autenticador.android.bl.ColegiosElectoralesBL;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.bl.MesasBL;
import carvajal.autenticador.android.bl.MunicipiosBL;
import carvajal.autenticador.android.bl.ProvinciasBL;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectorales;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.read.Mesas;
import carvajal.autenticador.android.dal.greendao.read.Municipios;
import carvajal.autenticador.android.dal.greendao.read.Provincias;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.file.AutenticadorFileReader;

public class ConfiguracionFormActivity extends Activity {

	/**
	 * Variable global de la clase para hacer referencia a la misma
	 */
	private final Activity actividad = this;

	/**
	 * Spinners de las variables de configuracion
	 */
	private Spinner spinnerProv;
	private Spinner spinnerMpio;
	private Spinner spinnerColegio;
	private Spinner spinnerMesa;
	private Spinner spinnerServidor;

	/**
	 * Campos de texto para la configuración actual
	 */
	private TextView txtProv;
	private TextView txtMpio;
	private TextView txtColegio;
	private TextView txtMesa;
	private TextView txtServidor;

	/**
	 * Objeto tipo TextView para la fecha.
	 */
	private TextView lblFecha;

	/**
	 * Clases BL
	 */
	private ConfiguracionBL configuracionBL;
	private ProvinciasBL provinciasBL;
	private MunicipiosBL municipiosBL;
	private ColegiosElectoralesBL colegiosBL;
	private MesasBL mesasBL;

	/**
	 * Clases para manejos de entidades
	 */
	private Configuracion configuracionActiva;
	private Configuracion configuracionNueva;

	/**
	 * Variables para almacenar los datos seleccionados
	 */
	private long provSeleccionada;
	private long mpioSeleccionado;
	private long colegioSeleccionado;
	private String nomColegioSeleccionado;
	private String mesaSeleccionada;
	private int numeroServidorSeleccionado;
	private String ipServidorSeleccionado = "0";

	/**
	 * Variable para validar si se guardó la configuración
	 */
	private boolean guardado = false;

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger
			.getLogger(AutenticacionActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_configuracion_form);
	}

	@Override
	protected void onStart() {
		super.onStart();
		inicializarInterfaz();
	}

	/**
	 * Se inicializan variables relacionadas al layout y se asignan valores a
	 * las mismas. También se inicializan los objetos BL.
	 */
	private void inicializarInterfaz() {

		lblFecha = (TextView) findViewById(R.id.lbl_fechaConfiguracion);
		lblFecha.setText(Util.obtenerFecha());
		spinnerProv = (Spinner) findViewById(R.id.spinnerVarDepto);
		spinnerMpio = (Spinner) findViewById(R.id.spinnerVarMpio);
		spinnerColegio = (Spinner) findViewById(R.id.spinnerVarPuesto);
		spinnerMesa = (Spinner) findViewById(R.id.spinnerVarEstacion);
		spinnerServidor = (Spinner) findViewById(R.id.spinnerVarServidor);

		txtProv = (TextView) findViewById(R.id.textViewVarDeptoConf);
		txtMpio = (TextView) findViewById(R.id.textViewVarMpioConf);
		txtColegio = (TextView) findViewById(R.id.textViewVarPuestoConf);
		txtMesa = (TextView) findViewById(R.id.textViewVarEstacionConf);
		txtServidor = (TextView) findViewById(R.id.textViewVarServidorConf);

		try {
			/**
			 * FileReader - IP Servers
			 */
			String rutaAlmacenamiento = Util.obtenerAlmacenamientoSecundario().concat(getString(R.string.carpeta_archivo_ip_servidores));
			Util.listLinesServers = AutenticadorFileReader.readFile(rutaAlmacenamiento, getString(R.string.nombre_archivo_ip_servidores));
			
			configuracionBL = new ConfiguracionBL(getApplicationContext());
			municipiosBL = new MunicipiosBL(this.getApplicationContext());
			provinciasBL = new ProvinciasBL(this.getApplicationContext());
			colegiosBL = new ColegiosElectoralesBL(this.getApplicationContext());
			mesasBL = new MesasBL(this.getApplicationContext());

			mostrarConfiguracionActual();
			cargarProvincias();
			cargarServidores();
		}
		catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:inicializarInterfaz:",
							e);
		}

	}

	/**
	 * Obtiene la lista de todos los departamentos almacenados en la abse de
	 * datos y la muestra en el spinner
	 */
	private void cargarProvincias() {

		try {
			ArrayList<Provincias> listaProvincias = provinciasBL
					.obtenerProvincias();

			spinnerProv.setAdapter(new ProvinciaSpinnerAdapter(this,
					listaProvincias));

			// Se crea un listener para obtener el departamento seleccionado y
			// los municipios del departamento cada vez que se cambie en el
			// spinner.
			spinnerProv
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onNothingSelected(AdapterView<?> parent) {
						}

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							provSeleccionada = parent
									.getItemIdAtPosition(position);

							cargarMunicipios();
						}
					});
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:cargarProvincias:",
							e);
		}
	}

	/**
	 * Obtiene la lista de municipios correspondientes al departamento
	 * seleccionado y la muestra en el spinner
	 * 
	 * @param nombreDpto
	 */
	private void cargarMunicipios() {
		try {

			ArrayList<Municipios> listaMunicipios = municipiosBL
					.obtenerMunicipios(String.valueOf(provSeleccionada));

			spinnerMpio.setAdapter(new MunicipioSpinnerAdapter(this,
					listaMunicipios));

			// Se crea un listener para obtener el municipio seleccionado y los
			// puestos del municipio cada vez que se cambie en el spinner.
			spinnerMpio
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onNothingSelected(AdapterView<?> parent) {
						}

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							mpioSeleccionado = parent
									.getItemIdAtPosition(position);

							cargarColegios();
						}
					});

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:cargarMunicipios:",
							e);
		}
	}

	/**
	 * Obtiene los puestos en base al departamento y municipio selecionados
	 */
	private void cargarColegios() {
		try {

			ArrayList<ColegiosElectorales> listaColegios = colegiosBL
					.obtenerPuestos(String.valueOf(provSeleccionada),
							String.valueOf(mpioSeleccionado));

			spinnerColegio.setAdapter(new ColegioElectoralSpinnerAdapter(this,
					listaColegios));

			// Se crea un listener para obtener el valor del puesto seleccionado
			// cada vez que se cambia en el spinner.
			spinnerColegio
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onNothingSelected(AdapterView<?> parent) {
						}

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							colegioSeleccionado = parent
									.getItemIdAtPosition(position);

							nomColegioSeleccionado = ((ColegiosElectorales) parent
									.getItemAtPosition(position))
									.getNomColElec();
							
							cargarMesas();

						}
					});

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:cargarColegios:",
							e);
		}
	}

	/**
	 * Obtiene las mesas configuradas a partir de la provincia, municipio y colegio electoral seleccionado
	 */
	private void cargarMesas() {
		try {
			String codZona = colegiosBL.obtenerZona(String.valueOf(provSeleccionada), String.valueOf(mpioSeleccionado), String.valueOf(nomColegioSeleccionado));
			
			Log.d("ConfiguracionFormActivity:", "cargarMesas:provSeleccionada: " + provSeleccionada);
			Log.d("ConfiguracionFormActivity:", "cargarMesas:mpioSeleccionado: " + mpioSeleccionado);
			Log.d("ConfiguracionFormActivity:", "cargarMesas:colegioSeleccionado: " + colegioSeleccionado);
			Log.d("ConfiguracionFormActivity:", "cargarMesas:nomColegioSeleccionado: " + nomColegioSeleccionado);
			Log.d("ConfiguracionFormActivity:", "cargarMesas:codZona: " + codZona);
			
			ArrayList<Mesas> listaMesas = mesasBL.obtenerMesas(String.valueOf(provSeleccionada), String.valueOf(mpioSeleccionado), codZona, String.valueOf(colegioSeleccionado));
			
			Log.d("ConfiguracionFormActivity:", "cargarMesas:listaMesas:size: " + listaMesas.size());

			spinnerMesa.setAdapter(new MesaSpinnerAdapter(this,
					listaMesas));

			// Se crea un listener para obtener el valor de la estacion
			// seleccionada cada vez que se cambia en el spinner.
			spinnerMesa
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onNothingSelected(AdapterView<?> parent) {
						}

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							mesaSeleccionada = ((Mesas) parent.getItemAtPosition(position)).getCodMesa();

						}
					});
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:cargarMesas:",
							e);
		}
	}

	/**
	 * Obtiene la lista de los servidores configurados
	 */
	private void cargarServidores() {
		try {
			ArrayList<String> listaServidores = new ArrayList<String>();
			listaServidores.add(getString(R.string.nombre_servidor_0));
			if (Util.listLinesServers != null && !Util.listLinesServers.isEmpty()) {
				if (Util.validarDireccionIPPrivada(Util.listLinesServers.get(0))) {
					listaServidores.add(getString(R.string.nombre_servidor_1));
				}
				if (Util.validarDireccionIPPrivada(Util.listLinesServers.get(1))) {
					listaServidores.add(getString(R.string.nombre_servidor_2));
				}
				if (Util.validarDireccionIPPrivada(Util.listLinesServers.get(2))) {
					listaServidores.add(getString(R.string.nombre_servidor_3));
				}
			}
			spinnerServidor.setAdapter(new ServidorSpinnerAdapter(this,
					listaServidores));

			// se crea un listener para obtener el numero del servidor
			// seleccionado cada vez que se cambia en el spinner.
			spinnerServidor
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onNothingSelected(AdapterView<?> parent) {
						}

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							numeroServidorSeleccionado = (int) parent
									.getItemIdAtPosition(position);

						}
					});
		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:cargarServidores:",
							e);
		}
	}

	/**
	 * Obtiene los datos de configuracion activa y los muestra en pantalla
	 */
	private void mostrarConfiguracionActual() {
		try {
			configuracionActiva = configuracionBL.obtenerConfiguracionActiva();

			if (configuracionActiva != null) {
				String provConf = configuracionActiva.getCodProv();
				String mpioConf = configuracionActiva.getCodMpio();
				String zonaConf = configuracionActiva.getCodZona();
				String puestoConf = configuracionActiva.getCodColElec();
				txtProv.setText(provinciasBL.obtenerProvincia(provConf)
						.getNomProv());
				txtMpio.setText(municipiosBL.obtenerMunicipio(provConf,
						mpioConf).getNomMpio());
				txtColegio.setText(colegiosBL.obtenerPuesto(provConf, mpioConf,
						zonaConf, puestoConf).getNomColElec());
				txtMesa.setText(String.valueOf(configuracionActiva.getCodMesa()));
				txtServidor.setText(Util.obtenerNombreServidor(
						getApplicationContext(),
						configuracionActiva.getIPServidor()));
			} else {
				txtProv.setText("N/A");
				txtMpio.setText("N/A");
				txtColegio.setText("N/A");
				txtMesa.setText("N/A");
				txtServidor.setText("N/A");
			}

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:mostrarConfiguracionActual:",
							e);
		}
	}

	/**
	 * Verifica que no se haya excedido el m&aacute;ximo de configuraciones
	 * permitidas, y recupera los datos necesarios para guardar la nueva
	 * configuraci&oacute;n.
	 * 
	 * @param v
	 */
	public void guardarConfiguracion(View v) {
		try {
			String urlServidor = getString(R.string.direccion_servidor_n_mask);
			String codZona = colegiosBL.obtenerZona(
					String.valueOf(provSeleccionada),
					String.valueOf(mpioSeleccionado),
					String.valueOf(nomColegioSeleccionado));

			ColegiosElectorales puestoNuevo = colegiosBL.obtenerPuesto(
					String.valueOf(provSeleccionada),
					String.valueOf(mpioSeleccionado), codZona,
					String.valueOf(colegioSeleccionado));

			String nombreBD = getString(R.string.prefijo_db)
					.concat(String.valueOf(configuracionBL
							.getConfiguracionCount() + 1));
			/**
			 * listLinesServers Read
			 */
			if (Util.listLinesServers != null && !Util.listLinesServers.isEmpty()) {
				switch (numeroServidorSeleccionado) {
				case 1:
					ipServidorSeleccionado = Util.listLinesServers.get(0);
					break;
				case 2:
					ipServidorSeleccionado = Util.listLinesServers.get(1);
					break;
				case 3:
					ipServidorSeleccionado = Util.listLinesServers.get(2);
					break;
				}
			}
			
			Log.d("ConfiguracionFormActivity:", "guardarConfiguracion:ipServidorSeleccionado: " + ipServidorSeleccionado);
			urlServidor = urlServidor.replace("dir_ip", ipServidorSeleccionado);
			Log.d("ConfiguracionFormActivity:", "guardarConfiguracion:urlServidor: " + urlServidor);
			
			configuracionNueva = new Configuracion(null,
					Util.ponerCerosIzquierda(2,
							String.valueOf(provSeleccionada)),
					Util.ponerCerosIzquierda(3,
							String.valueOf(mpioSeleccionado)),
					puestoNuevo.getCodZona(), Util.ponerCerosIzquierda(2,
							String.valueOf(colegioSeleccionado)),
							String.valueOf(mesaSeleccionada), nombreBD, 0, urlServidor);

			if (!verificarConfiguracionAntigua()) {
				if (verificarNumeroConfiguraciones()) {
					guardado = configuracionBL.guardarConfiguracion(
							null,
							Util.ponerCerosIzquierda(2,
									String.valueOf(provSeleccionada)),
							Util.ponerCerosIzquierda(3,
									String.valueOf(mpioSeleccionado)),
							puestoNuevo.getCodZona(),
							Util.ponerCerosIzquierda(2,
									String.valueOf(colegioSeleccionado)),
									String.valueOf(mesaSeleccionada), nombreBD, urlServidor);
				}
			}
			else {
				guardado = true;
			}

		}
		catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:guardarConfiguracion:",
							e);
		}
		mostrarConfiguracionActual();
	}

	/**
	 * Verifica si se ha alcanzado el m&aacute;ximo de configuraciones
	 * permitidas y muestra el mensaje correspondiente.
	 * 
	 * @return <b>true</b> si NO se ha alcanzado el el m&aacute;ximo de
	 *         configuraciones permitidas, <b>false</b> en caso contrario.
	 */
	private boolean verificarNumeroConfiguraciones() {
		boolean res = false;
		try {

			int numeroConfiguraciones = configuracionBL
					.obtenerNumeroDeConfiguraciones();
			int limiteConfiguraciones = Integer
					.parseInt(getString(R.string.limite_configuraciones));

			if (numeroConfiguraciones >= limiteConfiguraciones) {
				AlertDialog cuadDialogo = Util.mensajeAceptar(this,
						R.style.TemaDialogo,
						getString(R.string.title_activity_login),
						getString(R.string.M45), Util.DIALOG_ERROR, null);
				cuadDialogo.show();

				Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

			} else {
				res = true;
			}

		} catch (Exception e) {
			log4jDroid
					.error("AutenticadorAndroidProject:ConfiguracionFormActivity:verificarNumeroConfiguraciones:",
							e);
		}
		return res;
	}

	/**
	 * Verifica si la configuracion a guardar ya existia con anterioridad y
	 * procede a marcarla nuevamente como activa.
	 * 
	 * @return <b>true</b> si la configuracion ya existia y ha sido marcada
	 *         nuevamente como activa, <b>false</b> en caso contrario.
	 */
	private boolean verificarConfiguracionAntigua() {
		boolean res = false;

		try {
			List<Configuracion> configuraciones = configuracionBL
					.obtenerConfiguraciones();

			String codProvNuevo = configuracionNueva.getCodProv();
			String codMpioNuevo = configuracionNueva.getCodMpio();
			String codZonaNuevo = configuracionNueva.getCodZona();
			String codColegioNuevo = configuracionNueva.getCodColElec();
			String mesaNuevo = configuracionNueva.getCodMesa();
			String ipNuevo = configuracionNueva.getIPServidor();

			if (configuraciones != null && configuraciones.size() > 0) {
				for (Configuracion c : configuraciones) {
					String codProvGuardado = c.getCodProv();
					String codMpioGuardado = c.getCodMpio();
					String codZonaGuardado = c.getCodZona();
					String codColegioGuardado = c.getCodColElec();
					String mesaGuardado = c.getCodMesa();
					String ipGuardado = c.getIPServidor();

					if (codProvGuardado.equalsIgnoreCase(codProvNuevo)
							&& codMpioGuardado.equalsIgnoreCase(codMpioNuevo)
							&& codZonaGuardado.equalsIgnoreCase(codZonaNuevo)
							&& codColegioGuardado
									.equalsIgnoreCase(codColegioNuevo)
							&& mesaGuardado.equalsIgnoreCase(mesaNuevo)
							&& ipGuardado.equalsIgnoreCase(ipNuevo)) {

						res = configuracionBL.marcarConfiguracionActiva(c
								.getId());
						break;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public void onBackPressed() {

		if (!guardado) {
			AlertDialog cuadDialogo = Util.mensajeAceptar(this,
					R.style.TemaDialogo,
					getString(R.string.title_activity_login),
					getString(R.string.M49), Util.DIALOG_INFORMATIVO,
					clickListenerVolver);
			cuadDialogo.show();

			Util.cambiarLineaDialogos(cuadDialogo, getApplicationContext());

		} else {
			volver();
		}

	}

	public OnClickListener clickListenerVolver = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			volver();
		}

	};

	private void volver() {
		Intent intent = new Intent(getApplicationContext(),
				ConfiguracionActivity.class);
		startActivity(intent);
		actividad.finish();
	}
}