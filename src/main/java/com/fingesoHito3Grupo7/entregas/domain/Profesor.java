package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PROFESOR")
public class Profesor extends Usuario {
    public Profesor() { super(); }
}