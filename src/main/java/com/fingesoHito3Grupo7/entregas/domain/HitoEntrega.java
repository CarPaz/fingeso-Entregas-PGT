package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hito_entrega")
public class HitoEntrega {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hito_entrega", nullable = false)
    private Long idHitoEntrega;


    @Column(name = "nombre",length = 100, nullable = false)
    private String nombre;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    //formato pdf ?
    @Column(name ="formato", nullable = false)
    private String formato;

    // no estoy seguro si tenemos que avisar al ususario si se acerca un hito
    // ejemplos Atrasado Enviado Pendiente
    @Column(name = "estado", nullable = false)
    private String estado;


}
