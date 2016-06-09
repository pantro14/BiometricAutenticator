package carvajal.autenticador.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class BackupUtil {

	public static synchronized void copyDataBase(final String dbDirectoryPath,
			final String dbFileName, final String targetFilePath)
			throws Exception {
		try {
			final File dbDirectory = new File(dbDirectoryPath);
			if (dbDirectory.exists() && dbDirectory.isDirectory()) {
				final File dbFile = new File(dbDirectoryPath, dbFileName);
				if (dbFile.exists() && dbFile.isFile()) {
					final File target = new File(targetFilePath.concat(
							dbFileName).concat(".sqlite"));
					if (target.exists()) {
						target.delete();
					}
					target.createNewFile();
					final InputStream inputStream = new FileInputStream(dbFile);
					final OutputStream outputStream = new FileOutputStream(
							target);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, len);
					}
					inputStream.close();
					outputStream.close();
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage().toString(), e);
		}
	}

	/**
	 * Copia y pega un archivo de la ruta origen a la ruta destino renombrandolo
	 * y verificando la existencia de la carpeta destino.
	 * 
	 * @param directoryPath
	 *            ruta de la carpeta del archivo origen
	 * @param fileName
	 *            nombre del archivo origen
	 * @param targetDirectoryPath
	 *            ruta de la carpeta del archivo destino
	 * @param targetFilePath
	 *            nombre del archivo destino
	 * @throws Exception
	 */
	public static synchronized void copyFile(final String directoryPath,
			final String fileName, final String targetDirectoryPath,
			final String targetFilePath) throws Exception {
		try {
			final File dbDirectory = new File(directoryPath);
			final File targetDirectory = new File(targetDirectoryPath);

			if (!targetDirectory.exists()) {
				targetDirectory.mkdirs();
			}

			if (dbDirectory.exists() && dbDirectory.isDirectory()) {
				final File file = new File(directoryPath, fileName);
				if (file.exists() && file.isFile()) {
					final File target = new File(targetDirectoryPath,
							targetFilePath);
					if (target.exists()) {
						target.delete();
					}
					target.createNewFile();
					final InputStream inputStream = new FileInputStream(file);
					final OutputStream outputStream = new FileOutputStream(
							target);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, len);
					}
					inputStream.close();
					outputStream.close();
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage().toString(), e);
		}
	}

}
