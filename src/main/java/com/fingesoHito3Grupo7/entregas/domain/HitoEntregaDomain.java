package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hitoEntrega")
public class HitoEntregaDomain {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hito_entrega")
    private Long idHitoEntrega;


    @Column(name = "nombre",length = 100)
    private String nombre;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    //formato pdf ?
    @Column(name ="formato")
    private String formato;

    // no estoy seguro si tenemos que avisar al ususario si se acerca un hito
    // ejemplos Atrasado Enviado Pendiente
    @Column(name = "estado")
    private String estado;


}
