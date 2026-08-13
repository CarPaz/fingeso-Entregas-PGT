package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "registro_entrega")
public class RegistroEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_entrega", nullable = false)
    private Long idRegistroEntrega;

    


}
