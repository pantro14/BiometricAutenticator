package carvajal.autenticador.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.util.exception.ZipUtilException;
import android.app.Activity;

public class ZipUtil {

	List<String> filesListInDir;

	private static String sourceFolder;
	Activity actividad;

	public ZipUtil(Activity actividad) {
		this.actividad = actividad;
		filesListInDir = new ArrayList<String>();
	}

	/**
	 * Comprime un directorio predefinido y lo renombra al valor del parámetro
	 * <b>nombreArchivo</b>
	 * 
	 * @param nombreArchivo
	 *            Nombre del archivo resultante
	 * @throws ZipUtilException
	 */
	public void comprimirDirectorio(String nombreArchivo)
			throws ZipUtilException {
		try {

			String rutaAlmacenamiento = Util.obtenerAlmacenamientoSecundario();
			sourceFolder = rutaAlmacenamiento.concat(actividad
					.getApplicationContext().getString(
							R.string.carpeta_temporal_backup));

			String carpetaBackup = rutaAlmacenamiento
					.concat(actividad.getApplicationContext().getString(
							R.string.carpeta_backup));

			String zipFile = carpetaBackup.concat(nombreArchivo);

			File sourceFile = new File(sourceFolder);
			File destFile = new File(carpetaBackup);
			
			if(!destFile.exists()){
				destFile.mkdirs();
			}

			populateFilesList(sourceFile);
			// now zip files one by one
			// create ZipOutputStream to write to the zip file
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String filePath : filesListInDir) {
				System.out.println("Zipping " + filePath);
				// for ZipEntry we need to keep only relative file path, so we
				// used substring on absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(sourceFile
						.getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				// read the file and write to ZipOutputStream
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			fos.close();
		} catch (Exception e) {
			throw new ZipUtilException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Genera una lista cons los archivos contenidos en la carpeta a crompimir
	 * 
	 * @param dir
	 */
	private void populateFilesList(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile())
				filesListInDir.add(file.getAbsolutePath());
			else
				populateFilesList(file);
		}

	}

}
