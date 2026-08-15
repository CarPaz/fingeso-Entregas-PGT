package com.fingesoHito3Grupo7.entregas.controller;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
//Este archivo es para recibir las  peticiones HTTP que lleguen desde el front


//Se le dice a spring boot que esta clase va a recibir peticiones web
@RestController
//Para definir la ruta URL base
@RequestMapping("/api/entregas")
//Para conexion con el front
@CrossOrigin("*") 
public class entregaController {
    //coneccion a la base de datos 
    private final EntregaService entregaService;

    entregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    // Obtener todas las entregas
    @GetMapping
    public List<Entrega> obtenerEntregas() {
        return entregaService.obtenerTodasLasEntregas();
    }

    // Crear una nueva entrega
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Entrega> crearEntrega(
            @RequestPart("entrega") Entrega entrega,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        
        Entrega nuevaEntrega = entregaService.crearEntrega(entrega, archivo);
        return ResponseEntity.ok(nuevaEntrega);
    }
}
