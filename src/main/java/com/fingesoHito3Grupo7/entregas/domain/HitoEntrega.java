package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hito_entrega")
public class HitoEntrega {

    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hito_entrega")
    private Long idHitoEntrega;

    // Nombre descriptivo del hito.
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Fecha y hora límite para realizar la entrega.
    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    // Formato permitido para el documento, por ejemplo: PDF.
    @Column(name = "formato", nullable = false, length = 30)
    private String formato;

    // Ejemplos: PENDIENTE, ABIERTO, CERRADO o ATRASADO.
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    /*
     * Un hito puede recibir varias entregas.
     * mappedBy apunta al atributo hitoEntrega de Entrega.java.
     *
     * No se utiliza CascadeType.ALL ni orphanRemoval para evitar que
     * una eliminación del hito borre las entregas accidentalmente.
     */
    @OneToMany(mappedBy = "hitoEntrega")
    private List<Entrega> entregas = new ArrayList<>();

    /*
     * Relación:
     * hito_entrega.id_proceso_tesis -> proceso_tesis.id_proceso_tesis
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_proceso_tesis",
        referencedColumnName = "id_proceso_tesis",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_hito_proceso_tesis")
    )
    private ProcesoTesis procesoTesis;

    // Constructor vacío requerido por JPA.
    public HitoEntrega() {
    }

	// |GETTERS & SETTERS|
    public Long getIdHitoEntrega() {
        return idHitoEntrega;
    }

    public void setIdHitoEntrega(Long idHitoEntrega) {
        this.idHitoEntrega = idHitoEntrega;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Entrega> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<Entrega> entregas) {
        this.entregas = entregas;
    }

    public ProcesoTesis getProcesoTesis() {
        return procesoTesis;
    }

    public void setProcesoTesis(ProcesoTesis procesoTesis) {
        this.procesoTesis = procesoTesis;
    }
}