package carvajal.autenticador.android.bl;

import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.exception.ConfiguracionBLException;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.read.ConfiguracionDao;
import carvajal.autenticador.android.dal.greendao.read.ConfiguracionDao.Properties;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import de.greenrobot.dao.query.QueryBuilder;

public class ConfiguracionBL {

	private DaoSession daoSession;
	private ConfiguracionDao configuracionDao;
	private Context context;

	public ConfiguracionBL(Context context)
			throws AutenticadorDaoMasterSourceException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			if (this.daoSession != null) {
				this.configuracionDao = daoSession.getConfiguracionDao();
				// this.actividad = actividad;
				this.context = context;
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
	 * Obtiene el conteo de filas de la tabla de configuraci&oacute;n
	 * 
	 * @return N&uacute;mero de filas de la tabla de configuraci&oacute;n
	 */
	public long getConfiguracionCount() {
		return configuracionDao.count();
	}

	/**
	 * Almacena los par&aacute;metros de configuración del dispositivo en la
	 * base de datos
	 * 
	 * @param id
	 * @param CodProv
	 * @param CodMpio
	 * @param CodZona
	 * @param CodColElec
	 * @param Mesa
	 * @param NombreBD
	 * @param ConfActiva
	 * @param IPServidor
	 * @return <b>true</b> si se logró almacenar la configuracion, <b>false</b>
	 *         en caso contrario.
	 * @throws ConfiguracionBLException
	 */
	public boolean guardarConfiguracion(Long id, String CodProv,
			String CodMpio, String CodZona, String CodColElec, String Mesa,
			String NombreBD, String IPServidor) throws ConfiguracionBLException {
		try {

			boolean flag = false;
			// se marcan las configuraciones anteriores como inactivas
			desactivarConfiguracionesAntiguas();

			Object params[] = { CodProv, CodMpio, CodZona, CodColElec, Mesa,
					NombreBD, IPServidor };

			// se validan valores nulos
			if (validarParametros(params)) {
				Configuracion configuracion = new Configuracion(id, CodProv,
						CodMpio, CodZona, CodColElec, Mesa, NombreBD,
						Integer.valueOf(context
								.getString(R.string.configuracion_activa)),
						IPServidor);

				if (configuracionDao.insert(configuracion) > 0) {
					flag = true;
				}
			}

			return flag;

		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * obtiene la configuraci&oacute;n que se encuentre marcada como
	 * <b>activa</b> en la base de datos
	 * 
	 * @return Objeto de tipo <b>Configuracion</b> si existe configuracion
	 *         activa. <br/>
	 *         <b>null</b> en caso contrario.
	 * @throws ConfiguracionBLException
	 */
	public Configuracion obtenerConfiguracionActiva()
			throws ConfiguracionBLException {
		Configuracion configuracion = null;
		try {
			int configuracionActiva = Integer.parseInt(context
					.getString(R.string.configuracion_activa));
			QueryBuilder<Configuracion> qb = configuracionDao.queryBuilder();
			List<Configuracion> configuraciones = qb.where(
					Properties.ConfActiva.eq(configuracionActiva)).list();

			if (configuraciones != null && configuraciones.size() > 0) {
				configuracion = configuraciones.get(0);
			}

			return configuracion;
		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Obtiene el n&uacute;mero de configuraciones realizadas.
	 * 
	 * @return n&uacute;mero de configuraciones realizadas.
	 * @throws ConfiguracionBLException
	 */
	public int obtenerNumeroDeConfiguraciones() throws ConfiguracionBLException {
		try {
			int configuraciones = 0;

			configuraciones = (int) configuracionDao.count();

			return configuraciones;
		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Marca todas las configuraciones como inactivas en la base de datos
	 * 
	 * @throws ConfiguracionBLException
	 */
	private void desactivarConfiguracionesAntiguas()
			throws ConfiguracionBLException {
		try {
			List<Configuracion> configuraciones = configuracionDao.loadAll();
			if (configuraciones != null && configuraciones.size() > 0) {
				for (Configuracion c : configuraciones) {
					c.setConfActiva(Integer.valueOf(context
							.getString(R.string.configuracion_inactiva)));
					configuracionDao.insertOrReplace(c);
				}

			}
		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene una lista de todas las configuraciones almacenadas en la base de
	 * datos.
	 * 
	 * @return Lista de configuraciones almacenadas en la base de datos.
	 * @throws ConfiguracionBLException
	 */
	public List<Configuracion> obtenerConfiguraciones()
			throws ConfiguracionBLException {
		try {
			return configuracionDao.loadAll();
		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Marca la configuracion identificada por el id ingresado como activa.
	 * 
	 * @param id
	 * @return <b>true</b> si se logró marcar la configuracion como activa,
	 *         <b>false</b> en caso contrario.
	 * @throws ConfiguracionBLException
	 */
	public boolean marcarConfiguracionActiva(long id)
			throws ConfiguracionBLException {
		boolean res = false;
		try {

			Configuracion configuracion = configuracionDao.load(id);
			if (configuracion != null) {
				desactivarConfiguracionesAntiguas();
				configuracion.setConfActiva(Integer.valueOf(context
						.getString(R.string.configuracion_activa)));
				configuracionDao.insertOrReplace(configuracion);
				res = true;
			}

		} catch (Exception e) {
			throw new ConfiguracionBLException(e.getLocalizedMessage(), e);
		}
		return res;
	}

}
