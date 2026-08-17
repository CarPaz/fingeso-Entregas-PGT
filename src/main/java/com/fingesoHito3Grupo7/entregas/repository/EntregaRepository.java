package com.fingesoHito3Grupo7.entregas.repository;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.domain.HitoEntrega;
import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega>
            findTopByProcesoTesisAndHitoEntregaAndTipoEntregaOrderByNumeroVersionDesc(
                    ProcesoTesis procesoTesis,
                    HitoEntrega hitoEntrega,
                    String tipoEntrega
            );

    List<Entrega> findByEstudianteCorreoInstitucionalIgnoreCaseOrderByFechaHoraDesc(
            String correoInstitucional
    );

    List<Entrega> findByProcesoTesisProfesorCorreoInstitucionalIgnoreCaseOrderByFechaHoraDesc(
            String correoInstitucional
    );

    List<Entrega> findAllByOrderByFechaHoraDesc();
}
