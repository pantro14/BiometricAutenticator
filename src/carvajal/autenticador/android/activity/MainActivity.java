package carvajal.autenticador.android.activity;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import carvajal.autenticador.android.dal.greendao.read.Censo;
import carvajal.autenticador.android.dal.greendao.read.CensoDao;
import carvajal.autenticador.android.dal.greendao.read.DaoSession;
import carvajal.autenticador.android.dal.greendao.read.Templates;
import carvajal.autenticador.android.dal.greendao.read.TemplatesDao;
import carvajal.autenticador.android.greendao.AutenticadorDaoMasterSourceRead;
import carvajal.autenticador.android.util.BackupUtil;
import carvajal.autenticador.android.util.Util;
import carvajal.autenticador.android.util.keyczar.AutenticadorKeyczarCrypter;

public class MainActivity extends Activity {

	/**
	 * Objects Creation DataBase
	 */
	private DaoSession autenticadorDaoSession = null;

	/**
	 * Objects
	 */
	private Censo censo = null;
	private Templates template = null;

	/**
	 * Daos
	 */
	private CensoDao censoDao = null;
	private TemplatesDao templatesDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			/**
			 * Setting DataBase Environment
			 */
			AutenticadorDaoMasterSourceRead.getInstance(this);
			autenticadorDaoSession = AutenticadorDaoMasterSourceRead
					.getAutenticadorDaoSession();

			/**
			 * Keyczar
			 */
			AutenticadorKeyczarCrypter keyczar = AutenticadorKeyczarCrypter
					.getInstance(getResources());

			/**
			 * Instance DAO's
			 */
			censoDao = autenticadorDaoSession.getCensoDao();
			templatesDao = autenticadorDaoSession.getTemplatesDao();

			/**
			 * File Path
			 */
			String rutaAlmacenamiento = Util.obtenerAlmacenamientoSecundario();
			rutaAlmacenamiento = rutaAlmacenamiento
					.concat(getString(R.string.carpeta_base_de_datos));
			// File path =
			// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File path = new File(rutaAlmacenamiento);
			Log.d("AutenticadorAndroidProject:",
					"ExternalStoragePublicDirectory: " + path.getPath());

			Log.d("AutenticadorAndroidProject:", "readFile:begin: "
					+ new Date());
			/*
			 * CensoFileReaderWriterUtil.processCensoFile(censo, censoDao, path
			 * .getPath().concat(File.separator).concat("Censo2015.txt"));
			 */
//			CensoFileReaderWriterUtil.processCensoFile(
//					censo,
//					censoDao,
//					templatesDao,
//					path.getPath().concat(File.separator)
//							.concat("Censo_127000.txt"),
//					path.getPath().concat(File.separator).concat("0.pkc"),
//					keyczar);
//			Log.d("AutenticadorAndroidProject:", "readFile:end: " + new Date());

			/*
			 * Log.d("AutenticadorAndroidProject:",
			 * "processTemplatesFiles:begin: " + new Date());
			 * TemplateFileReaderWriterUtil.processTemplatesFiles(template,
			 * templatesDao,
			 * path.getPath().concat(File.separator).concat("Templates"
			 * ).concat(File.separator)); Log.d("AutenticadorAndroidProject:",
			 * "processTemplatesFiles:end: " + new Date());
			 */

			Log.d("AutenticadorAndroidProject:", "copyDataBase:begin: "
					+ new Date());
			BackupUtil
					.copyDataBase(
							"/data/data/carvajal.autenticador.android.activity/databases",
							"autenticador-db",
							path.getPath().concat(File.separator));
			Log.d("AutenticadorAndroidProject:", "copyDataBase:end: "
					+ new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
