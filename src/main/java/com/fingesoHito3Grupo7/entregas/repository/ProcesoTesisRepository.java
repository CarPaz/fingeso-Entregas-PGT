package com.fingesoHito3Grupo7.entregas.repository;

import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcesoTesisRepository extends JpaRepository<ProcesoTesis, Long> {
    /*
     * Recupera únicamente los procesos pertenecientes al correo obtenido
     * desde el JWT. El frontend nunca decide qué procesos puede utilizar.
     */
    List<ProcesoTesis> findByTesistaCorreoInstitucionalIgnoreCaseOrderByTemaAsc(
            String correoInstitucional
    );
}
