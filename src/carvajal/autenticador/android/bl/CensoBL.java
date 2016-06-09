package carvajal.autenticador.android.bl;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.CensoBLException;
import carvajal.autenticador.android.dal.greendao.read.Censo;
import carvajal.autenticador.android.dal.greendao.read.CensoDao;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;

public class CensoBL {

	private DaoSession daoSession;
	private CensoDao censoDao;
	private AutenticadorKeyczarCrypter crypter;

	public CensoBL(Context context) throws AutenticadorDaoMasterSourceException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			this.censoDao = daoSession.getCensoDao();
			crypter = AutenticadorKeyczarCrypter.getInstance(context
					.getResources());
		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(e
					.getLocalizedMessage().toString(), e);
		}

	}

	/**
	 * Busca un elector en la base de datos a partir de la cedula ingresada
	 * 
	 * @param cedula
	 * @return de encontrar la cedula en la base de datos retorna un objeto de
	 *         tipo Censo con los datos del elector, null en caso contrario.
	 * @throws CensoBLException
	 */
	public Censo getElector(String cedula) throws CensoBLException {
		Censo elector = null;

		try {
			if (cedula != null && cedula.length() > 0) {
				elector = censoDao.load(cedula);
				if (elector != null) {
					elector.setPriApellido(crypter.decrypt(elector
							.getPriApellido()));
					elector.setSegApellido(crypter.decrypt(elector
							.getSegApellido()));
					elector.setPriNombre(crypter.decrypt(elector.getPriNombre()));
					elector.setSegNombre(crypter.decrypt(elector.getSegNombre()));
					elector.setFecExpedicion(crypter.decrypt(elector
							.getFecExpedicion()));
				}

			}

		} catch (Exception e) {
			throw new CensoBLException(e.getLocalizedMessage().toString(), e);
		} finally {
			if (elector != null) {
				censoDao.detach(elector);
			}
		}
		return elector;
	}

	/**
	 * Obtiene el n&uacute;mero de registros de la tabla censo
	 * 
	 * @return <b>lon</b> con el n&uacute;mero de registros encontrados
	 * @throws CensoBLException
	 */
	public long getSize() throws CensoBLException {
		long tamaño = 0;
		try {
			tamaño = censoDao.count();
		} catch (Exception e) {
			throw new CensoBLException(e.getLocalizedMessage().toString(), e);
		}
		return tamaño;
	}

}
