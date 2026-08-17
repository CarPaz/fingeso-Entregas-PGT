package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proceso_tesis")
public class ProcesoTesis {

    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proceso_tesis")
    private Long idProcesoTesis;

    // Tema acordado para identificar el trabajo de tesis.
    @Column(name = "tema", nullable = false, length = 255)
    private String tema;

    // Estado general del proceso de tesis.
    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    // Etapa actual en la que se encuentra el proceso.
    @Column(name = "etapa_actual", nullable = false, length = 50)
    private String etapaActual;

    /*
     * Puede permanecer sin valor mientras el proceso de tesis
     * no tenga una calificación final.
     */
    @Column(name = "calificacion_final")
    private Double calificacionFinal;

    /*
     * Profesor responsable de acompañar el proceso de tesis.
     *
     * La relación permanece opcional mientras se actualizan los procesos
     * existentes. Cuando está disponible, su correo institucional se utiliza
     * como destinatario de las notificaciones de nuevas entregas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor")
    private Profesor profesor;

    /*
     * Tesista propietario del proceso.
     * La columna admite nulos solamente para permitir que instalaciones
     * existentes completen sus datos antes de exigir la restricción en BD.
     * El servicio de entregas rechaza procesos que no tengan tesista.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tesista")
    private Tesista tesista;

    /*
     * Un proceso de tesis puede tener varias entregas.
     * mappedBy corresponde al atributo procesoTesis de Entrega.java.
     *
     * No utilizamos CascadeType.ALL ni orphanRemoval para evitar
     * que al eliminar un proceso se borren automáticamente sus entregas.
     */
    @OneToMany(mappedBy = "procesoTesis")
    private List<Entrega> entregas = new ArrayList<>();

    /*
     * Un proceso de tesis puede tener varios hitos.
     * mappedBy corresponde al atributo procesoTesis de HitoEntrega.java.
     */
    @OneToMany(mappedBy = "procesoTesis")
    private List<HitoEntrega> hitos = new ArrayList<>();

    // Constructor vacío requerido por JPA.
    public ProcesoTesis() {
    }

	// |GETTERS & SETTERS|
    public Long getIdProcesoTesis() {
        return idProcesoTesis;
    }

    public void setIdProcesoTesis(Long idProcesoTesis) {
        this.idProcesoTesis = idProcesoTesis;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEtapaActual() {
        return etapaActual;
    }

    public void setEtapaActual(String etapaActual) {
        this.etapaActual = etapaActual;
    }

    public Double getCalificacionFinal() {
        return calificacionFinal;
    }

    public void setCalificacionFinal(Double calificacionFinal) {
        this.calificacionFinal = calificacionFinal;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Tesista getTesista() {
        return tesista;
    }

    public void setTesista(Tesista tesista) {
        this.tesista = tesista;
    }

    public List<Entrega> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<Entrega> entregas) {
        this.entregas = entregas;
    }

    public List<HitoEntrega> getHitos() {
        return hitos;
    }

    public void setHitos(List<HitoEntrega> hitos) {
        this.hitos = hitos;
    }
}
