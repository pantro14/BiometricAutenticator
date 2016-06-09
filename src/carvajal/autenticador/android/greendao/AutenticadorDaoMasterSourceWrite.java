package carvajal.autenticador.android.greendao;

import java.io.File;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.ConfiguracionBL;
import carvajal.autenticador.android.dal.greendao.read.Configuracion;
import carvajal.autenticador.android.dal.greendao.read.ConfiguracionDao;
import carvajal.autenticador.android.dal.greendao.write.DaoMaster;
import carvajal.autenticador.android.dal.greendao.write.DaoMaster.DevOpenHelper;
import carvajal.autenticador.android.dal.greendao.write.DaoSession;
import carvajal.autenticador.android.greendao.exception.AutenticadorDaoMasterSourceException;
import carvajal.autenticador.android.util.Util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AutenticadorDaoMasterSourceWrite {
	/**
	 * instance
	 */
	private static AutenticadorDaoMasterSourceWrite instance = null;
	private static ConfiguracionBL configuracionBL = null;
	private static Configuracion configuracion = null;

	/**
	 * Objects Creation DataBase
	 */
	private static SQLiteDatabase autenticadorDataBase = null;
	private static DaoMaster autenticadorDaoMaster = null;
	private static DaoSession autenticadorDaoSession = null;
	private static String PATH_DATA_BASE = null;
	private static String DATA_BASE_NAME = null;

	private AutenticadorDaoMasterSourceWrite(Context context)
			throws AutenticadorDaoMasterSourceException {
		try {
			/*
			 * Obtiene las variables de la carpeta y nombre de la base de datos
			 * del archivo de Strings
			 */

			if (configuracionBL == null) {
				configuracionBL = new ConfiguracionBL(context);
			}

			if (configuracion == null) {
				configuracion = configuracionBL.obtenerConfiguracionActiva();
			}
			if (configuracion != null) {

				String rutaAlmacenamiento = Util
						.obtenerAlmacenamientoSecundario();
				PATH_DATA_BASE = rutaAlmacenamiento.concat(context
						.getString(R.string.carpeta_base_de_datos));
				DATA_BASE_NAME = configuracion.getNombreBD();

				/*
				 * Verifica si la carpeta de la base de datos existe, si no
				 * existe la crea
				 */
				File dataBasePath = new File(PATH_DATA_BASE);
				if (!dataBasePath.exists()) {
					dataBasePath.mkdir();
				}

				DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
						PATH_DATA_BASE, DATA_BASE_NAME, null);
				autenticadorDataBase = helper.getWritableDatabase();
				autenticadorDaoMaster = new DaoMaster(autenticadorDataBase);
				autenticadorDaoSession = autenticadorDaoMaster.newSession();
				autenticadorDaoMaster.createAllTables(autenticadorDataBase,
						true);
			}

		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(
					e.getLocalizedMessage(), e);
		}
	}

	public static AutenticadorDaoMasterSourceWrite getInstance(Context context)
			throws AutenticadorDaoMasterSourceException {
		try {
			configuracionBL = new ConfiguracionBL(context);
			configuracion = configuracionBL.obtenerConfiguracionActiva();
			if (instance == null
					|| (DATA_BASE_NAME != null && !DATA_BASE_NAME
							.equalsIgnoreCase(configuracion.getNombreBD()))) {
				instance = new AutenticadorDaoMasterSourceWrite(context);

			}
			return instance;
		} catch (Exception e) {
			throw new AutenticadorDaoMasterSourceException(
					e.getLocalizedMessage(), e);
		}
	}

	public static DaoSession getAutenticadorDaoSession() {
		return autenticadorDaoSession;
	}

	public static SQLiteDatabase getDatabase() {
		return autenticadorDataBase;
	}
}
