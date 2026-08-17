package com.fingesoHito3Grupo7.entregas.dto;

import java.util.List;

/*
 * Proceso de tesis disponible para el usuario autenticado junto con sus
 * hitos. Este DTO permite construir listas desplegables sin pedir IDs.
 */
public class OpcionProcesoTesisDTO {
    private final Long idProcesoTesis;
    private final String tema;
    private final String estado;
    private final List<OpcionHitoEntregaDTO> hitos;

    public OpcionProcesoTesisDTO(
            Long idProcesoTesis,
            String tema,
            String estado,
            List<OpcionHitoEntregaDTO> hitos
    ) {
        this.idProcesoTesis = idProcesoTesis;
        this.tema = tema;
        this.estado = estado;
        this.hitos = hitos;
    }

    public Long getIdProcesoTesis() {
        return idProcesoTesis;
    }

    public String getTema() {
        return tema;
    }

    public String getEstado() {
        return estado;
    }

    public List<OpcionHitoEntregaDTO> getHitos() {
        return hitos;
    }
}
