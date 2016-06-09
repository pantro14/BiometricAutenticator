package carvajal.autenticador.android.bl;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.bl.exception.AutenticacionBLException;
import android.content.Context;

public class AutenticacionBL {

	private Context context;

	public AutenticacionBL(Context context) {
		this.context = context;
	}

	/**
	 * Valida la clave dada con la configurada en el sistema.
	 * 
	 * @param clave
	 * @return true si la clave dada coincide con la configurada en el sistema.
	 */
	public boolean validarDelegado(String clave)
			throws AutenticacionBLException {
		try {
			// valida que la clave no sea nula y tenga una longitud mayor a 0
			if (clave != null && clave.length() > 0) {
				String usuario = context.getString(R.string.usuario_supervisor);
				return clave.equals(usuario);
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AutenticacionBLException(e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Valida el usuario y clave con los configurados en el sistema.
	 * 
	 * @param usuario
	 * @param clave
	 * @return <b>Operario</b> si los datos ingresados corresponden a los
	 *         configurados para Operario. <br/>
	 *         <b>Supervisor</b> si los datos ingresados corresponden a los
	 *         configurados para Supervisor. <br/>
	 *         <b>null</b> en caso contrario
	 */
	public String validarLogin(String usuario, String clave)
			throws AutenticacionBLException {
		try {
			String salida = null;
			// valida que la clave y usuario no sean nulas y tengan una longitud
			// mayor a 0
			if (usuario != null && usuario.length() > 0 && clave != null
					&& clave.length() > 0) {
				String usuarioSupervisor = context
						.getString(R.string.usuario_supervisor);
				String claveSupervisor = context
						.getString(R.string.usuario_supervisor);

				String usuarioOperario = context
						.getString(R.string.usuario_operario);
				String claveOperario = context
						.getString(R.string.usuario_operario);

				if (usuarioSupervisor.equals(usuario)
						&& claveSupervisor.equals(clave)) {
					salida = context.getString(R.string.rol_supervisor);
				} else if (usuarioOperario.equals(usuario)
						&& claveOperario.equals(clave)) {
					salida = context.getString(R.string.rol_operario);
				}
			}

			return salida;
		} catch (Exception e) {
			throw new AutenticacionBLException(e.getLocalizedMessage(), e);
		}

	}

}
