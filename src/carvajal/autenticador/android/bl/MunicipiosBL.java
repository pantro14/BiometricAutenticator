package carvajal.autenticador.android.bl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.MunicipiosBLException;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Municipios;
import carvajal.autenticador.android.dal.greendao.read.MunicipiosDao;
import carvajal.autenticador.android.dal.greendao.read.MunicipiosDao.Properties;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.util.Util;
import de.greenrobot.dao.query.QueryBuilder;

public class MunicipiosBL {

	private DaoSession daoSession;
	private MunicipiosDao municipiosDao;

	public MunicipiosBL(Context context) throws MunicipiosBLException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			this.municipiosDao = daoSession.getMunicipiosDao();
		} catch (Exception e) {
			throw new MunicipiosBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

	/**
	 * Obtiene los municipios relacionados con un departamento
	 * 
	 * @param codProv
	 * @return Lista con los municipios correspondientes al c&oaucte;digo de
	 *         departamento, o null en caso de no encontrarse municipios.
	 * @throws MunicipiosBLException
	 */
	public ArrayList<Municipios> obtenerMunicipios(String codProv)
			throws MunicipiosBLException {
		try {

			codProv = Util.ponerCerosIzquierda(2, codProv);

			ArrayList<Municipios> municipios = null;

			QueryBuilder<Municipios> qb = municipiosDao.queryBuilder();
			municipios = (ArrayList<Municipios>) qb
					.where(Properties.CodProv.eq(codProv))
					.orderAsc(Properties.NomMpio).list();

			return municipios;
		} catch (Exception e) {
			throw new MunicipiosBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene un Municipio a partir del c&oacute;digo del Departamento y el
	 * c&oacute;digo del Municipio
	 * 
	 * @param codProv
	 * @param codMpio
	 * @return <b>Municipio</b> si se encuentra, <b>null</b> en caso contrario.
	 * @throws MunicipiosBLException
	 */
	public Municipios obtenerMunicipio(String codProv, String codMpio)
			throws MunicipiosBLException {
		try {

			Municipios municipio = null;

			QueryBuilder<Municipios> qb = municipiosDao.queryBuilder();
			List<Municipios> municipios = qb.where(
					Properties.CodProv.eq(codProv),
					Properties.CodMpio.eq(codMpio)).list();

			if (municipios != null && municipios.size() > 0) {
				municipio = municipios.get(0);
			}

			return municipio;
		} catch (Exception e) {
			throw new MunicipiosBLException(e.getLocalizedMessage(), e);
		}
	}

}
