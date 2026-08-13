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
    @Column(name = "id_proceso_tesis", nullable = false)
    private Long idProcesoTesis;


    @Column(name = "estado", nullable = false , length = 50)
    private  String estado;


    @Column(name = "etapa_actual", nullable = false, length = 50)
    private String etapaActual;

    @Column(name =  "calificacion_final")
    private Double calificacionFinal;

    //relaciones con tablas
    //puede estar relacionado con mas de una entrega
    @OneToMany(mappedBy = "procesoTesis", cascade = CascadeType.ALL, orphanRemoval = true)// tener cuidado con cascade!! si se borra un proceso se borraran sus entregas en la bd
    private List<Entrega> entregas = new ArrayList<>(); //lista de entregas 

    //puede estar relacionado con mas de un hito
    @OneToMany(mappedBy = "procesoTesis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HitoEntrega> hitos = new ArrayList<>();

    // Constructor vacio
    public ProcesoTesis() {
    }
    //getters y setters 
    public Long getIdProcesoTesis() {
        return idProcesoTesis;
    }

    public void setIdProcesoTesis(Long idProcesoTesis) {
        this.idProcesoTesis = idProcesoTesis;
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
