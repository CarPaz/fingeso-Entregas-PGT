package com.fingesoHito3Grupo7.entregas.repository;

import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TesistaRepository extends JpaRepository<Tesista, Long> {
    Optional<Tesista> findByCorreoInstitucionalIgnoreCase(String correoInstitucional);
}
