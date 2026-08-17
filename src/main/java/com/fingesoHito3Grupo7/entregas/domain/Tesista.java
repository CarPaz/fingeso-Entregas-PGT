package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TESISTA")
public class Tesista extends Usuario {

    public Tesista() { 
        super(); // Llamada al constructor de la clase base Usuario
    }
}