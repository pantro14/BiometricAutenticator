package com.imobile.thermalprinterwifiswitch;

import android.content.res.Resources;
import carvajal.autenticador.android.activity.R;

/**
 * Clase que implementa la libreria en C imobileJNI para el encendido y apagado
 * de los dispositivos
 * 
 * @author grasotos
 * 
 */
public class EncendidoApagadoDispositivos {

	/**
	 * Estado de la impresora 0: Encendido 1: Apagado
	 * 
	 * @author grasotos
	 */
	private static int estado_impresora;

	/**
	 * Estado de la huellero 2: Encendido 3: Apagado
	 * 
	 * @author grasotos
	 */
	private static int estado_huellero;

	/**
	 * Llamado al procedimiento de C
	 * 
	 * @author grasotos
	 */
	private String llamadoImobile;

	/**
	 * Estado impresora encendida
	 * 
	 * @author grasotos
	 */
	private String impresoraOn;

	/**
	 * Estado impresora apagada
	 * 
	 * @author grasotos
	 */
	private String impresoraOff;

	/**
	 * Estado huellero encendido
	 * 
	 * @author grasotos
	 */
	private String huelleroOn;

	/**
	 * Estado huellero apagado
	 * 
	 * @author grasotos
	 */
	private String huelleroOff;

	/**
	 * Encendido u apagado exitoso
	 * 
	 * @author grasotos
	 */
	private String exitoso;

	/**
	 * Contructor de la clase, se inicializan las variables y se apagan los
	 * dispositivos.
	 * 
	 * @param recursos
	 *            para obtener los Strings a usar.
	 * @author grasotos
	 */
	public EncendidoApagadoDispositivos(Resources recursos) {

		estado_impresora = Integer.valueOf(recursos.getString(R.string.impresora_off));
		estado_huellero = Integer.valueOf(recursos.getString(R.string.huellero_off));

		llamadoImobile = recursos.getString(R.string.llamado_imobileJNI);
		impresoraOn = recursos.getString(R.string.impresora_on);
		impresoraOff = recursos.getString(R.string.impresora_off);
		huelleroOn = recursos.getString(R.string.huellero_on);
		huelleroOff = recursos.getString(R.string.huellero_off);

		exitoso = recursos.getString(R.string.exitoso);

		apagarDispositivosInicio();
		administrarDispositivos();
	}

	/**
	 * Permite inicializar los dispositivos en apagado.
	 * 1: Impresora 3: Huellero
	 * 
	 * @author grasotos
	 */
	public void apagarDispositivosInicio() {

		if (estado_impresora == Integer.valueOf(impresoraOff)) {
			imobileJNI.WriteProc(llamadoImobile, impresoraOff);
		}

		if (estado_huellero == Integer.valueOf(huelleroOff)) {
			imobileJNI.WriteProc(llamadoImobile, huelleroOff);
		}

	}

	/**
	 * Permite administrar encendido y apagado de dispositivos.
	 * 
	 * @author grasotos
	 */
	public void administrarDispositivos() {
		administrarImpresora();
		administrarHuellero();
	}

	/**
	 * Permite desconectar los dispositivos.
	 * 
	 * @author grasotos
	 */
	public void desconectar() {
		administrarDispositivos();
	}

	/**
	 * Permite encender y apagar la impresora
	 * 
	 * @author grasotos
	 */
	public void administrarImpresora() {

		String estadoTmpImpresora = "";

		if (estado_impresora == Integer.valueOf(impresoraOn)) {
			estadoTmpImpresora = imobileJNI.WriteProc(llamadoImobile, impresoraOff);

			if (estadoTmpImpresora.equals(exitoso)) {
				estado_impresora = Integer.valueOf(impresoraOff);
			}
		} else {
			estadoTmpImpresora = imobileJNI.WriteProc(llamadoImobile, impresoraOn);

			if (estadoTmpImpresora.equals(exitoso)) {
				estado_impresora = Integer.valueOf(impresoraOn);
			}
		}
	}

	/**
	 * Permite encender y apagar el huellero
	 * 
	 * @author grasotos
	 */
	public void administrarHuellero() {

		String estadoTmpHuellero = "";

		if (estado_huellero == Integer.valueOf(huelleroOn)) {

			estadoTmpHuellero = imobileJNI.WriteProc(llamadoImobile, huelleroOff);
			if (estadoTmpHuellero.equals(exitoso)) {
				estado_huellero = Integer.valueOf(huelleroOff);
			}

		} else {
			estadoTmpHuellero = imobileJNI.WriteProc(llamadoImobile, huelleroOn);
			if (estadoTmpHuellero.equals(exitoso)) {
				estado_huellero = Integer.valueOf(huelleroOn);
			}

		}
	}

}
