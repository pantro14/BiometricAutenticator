package carvajal.autenticador.android.bl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.ProvinciasBLException;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Provincias;
import carvajal.autenticador.android.dal.greendao.read.ProvinciasDao;
import carvajal.autenticador.android.dal.greendao.read.ProvinciasDao.Properties;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import de.greenrobot.dao.query.QueryBuilder;

public class ProvinciasBL {

	private DaoSession daoSession;
	private ProvinciasDao provinciasDao;

	public ProvinciasBL(Context context) throws ProvinciasBLException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			this.provinciasDao = daoSession.getProvinciasDao();
		} catch (Exception e) {
			throw new ProvinciasBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene una lista con las Provincias cargadas en la base de datos
	 * 
	 * @return <b>List</b> con las Provincias obtenidas
	 * @throws ProvinciasBLException
	 */
	public ArrayList<Provincias> obtenerProvincias()
			throws ProvinciasBLException {
		try {
			QueryBuilder<Provincias> qb = provinciasDao.queryBuilder();
			List<Provincias> departamentos = qb
					.where(Properties.NomProv.isNotNull())
					.orderAsc(Properties.NomProv).list();

			return (ArrayList<Provincias>) departamentos;

		} catch (Exception e) {
			throw new ProvinciasBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene una Provincia a partir del c&oacute;digo.
	 * 
	 * @param codProv
	 * @return Objeto de tipo <b>Provincias</b> si se encuentra el c&oacute;digo
	 *         enviado, <b>null</b> en caso contrario.
	 * @throws ProvinciasBLException
	 */
	public Provincias obtenerProvincia(String codProv)
			throws ProvinciasBLException {
		try {
			Provincias provincia = null;

			QueryBuilder<Provincias> qb = provinciasDao.queryBuilder();
			List<Provincias> departamentos = qb.where(
					Properties.CodProv.eq(codProv)).list();

			if (departamentos != null && departamentos.size() > 0) {
				provincia = departamentos.get(0);
			}

			return provincia;
		} catch (Exception e) {
			throw new ProvinciasBLException(e.getLocalizedMessage(), e);
		}
	}

}
