package carvajal.autenticador.android.bl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.ColegiosElectoralesBLException;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectorales;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectoralesDao;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectoralesDao.Properties;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.util.Util;
import de.greenrobot.dao.query.QueryBuilder;

public class ColegiosElectoralesBL {

	private DaoSession daoSession;
	private ColegiosElectoralesDao colegiosDao;

	public ColegiosElectoralesBL(Context context)
			throws ColegiosElectoralesBLException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			this.colegiosDao = daoSession.getColegiosElectoralesDao();
		} catch (Exception e) {
			throw new ColegiosElectoralesBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtienes una lista de puestos a partir del c&oacute;digo del municipio y
	 * el c&oacute;digo de la zona
	 * 
	 * @param codMpio
	 * @param codZona
	 * @return Lista de puestos que concuerdan con los par&aacute;metros
	 *         ingresados.
	 * @throws ColegiosElectoralesBLException
	 */
	public ArrayList<ColegiosElectorales> obtenerPuestos(String codProv,
			String codMpio) throws ColegiosElectoralesBLException {
		try {

			codProv = Util.ponerCerosIzquierda(2, codProv);
			codMpio = Util.ponerCerosIzquierda(3, codMpio);

			ArrayList<ColegiosElectorales> puestos = null;

			QueryBuilder<ColegiosElectorales> qb = colegiosDao.queryBuilder();
			puestos = (ArrayList<ColegiosElectorales>) qb
					.where(Properties.CodProv.eq(codProv),
							Properties.CodMpio.eq(codMpio))
					.orderAsc(Properties.NomColElec).list();

			return puestos;
		} catch (Exception e) {
			throw new ColegiosElectoralesBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene un puesto a partir del c&oacute;digo del departamento, municipio,
	 * zona y puesto
	 *
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColElec
	 * @return <b>Puesto</b> correspondiente a los par&aacute;metros ingresados,
	 *         o <b>null</b> si no se encuentra el puesto
	 * @throws ColegiosElectoralesBLException
	 */
	public ColegiosElectorales obtenerPuesto(String codProv, String codMpio,
			String codZona, String codColElec)
			throws ColegiosElectoralesBLException {
		try {
			ColegiosElectorales puesto = null;

			codProv = Util.ponerCerosIzquierda(2, codProv);
			codMpio = Util.ponerCerosIzquierda(3, codMpio);
			codZona = Util.ponerCerosIzquierda(2, codZona);
			codColElec = Util.ponerCerosIzquierda(2, codColElec);

			QueryBuilder<ColegiosElectorales> qb = colegiosDao.queryBuilder();
			List<ColegiosElectorales> puestos = qb.where(
					Properties.CodZona.eq(codZona),
					Properties.CodMpio.eq(codMpio),
					Properties.CodProv.eq(codProv),
					Properties.CodColElec.eq(codColElec)).list();

			if (puestos != null && puestos.size() > 0) {
				puesto = puestos.get(0);
			}

			return puesto;
		} catch (Exception e) {
			throw new ColegiosElectoralesBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene el c&oacute;digo de zona a partir de los par&aacute;metros
	 * ingresados.
	 * 
	 * @param codProv
	 * @param codMpio
	 * @param nomColElec
	 * @return C&oacute;digo de zona de ser encontrado, <b>null</b> en caso
	 *         contrario
	 * @throws ColegiosElectoralesBLException
	 */
	public String obtenerZona(String codProv, String codMpio, String nomColElec)
			throws ColegiosElectoralesBLException {
		try {
			String codZona = null;

			codProv = Util.ponerCerosIzquierda(2, codProv);
			codMpio = Util.ponerCerosIzquierda(3, codMpio);

			QueryBuilder<ColegiosElectorales> qb = colegiosDao.queryBuilder();
			List<ColegiosElectorales> puestos = qb.where(
					Properties.CodMpio.eq(codMpio),
					Properties.CodProv.eq(codProv),
					Properties.NomColElec.eq(nomColElec)).list();

			if (puestos != null && puestos.size() > 0) {
				codZona = puestos.get(0).getCodZona();
			}

			return codZona;
		} catch (Exception e) {
			throw new ColegiosElectoralesBLException(e.getLocalizedMessage(), e);
		}
	}
}
