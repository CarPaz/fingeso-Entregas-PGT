package com.fingesoHito3Grupo7.entregas.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proceso_tesis")
public class ProcesoTesis {


    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proceso_tesis", nullable = false)
    private Long idProcesoT;


    @Column(name = "estado", nullable = false , length = 50)
    private  String estado;


    @Column(name = "etapa_actual", nullable = false, length = 50)
    private String etapaActual;

    @Column(name =  "calificacion_final")
    private Double calificacionFinal;

    

}
