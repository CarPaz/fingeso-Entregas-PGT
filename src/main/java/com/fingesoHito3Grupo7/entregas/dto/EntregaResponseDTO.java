package com.fingesoHito3Grupo7.entregas.dto;

public class EntregaResponseDTO {
    
    private Long idEntrega; 
    private Long idProcesoTesis;
    private Long idHitoEntrega;
    private String nombreOriginal;
    private String nombreAlmacenado;
    private String mimeType;
    private Long tamanoBytes;
    private String rutaRelativaArchivo;

    public EntregaResponseDTO() {
    }

    //Getters y setters
    public Long getIdEntrega() { return idEntrega; }
    public void setIdEntrega(Long idEntrega) { this.idEntrega = idEntrega; }

    public Long getIdProcesoTesis() { return idProcesoTesis; }
    public void setIdProcesoTesis(Long idProcesoTesis) { this.idProcesoTesis = idProcesoTesis; }

    public Long getIdHitoEntrega() { return idHitoEntrega; }
    public void setIdHitoEntrega(Long idHitoEntrega) { this.idHitoEntrega = idHitoEntrega; }

    public String getNombreOriginal() { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }

    public String getNombreAlmacenado() { return nombreAlmacenado; }
    public void setNombreAlmacenado(String nombreAlmacenado) { this.nombreAlmacenado = nombreAlmacenado; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public String getRutaRelativaArchivo() { return rutaRelativaArchivo; }
    public void setRutaRelativaArchivo(String rutaRelativaArchivo) { this.rutaRelativaArchivo = rutaRelativaArchivo; }
}
