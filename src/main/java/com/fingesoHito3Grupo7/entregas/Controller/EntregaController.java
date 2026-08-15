package com.fingesoHito3Grupo7.entregas.Controller;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Este archivo es para recibir las  peticiones HTTP que lleguen desde el front


//Se le dice a spring boot que esta clase va a recibir peticiones web
@RestController
//Para definir la ruta URL base
@RequestMapping("/api/entregas")
//Para conexion con el front
@CrossOrigin("*") 
public class EntregaController {
    //coneccion a la base de datos 
    private final EntregaRepository entregaRepository;

    EntregaController(EntregaRepository entregaRepository) {
        this.entregaRepository = entregaRepository;
    }

    // Obtener todas las entregas
    @GetMapping
    public List<Entrega> obtenerEntregas() {
        return entregaRepository.findAll();
    }

    // Crear una nueva entrega
    @PostMapping
    public ResponseEntity<Entrega> crearEntrega(@RequestBody Entrega entrega) {
        Entrega nuevaEntrega = entregaRepository.save(entrega);
        return ResponseEntity.ok(nuevaEntrega);
    }
}
