package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones") // verificar si agregaremos esta entidad, si se agrega deberia tener id de destinatario, su correo y quizas el mensaje
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion", nullable = false)
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


