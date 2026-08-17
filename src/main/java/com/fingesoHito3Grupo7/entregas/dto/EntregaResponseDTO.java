package com.fingesoHito3Grupo7.entregas.dto;

import java.time.LocalDateTime;

public class EntregaResponseDTO {

    private Long idEntrega;
    private Long idProcesoTesis;
    private Long idHitoEntrega;
    private Long idEstudiante;

    private String tipoEntrega;
    private LocalDateTime fechaHora;
    private String estado;
    private Integer numeroVersion;

    private String nombreOriginal;
    private String nombreAlmacenado;
    private String mimeType;
    private Long tamanoBytes;
    private String rutaRelativaArchivo;

    // |GETTERS & SETTERS|
    public EntregaResponseDTO() {
    }

    public Long getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Long idEntrega) {
        this.idEntrega = idEntrega;
    }

    public Long getIdProcesoTesis() {
        return idProcesoTesis;
    }

    public void setIdProcesoTesis(Long idProcesoTesis) {
        this.idProcesoTesis = idProcesoTesis;
    }

    public Long getIdHitoEntrega() {
        return idHitoEntrega;
    }

    public void setIdHitoEntrega(Long idHitoEntrega) {
        this.idHitoEntrega = idHitoEntrega;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getNombreAlmacenado() {
        return nombreAlmacenado;
    }

    public void setNombreAlmacenado(String nombreAlmacenado) {
        this.nombreAlmacenado = nombreAlmacenado;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getRutaRelativaArchivo() {
        return rutaRelativaArchivo;
    }

    public void setRutaRelativaArchivo(String rutaRelativaArchivo) {
        this.rutaRelativaArchivo = rutaRelativaArchivo;
    }
}