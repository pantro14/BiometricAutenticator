package carvajal.autenticador.android.util.wrappers;

import java.util.Date;

/**
 * Clase encargada del transporte de datos para ser devueltos por el servidor.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since   10 de Abril de 2015
 */
public class NovSyncWrapper {
	
	//	Atributos propios del transporte de datos
	private String androidId;
	private String cedula;
	private Date fechaNovedad;
	private int score;
	private int templateHit;
	private int tipoElector;
	private int tipoNovedad;
	private int respuesta;
	private String codMesa;

	//	Constructor sin par�metros	
	public NovSyncWrapper() {
		super();
	}
	
	/**
	 * 
	 * Constructor con los parametros para instanciar la clase
	 * 
	 * @param androidId Direcci�n MAC del dipositivo m�vil.
	 * @param cedula C�dula del elector.
	 * @param fechaNovedad Fecha de creaci�n de la novedad en el m�vil.
	 * @param score Valor n�merico del score.
	 * @param templateHit Valor n�merico del hit.
	 * @param tipoElector tipo de elector.
	 * @param tipoNovedad tipo de novedad.
	 * @param respuesta respuesta de la petici�n al servidor.
	 * @param codMesa c�digo de la mesa de votaci�n.
	 */
	public NovSyncWrapper(String androidId, String cedula, Date fechaNovedad,
			int score, int templateHit, int tipoElector, int tipoNovedad,
			int respuesta, String codMesa) {
		super();
		this.androidId = androidId;
		this.cedula = cedula;
		this.fechaNovedad = fechaNovedad;
		this.score = score;
		this.templateHit = templateHit;
		this.tipoElector = tipoElector;
		this.tipoNovedad = tipoNovedad;
		this.respuesta = respuesta;
		this.codMesa = codMesa;
	}

	/*
	 * Getters y Setters de los par�metros globales
	 */
	
	public String getandroidId() {
		return androidId;
	}

	public void setandroidId(String androidId) {
		this.androidId = androidId;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public Date getFechaNovedad() {
		return fechaNovedad;
	}

	public void setFechaNovedad(Date fechaNovedad) {
		this.fechaNovedad = fechaNovedad;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTemplateHit() {
		return templateHit;
	}

	public void setTemplateHit(int templateHit) {
		this.templateHit = templateHit;
	}

	public int getTipoElector() {
		return tipoElector;
	}

	public void setTipoElector(int tipoElector) {
		this.tipoElector = tipoElector;
	}

	public int getTipoNovedad() {
		return tipoNovedad;
	}

	public void setTipoNovedad(int tipNovedad) {
		this.tipoNovedad = tipNovedad;
	}

	public int getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}

	public String getCodMesa() {
		return codMesa;
	}

	public void setCodMesa(String codMesa) {
		this.codMesa = codMesa;
	}

}
