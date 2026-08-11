package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class NotificacionDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idHitoEntrega;

    //destinatario podria ser  Usuario no estoy seguro
    @Column(name = "destinatario")
    private String destinatario;

    //fecha envio
    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    //Ejemplos  Enviado , no enviado, pendiente
    @Column(name = "resultado_notificacion")
    private String resultado;

}
