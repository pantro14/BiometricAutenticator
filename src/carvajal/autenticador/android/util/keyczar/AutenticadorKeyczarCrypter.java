package carvajal.autenticador.android.util.keyczar;

import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;

import android.content.res.Resources;
import carvajal.autenticador.android.util.keyczar.autenticador.AutenticadorKeyczarCrypterException;

public class AutenticadorKeyczarCrypter {

	private static AutenticadorKeyczarCrypter instance = null;
	private Crypter crypter = null;

	private AutenticadorKeyczarCrypter(final Resources resources)
			throws AutenticadorKeyczarCrypterException {
		try {
			crypter = new Crypter(new AutenticadorKeyczarReader(resources,
					"keys"));
		} catch (Exception e) {
			throw new AutenticadorKeyczarCrypterException(e
					.getLocalizedMessage().toString(), e);
		}
	}

	public static AutenticadorKeyczarCrypter getInstance(
			final Resources resources)
			throws AutenticadorKeyczarCrypterException {
		if (instance == null) {
			instance = new AutenticadorKeyczarCrypter(resources);
		}
		return instance;
	}

	public String encrypt(final String clearText)
			throws AutenticadorKeyczarCrypterException {
		String cipherText = null;
		try {
			if (clearText != null && clearText.trim().length() > 0) {
				cipherText = crypter.encrypt(clearText);
			} else {
				cipherText = clearText;
			}

		} catch (KeyczarException e) {
			throw new AutenticadorKeyczarCrypterException(e
					.getLocalizedMessage().toString(), e);
		} catch (Exception e) {
			throw new AutenticadorKeyczarCrypterException(e
					.getLocalizedMessage().toString(), e);
		}
		return cipherText;
	}

	public String decrypt(final String cipherText)
			throws AutenticadorKeyczarCrypterException {
		String clearText = null;
		try {

			if (cipherText != null && cipherText.trim().length() > 0) {
				clearText = crypter.decrypt(cipherText);
			} else {
				clearText = cipherText;
			}

		} catch (Exception e) {
			throw new AutenticadorKeyczarCrypterException(e
					.getLocalizedMessage().toString(), e);
		}
		return clearText;
	}
}
