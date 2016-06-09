package carvajal.autenticador.android.bl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.exception.NovedadesBLException;
import carvajal.autenticador.android.dal.greendao.write.DaoSession;
import carvajal.autenticador.android.dal.greendao.write.Novedades;
import carvajal.autenticador.android.dal.greendao.write.NovedadesDao;
import carvajal.autenticador.android.dal.greendao.write.NovedadesDao.Properties;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceWrite;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author juarojre
 *
 */
public class NovedadesBL {

	private NovedadesDao novedadesDao;
	private DaoSession daoSession;
	private Context context;
	private QueryBuilder<Novedades> qb;
	private AutenticadorKeyczarCrypter crypter;

	// private SQLiteDatabase db;
	// private AutenticadorDaoMasterSource autenticadorDaoMasterSource;

	public NovedadesBL(Context context)
			throws AutenticadorDaoMasterSourceException {
		this.context = context;
		try {
			AutenticadorDaoMasterSourceWrite.getInstance(context);
			// this.db = autenticadorDaoMasterSource.getDatabase();
			this.daoSession = AutenticadorDaoMasterSourceWrite
					.getAutenticadorDaoSession();
			if (this.daoSession != null) {
				this.novedadesDao = daoSession.getNovedadesDao();
				this.qb = novedadesDao.queryBuilder();
				this.crypter = AutenticadorKeyczarCrypter.getInstance(context
						.getResources());
			}

		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(e
					.getLocalizedMessage().toString(), e);
		}

	}

	/**
	 * Valida si no se han enviado parametros nulos
	 * 
	 * @param params
	 * @return true si no hay paramtros nulos, false en caso contrario.
	 */
	private boolean validarParametros(Object[] params) {
		int contador = 0;
		boolean flag = false;
		if (params != null && params.length > 0) {
			for (Object o : params) {
				if (o != null) {
					contador++;
				}
			}

			if (contador == params.length) {
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * Valida si los parámetros ingresados son nulos, de ser así los asigna con
	 * los valores correpsondientes del archivo de strings
	 * 
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @return Objeto tipo Map<String, String> con los códigos divipol.
	 */
	private Map<String, String> validarDivipol(String codProv, String codMpio,
			String codZona, String codColegio) {

		Map<String, String> divipol = new HashMap<String, String>();
		if (codProv != null && codProv.length() > 0) {
			divipol.put("codProv", codProv);
		} else {
			divipol.put("codProv", context.getString(R.string.dpto_config));
		}

		if (codMpio != null && codMpio.length() > 0) {
			divipol.put("codMpio", codMpio);
		} else {
			divipol.put("codMpio", context.getString(R.string.mpio_config));
		}

		if (codZona != null && codZona.length() > 0) {
			divipol.put("codZona", codZona);
		} else {
			divipol.put("codZona", context.getString(R.string.zona_config));
		}

		if (codColegio != null && codColegio.length() > 0) {
			divipol.put("codColegio", codColegio);
		} else {
			divipol.put("codColegio", context.getString(R.string.puesto_config));
		}

		return divipol;
	}

	/**
	 * Este metodo crea un objeto de tipo Novedad con los datos solicitados y lo
	 * envia a la base de datos.
	 * 
	 * @param tipoNovedad
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param templateHit
	 * @param score
	 * @param sync
	 * @return numero de filas afectadas.
	 * @throws NovedadesBLException
	 */

	private long guardarNovedad(int tipoNovedad, String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer templateHit, Integer score,
			Integer sync) throws NovedadesBLException {

		try {

			String androidId = Util.obtenerMAC(context);

			Map<String, String> divipol = validarDivipol(codProv, codMpio,
					codZona, codColegio);

			Map<String, String> divipolConf = new Util()
					.obtenerDivipolPuesto(context);

			if (templateHit != null && Integer.valueOf(templateHit) > 0) {
				templateHit = Integer.valueOf(templateHit) + 30;
			}

			Novedades novedad = new Novedades(null,
					String.valueOf(tipoNovedad), cedula,
					crypter.encrypt(divipol.get("codProv")),
					crypter.encrypt(divipol.get("codMpio")),
					crypter.encrypt(divipol.get("codZona")),
					crypter.encrypt(divipol.get("codColegio")),
					crypter.encrypt(codMesa), crypter.encrypt(String
							.valueOf(new Date().getTime())),
					crypter.encrypt(String.valueOf(tipoElector)),
					crypter.encrypt(String.valueOf(templateHit)),
					crypter.encrypt(androidId), crypter.encrypt(String
							.valueOf(score)), crypter.encrypt(divipolConf
							.get("codProv")), crypter.encrypt(divipolConf
							.get("codMpio")), crypter.encrypt(divipolConf
							.get("codZona")), crypter.encrypt(divipolConf
							.get("codColegio")), crypter.encrypt(divipolConf
							.get("codMesa")), String.valueOf(sync));

			return novedadesDao.insert(novedad);
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}

	}

	/**
	 * Este caso se presenta cuando el ciudadano que se está intentando
	 * autenticar no está registrado en el censo contenido en la base de datos
	 * de la estación.
	 * 
	 * @param cedula
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarElectorNoEncontrado(String cedula)
			throws NovedadesBLException {
		try {
			Object params[] = { cedula };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.no_encontrado));
				if (guardarNovedad(tipoNovedad, cedula, null, null, null, null,
						null, null, null, null, null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se presenta cuando el sistema encuentra que aunque el ciudadano
	 * que se está intentando autenticar si está registrado en la base de datos,
	 * no corresponde al puesto de votación registrado para la estación
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarElectorEnColegioElectoralDiferente(String cedula,
			String codProv, String codMpio, String codZona, String codColegio,
			String codMesa) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.en_colegio_electoral_diferente));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, null, null, null, null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se presenta cuando a pesar de que el sistema encuentra el
	 * número de cédula en el censo registrado en la base de datos de la
	 * estación, el operador comprueba que los datos biográficos que presenta el
	 * sistema son diferentes a los registrados en el documento de identidad del
	 * elector.
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarDatosNoCoinciden(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.datos_no_coinciden));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Aunque el número de cédula del elector si este registrado en el censo
	 * cargado en la base de datos de la estación y aunque el mismo elector se
	 * encuentre en el puesto de votación correcto, puede suceder que la huella
	 * tomada del elector no coincida con ninguna de las registradas en el
	 * sistema para dicho número de cédula o que no se pueda capturar la huella
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param score
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarNoSePudoAutenticar(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer score) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector, score };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.no_se_pudo_autenticar));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, score,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Aunque el sistema encuentra que el número de cédula del elector si está
	 * registrado en el censo cargado en su base de datos y el mismo elector
	 * está en el puesto de votación correcto, no se tienen las huellas para
	 * poder hacer la validación biométrica
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarSinInformacionBiometrica(String cedula,
			String codProv, String codMpio, String codZona, String codColegio,
			String codMesa, Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.sin_informacion_biometrica));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Caso en el que un ciudadano no pone su huella o no se reconoce en 4
	 * segundos luego de que el sistema esté preparado para realizar la captura
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return
	 * @throws NovedadesBLException
	 */
	public boolean notificarNoPusoDedoParaValidacion(String cedula,
			String codProv, String codMpio, String codZona, String codColegio,
			String codMesa, Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.no_puso_dedo_para_validacion));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Caso en el que el ciudadano se presenta más de una vez a la autenticación
	 * biométrica
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarYaAutenticado(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.ya_autenticado));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se edbe notificar cuando se ha impreso un duplicado del
	 * comprobante
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param sync
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarDuplicadoImpreso(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer sync) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.duplicado_impreso));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						sync) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se notifica cuando se ha impreso un comprobante
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param sync
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarCertificadoImpreso(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer sync) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.certificado_impreso));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						sync) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se da cuando el Elector se ha autenticado exitosamente.
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param templateHit
	 * @param score
	 * @param sync
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarAutenticado(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer templateHit, Integer score,
			Integer sync) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.autenticado));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, templateHit,
						score, sync) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se da cuando la autenticación se logra con la aprobación de un
	 * delegado de la registraduria
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @param templateHit
	 * @param score
	 * @param sync
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarAutenticadoDelegado(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector, Integer templateHit, Integer score,
			Integer sync) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.autenticacion_asistida_delegado));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, templateHit,
						score, sync) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este caso se da cuando se intenta imprimir un nuevo comprobante despues
	 * de haberse impreso el duplicado
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarIntentoReimpresionComprobante(String cedula,
			String codProv, String codMpio, String codZona, String codColegio,
			String codMesa, Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.intento_reimpresion_comprobante));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Caso en el que el ciudadano es reportado por la Junta Central electoral
	 * como impedido para ejercer el derecho al voto en República Dominicana.
	 * 
	 * @param cedula
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColegio
	 * @param codMesa
	 * @param tipoElector
	 * @return true si se realiza la insercion, false en caso contrario.
	 * @throws NovedadesBLException
	 */
	public boolean notificarElectorImpedido(String cedula, String codProv,
			String codMpio, String codZona, String codColegio, String codMesa,
			Integer tipoElector) throws NovedadesBLException {
		try {
			Object params[] = { cedula, codProv, codMpio, codZona, codColegio,
					codMesa, tipoElector };
			if (validarParametros(params)) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.elector_impedido));
				if (guardarNovedad(tipoNovedad, cedula, codProv, codMpio,
						codZona, codColegio, codMesa, tipoElector, null, null,
						null) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este metodo verifica en la base de datos si ya se ha impreso un duplicado
	 * del comprobante para la cedula dada.
	 * 
	 * @param cedula
	 * @return true si se ya se ha impreso un duplicado, false en caso contrario
	 * @throws NovedadesBLException
	 */
	public boolean isDuplicadoImpreso(String cedula)
			throws NovedadesBLException {
		try {
			boolean flag = false;

			if (cedula != null && cedula.length() > 0) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.duplicado_impreso));
				qb = novedadesDao.queryBuilder();
				List<Novedades> duplicados = qb.where(
						Properties.Cedula.eq(cedula),
						Properties.TipoNovedad.eq(String.valueOf(tipoNovedad)))
						.list();

				if (duplicados != null && duplicados.size() > 0) {
					flag = true;
				}
			}

			return flag;
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este metodo verifica en la base de datos si ya se ha impreso un
	 * comprobante para la cedula dada.
	 * 
	 * @param cedula
	 * @return true si se ya se ha impreso un certificado, false en caso
	 *         contrario
	 * @throws NovedadesBLException
	 */
	public boolean isCertificadoImpreso(String cedula)
			throws NovedadesBLException {
		try {
			boolean flag = false;

			if (cedula != null && cedula.length() > 0) {
				int tipoNovedad = Integer.parseInt(context
						.getString(R.string.certificado_impreso));
				qb = novedadesDao.queryBuilder();
				List<Novedades> duplicados = qb.where(
						Properties.Cedula.eq(cedula),
						Properties.TipoNovedad.eq(String.valueOf(tipoNovedad)))
						.list();

				if (duplicados != null && duplicados.size() > 0) {
					flag = true;
				}
			}

			return flag;
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Valida en la base de datos si ya hubo una autenticacion exitosa para la
	 * cedula dada
	 * 
	 * @param cedula
	 * @return Objeto de tipo <b>Novedades</b> si se ha autenticado. <br/>
	 *         <b>null</b> en caso contrario.
	 * @throws NovedadesBLException
	 */
	public Novedades obtenerAutenticado(String cedula)
			throws NovedadesBLException {
		Novedades novedad = null;
		try {

			if (cedula != null && cedula.length() > 0) {
				String tipoNovedad = context.getString(R.string.autenticado);
				String tipoNovedad2 = context
						.getString(R.string.autenticacion_asistida_delegado);

				qb = novedadesDao.queryBuilder();
				List<Novedades> autenticaciones = qb.where(
						Properties.Cedula.eq(cedula),
						qb.or(Properties.TipoNovedad.eq(tipoNovedad),
								Properties.TipoNovedad.eq(tipoNovedad2)))
						.list();

				/*
				 * List autenticaciones = novedadesDao .queryBuilder()
				 * .where(Properties.Cedula.eq(cedula),
				 * Properties.TipoNovedad.eq(tipoNovedad)).list();
				 */

				if (autenticaciones != null && autenticaciones.size() > 0) {
					novedad = (Novedades) autenticaciones.get(0);
				}
			}
			return novedad;
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este método permite obtener la totalidad de electores autenticados
	 * autorizados por delegado, en el dispositivo móvil. para ellos se buscan
	 * las novedades tipo 11: autenticacion_asistida_delegado
	 * 
	 * @return número de electores autenticados
	 * @throws NovedadesBLException
	 */
	public List<Novedades> obtenerCedulasAutorizaDelegado()
			throws NovedadesBLException {

		try {
			qb = novedadesDao.queryBuilder();
			List<Novedades> novedades = qb
					.where(Properties.TipoNovedad.eq(context
							.getString(R.string.autenticacion_asistida_delegado)))
					.list();

			return novedades;
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Este método permite obtener el NÚMERO total de electores autenticados en el
	 * dispositivo móvil. para ellos se buscan las novedades de tipo 0:
	 * autenticado y tipo 11: autenticacion_asistida_delegado
	 * 
	 * @return número de electores autenticados
	 * @throws NovedadesBLException
	 */
	public int obtenerConteoElectoresAutenticados() throws NovedadesBLException {
		String novedad_autenticacion = context.getString(R.string.autenticado);
		String novedad_aut_asistida_delegado = context
				.getString(R.string.autenticacion_asistida_delegado);
		try {
			long count = 0;
			if (novedadesDao != null) {
				qb = novedadesDao.queryBuilder();
				count = qb.where(
						qb.or(Properties.TipoNovedad.eq(novedad_autenticacion),
								Properties.TipoNovedad
										.eq(novedad_aut_asistida_delegado)))
						.count();
			}
			return (int) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}
	
	/**
	 * Este método permite obtener la lista de electores autenticados en el
	 * dispositivo móvil. para ellos se buscan las novedades< de tipo 0:
	 * autenticado y tipo 11: autenticacion_asistida_delegado
	 * 
	 * @return número de electores autenticados
	 * @throws NovedadesBLException
	 */
	public List<Novedades> obtenerElectoresAutenticados() throws NovedadesBLException {
		String novedad_autenticacion = context.getString(R.string.autenticado);
		String novedad_aut_asistida_delegado = context
				.getString(R.string.autenticacion_asistida_delegado);
		try {
			List<Novedades> novedades = new ArrayList<Novedades>();
			if (novedadesDao != null) {
				qb = novedadesDao.queryBuilder();
				novedades = qb.where(
						qb.or(Properties.TipoNovedad.eq(novedad_autenticacion),
								Properties.TipoNovedad
										.eq(novedad_aut_asistida_delegado)))
						.list();
			}
			return novedades;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Obtiene las novedades realacionadas a un numero de cedula
	 * 
	 * @param cedula
	 * @return Lista de novedades correspondientes a un numero de cedula
	 * @throws NovedadesBLException
	 */
	public List<Novedades> obtenerNovedadesCedula(String cedula)
			throws NovedadesBLException {

		try {

			List<Novedades> novedades = new ArrayList<Novedades>();

			if (novedadesDao != null) {
				qb = novedadesDao.queryBuilder();
				novedades = qb.where(Properties.Cedula.eq(cedula)).list();
			}

			return novedades;
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Método utilizado para recuperar el porcentaje de novedades sincronizadas
	 * en el servidor central. Primeramente, se realiza la consulta del total de
	 * novedades de tipo 11: autenticacion_asistida_delegado, 0: autenticado, 8:
	 * duplicado_impreso, 9: certificado_impreso. Seguidamente se consulta
	 * tambien el total de novedades con el campo sync = 1, y finalmente se
	 * ejecuta la razón entre las sincronizadas y el total, multiplicado por
	 * 100.
	 * 
	 * @return porcentaje porcentaje de novedades sincronizadas.
	 * @throws NovedadesBLException
	 */
	public String obtenerPorcentajeNovedadesSincronizadas()
			throws NovedadesBLException {
		String novedad_autenticacion = context.getString(R.string.autenticado);
		String novedad_aut_asistida_delegado = context
				.getString(R.string.autenticacion_asistida_delegado);
		String novedad_duplicado_impreso = context
				.getString(R.string.duplicado_impreso);
		String novedad_certificado_impreso = context
				.getString(R.string.certificado_impreso);
		String flag_sincronizado = context
				.getString(R.string.novedad_sincronizada);

		try {

			qb = novedadesDao.queryBuilder();
			double total = qb.where(
					qb.or(Properties.TipoNovedad.eq(novedad_autenticacion),
							Properties.TipoNovedad
									.eq(novedad_aut_asistida_delegado),
							Properties.TipoNovedad
									.eq(novedad_duplicado_impreso),
							Properties.TipoNovedad
									.eq(novedad_certificado_impreso))).count();

			double sync = qb.where(Properties.Sync.eq(flag_sincronizado))
					.count();

			if (total == 0) {
				return "0%";
			} else {
				double porcentaje = (sync / total) * 100;
				DecimalFormat df = new DecimalFormat("0");
				return df.format(porcentaje) + "%";
			}
		} catch (Exception e) {
			throw new NovedadesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}
}
