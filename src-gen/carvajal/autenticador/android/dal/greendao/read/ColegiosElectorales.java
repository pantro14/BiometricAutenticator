package carvajal.autenticador.android.dal.greendao.read;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table COLEGIOS_ELECTORALES.
 */
public class ColegiosElectorales {

    private Long id;
    /** Not-null value. */
    private String CodProv;
    /** Not-null value. */
    private String CodMpio;
    /** Not-null value. */
    private String CodZona;
    /** Not-null value. */
    private String CodColElec;
    /** Not-null value. */
    private String NomColElec;

    public ColegiosElectorales() {
    }

    public ColegiosElectorales(Long id) {
        this.id = id;
    }

    public ColegiosElectorales(Long id, String CodProv, String CodMpio, String CodZona, String CodColElec, String NomColElec) {
        this.id = id;
        this.CodProv = CodProv;
        this.CodMpio = CodMpio;
        this.CodZona = CodZona;
        this.CodColElec = CodColElec;
        this.NomColElec = NomColElec;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getCodProv() {
        return CodProv;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCodProv(String CodProv) {
        this.CodProv = CodProv;
    }

    /** Not-null value. */
    public String getCodMpio() {
        return CodMpio;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCodMpio(String CodMpio) {
        this.CodMpio = CodMpio;
    }

    /** Not-null value. */
    public String getCodZona() {
        return CodZona;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCodZona(String CodZona) {
        this.CodZona = CodZona;
    }

    /** Not-null value. */
    public String getCodColElec() {
        return CodColElec;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCodColElec(String CodColElec) {
        this.CodColElec = CodColElec;
    }

    /** Not-null value. */
    public String getNomColElec() {
        return NomColElec;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNomColElec(String NomColElec) {
        this.NomColElec = NomColElec;
    }

}