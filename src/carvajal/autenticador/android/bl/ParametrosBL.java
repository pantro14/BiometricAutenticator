package carvajal.autenticador.android.bl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.ParametrosBLException;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Parametros;
import carvajal.autenticador.android.dal.greendao.read.ParametrosDao;
import carvajal.autenticador.android.dal.greendao.read.ParametrosDao.Properties;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import de.greenrobot.dao.query.QueryBuilder;

public class ParametrosBL {

	private DaoSession daoSession;
	private ParametrosDao parametrosDao;

	public ParametrosBL(Context context) throws AutenticadorDaoMasterSourceException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			if (this.daoSession != null) {
				this.parametrosDao = daoSession.getParametrosDao();
			}

		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(e.getLocalizedMessage().toString(), e);
		}
	}
	
	public ArrayList<Parametros> obtenerParametros() throws ParametrosBLException {
		try {
			QueryBuilder<Parametros> qb = parametrosDao.queryBuilder();
			List<Parametros> parametros = qb.where(Properties.ValParam.isNotNull()).list();
			return (ArrayList<Parametros>) parametros;
		}
		catch (Exception e) {
			throw new ParametrosBLException(e.getLocalizedMessage(), e);
		}
	}
	
	public Parametros obtenerParametroPorNombre(String nomParam) throws ParametrosBLException {
		try {
			Parametros parametro = null;
			QueryBuilder<Parametros> qb = parametrosDao.queryBuilder();
			List<Parametros> listParametros = qb.where(
					Properties.NomParam.eq(nomParam)).list();
			
			if (listParametros != null && !listParametros.isEmpty()) {
				parametro = listParametros.get(0);
			}
			return parametro;
		}
		catch (Exception e) {
			throw new ParametrosBLException(e.getLocalizedMessage(), e);
		}
	}
}
