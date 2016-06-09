package carvajal.autenticador.android.bl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.MesasBLException;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Mesas;
import carvajal.autenticador.android.dal.greendao.read.MesasDao;
import carvajal.autenticador.android.dal.greendao.read.MesasDao.Properties;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import de.greenrobot.dao.query.QueryBuilder;

public class MesasBL {

	private DaoSession daoSession;
	private MesasDao mesasDao;

	public MesasBL(Context context) throws AutenticadorDaoMasterSourceException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			if (this.daoSession != null) {
				this.mesasDao = daoSession.getMesasDao();
			}

		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(e
					.getLocalizedMessage().toString(), e);
		}
	}

	/**
	 * Obtiene una lista de las mesas registradas en la divipol igresada
	 * 
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColElec
	 * @return Lista con las Mesas encontradas.
	 * @throws MesasBLException
	 */
	public ArrayList<Mesas> obtenerMesas(String codProv, String codMpio,
			String codZona, String codColElec) throws MesasBLException {
		try {

			codProv = Util.ponerCerosIzquierda(2, codProv);
			codMpio = Util.ponerCerosIzquierda(3, codMpio);
			codZona = Util.ponerCerosIzquierda(2, codZona);
			codColElec = Util.ponerCerosIzquierda(2, codColElec);

			ArrayList<Mesas> mesas = null;

			QueryBuilder<Mesas> qb = mesasDao.queryBuilder();
			mesas = (ArrayList<Mesas>) qb.where(Properties.CodZona.eq(codZona),
					Properties.CodMpio.eq(codMpio),
					Properties.CodProv.eq(codProv),
					Properties.CodColElec.eq(codColElec)).list();

			return mesas;
		} catch (Exception e) {
			throw new MesasBLException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Obtiene la mesa que cumpla con el divipol ingresado
	 * 
	 * @param codProv
	 * @param codMpio
	 * @param codZona
	 * @param codColElec
	 * @param codMesa
	 * @return Objeto de tipo Mesa encontrado.
	 * @throws MesasBLException
	 */
	public Mesas obtenerMesa(String codProv, String codMpio, String codZona,
			String codColElec, String codMesa) throws MesasBLException {
		try {
			Mesas mesa = null;

			codProv = Util.ponerCerosIzquierda(2, codProv);
			codMpio = Util.ponerCerosIzquierda(3, codMpio);
			codZona = Util.ponerCerosIzquierda(2, codZona);
			codColElec = Util.ponerCerosIzquierda(2, codColElec);

			QueryBuilder<Mesas> qb = mesasDao.queryBuilder();
			List<Mesas> mesas = qb.where(Properties.CodZona.eq(codZona),
					Properties.CodMpio.eq(codMpio),
					Properties.CodProv.eq(codProv),
					Properties.CodColElec.eq(codColElec),
					Properties.CodMesa.eq(codMesa)).list();

			if (mesas != null && mesas.size() > 0) {
				mesa = mesas.get(0);
			}

			return mesa;
		} catch (Exception e) {
			throw new MesasBLException(e.getLocalizedMessage(), e);
		}
	}

}
