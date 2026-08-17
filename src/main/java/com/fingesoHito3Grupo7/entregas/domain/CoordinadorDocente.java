package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COORDINADOR")
public class CoordinadorDocente extends Usuario {
    public CoordinadorDocente() { super(); }
}