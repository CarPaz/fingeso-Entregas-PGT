package com.fingesoHito3Grupo7.entregas.dto;

import java.time.LocalDateTime;

/*
 * Información mínima de un hito que el frontend necesita para mostrarlo
 * como alternativa de selección. No se expone la entidad JPA completa.
 */
public class OpcionHitoEntregaDTO {
    private final Long idHitoEntrega;
    private final String nombre;
    private final LocalDateTime fechaLimite;
    private final String estado;

    public OpcionHitoEntregaDTO(
            Long idHitoEntrega,
            String nombre,
            LocalDateTime fechaLimite,
            String estado
    ) {
        this.idHitoEntrega = idHitoEntrega;
        this.nombre = nombre;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    public Long getIdHitoEntrega() {
        return idHitoEntrega;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public String getEstado() {
        return estado;
    }
}
