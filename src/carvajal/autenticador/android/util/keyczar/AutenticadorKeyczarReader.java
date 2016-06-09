package carvajal.autenticador.android.util.keyczar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.keyczar.KeyMetadata;
import org.keyczar.exceptions.KeyczarException;
import org.keyczar.interfaces.KeyczarReader;

import android.content.res.AssetManager;
import android.content.res.Resources;

public class AutenticadorKeyczarReader implements KeyczarReader {

	private final static String META = "meta";
	private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	private final AssetManager mAssetManager;
	private final String mSubDirectory;

	public AutenticadorKeyczarReader(final Resources resources, final String assetSubDirectory) {
		mAssetManager = resources.getAssets();
		mSubDirectory = assetSubDirectory;
	}

	public String getKey() throws KeyczarException {
		final KeyMetadata metadata = KeyMetadata.read(getMetadata());
		final String keyName = String.valueOf(metadata.getPrimaryVersion().getVersionNumber());
		return getFileContentAsString(keyName);
	}

	public String getKey(final int keyVersion) throws KeyczarException {
		return getFileContentAsString(String.valueOf(keyVersion));
	}

	public String getMetadata() throws KeyczarException {
		return getFileContentAsString(META);
	}

	private String getFileContentAsString(final String filename) throws KeyczarException {
		try {
			final byte[] buf = new byte[1024];
			final StringBuilder stringBuilder = new StringBuilder();
			final InputStream inputStream = mAssetManager.open(getFullFilename(filename));
			int numRead;
			while ((numRead = inputStream.read(buf)) > 0) {
				stringBuilder.append(new String(buf, 0, numRead, CHARSET_UTF8));
			}
			return stringBuilder.toString();
		}
		catch (IOException e) {
			throw new KeyczarException("Couldn't read Keyczar 'meta' file from assets/", e);
		}
	}

	private String getFullFilename(final String filename) {
		if (mSubDirectory == null) {
			return filename;
		}
		return mSubDirectory + File.separator + filename;
	}
}
