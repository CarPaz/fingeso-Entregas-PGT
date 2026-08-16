package com.fingesoHito3Grupo7.entregas.dto;

public class EntregaDTO {
    
    private Long idProcesoTesis;
    private Long idHitoEntrega;

    private String tipoEntrega;
    private Long idEstudiante;

    // Constructor vacio
    public EntregaDTO() {
    }

    // Getters y Setters
    public Long getIdProcesoTesis() { return idProcesoTesis; }
    public void setIdProcesoTesis(Long idProcesoTesis) { this.idProcesoTesis = idProcesoTesis; }

    public Long getIdHitoEntrega() { return idHitoEntrega; }
    public void setIdHitoEntrega(Long idHitoEntrega) { this.idHitoEntrega = idHitoEntrega; }

    // 2. Asegúrate de tener estos Getters y Setters
    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }
        
}