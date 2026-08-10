package com.fingesoHito3Grupo7.entregas.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ProcesoTesis")
public class ProcesoTesisDomain {


    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_procesoT")
    private Long idProcesoT;


    @Column(name = "estado" , length = 50)
    private  String estado;


    @Column(name = "etapa_actual", length = 50)
    private String etapaActual;

    @Column(name =  "calificacion_final")
    private Double calificacionFinal;

    

}
