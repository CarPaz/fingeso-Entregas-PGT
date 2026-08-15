package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntregaService {
    //conexion con base de datos
    private final EntregaRepository entregaRepository;

    // Constructor para inyectar el repositorio
    public EntregaService(EntregaRepository entregaRepository) {
        this.entregaRepository = entregaRepository;
    }

    // obtener todas las entregas
    public List<Entrega> obtenerTodasLasEntregas() {
        return entregaRepository.findAll();
    }

    // crear una nueva entrega
    public Entrega crearEntrega(Entrega entrega) {
        return entregaRepository.save(entrega);
    }
}