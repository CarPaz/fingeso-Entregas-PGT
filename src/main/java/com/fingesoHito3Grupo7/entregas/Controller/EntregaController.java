package com.fingesoHito3Grupo7.entregas.controller;


import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;


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
    public List<EntregaResponseDTO> obtenerEntregas() {
        return entregaService.obtenerTodasLasEntregas();
    }

    // Crear una nueva entrega
    @PostMapping
    public ResponseEntity<EntregaResponseDTO> crearEntrega(
            @RequestPart("entrega") EntregaDTO entregaDTO,
            @RequestPart(value = "archivo") MultipartFile archivo) {
        
        // Guardamos el resultado en la variable del tipo correcto
        EntregaResponseDTO nuevaEntrega = entregaService.crearEntrega(entregaDTO, archivo);
        return ResponseEntity.ok(nuevaEntrega);
    }
}
