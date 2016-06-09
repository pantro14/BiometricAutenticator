package carvajal.autenticador.android.bl;

import android.content.Context;
import carvajal.autenticador.android.bl.exception.TemplatesBLException;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Templates;
import carvajal.autenticador.android.dal.greendao.read.TemplatesDao;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;

public class TemplatesBL {

	private DaoSession daoSession;
	private TemplatesDao templatesDao;

	public TemplatesBL(Context context)
			throws AutenticadorDaoMasterSourceException {
		try {
			AutenticadorDaoMasterSourceRead.getInstance(context);
			this.daoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();
			this.templatesDao = daoSession.getTemplatesDao();
		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(e
					.getLocalizedMessage().toString(), e);
		}

	}

	/**
	 * Metodo para obtener las huellas del elector en base a la cedula
	 * 
	 * @param cedula
	 * @return Objeto de tipo Templates en caso de coincidir la cedula con las
	 *         registradas en la tabla de la base de datos, null en caso
	 *         contrario
	 * @throws TemplatesBLException
	 */
	public Templates getTemplates(String cedula) throws TemplatesBLException {
		Templates templates = null;
		try {
			if (cedula != null && cedula.length() > 0) {
				templates = templatesDao.load(cedula);
			}

			return templates;
		} catch (Exception e) {
			throw new TemplatesBLException(e.getLocalizedMessage().toString(),
					e);
		}
	}

}
