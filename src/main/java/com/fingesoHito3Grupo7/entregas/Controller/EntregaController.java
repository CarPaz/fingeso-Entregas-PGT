package com.fingesoHito3Grupo7.entregas.controller;

import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * Controlador encargado de recibir las peticiones HTTP
 * relacionadas con las entregas.
 */
@RestController
@RequestMapping("/api/entregas")
@CrossOrigin("*")
public class EntregaController {

    /*
     * Servicio que contiene las reglas de negocio.
     * El controlador solamente recibe y devuelve información.
     */
    private final EntregaService entregaService;

    /*
     * Spring inyecta automáticamente EntregaService.
     */
    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    /*
     * Obtiene todas las entregas registradas.
     *
     * Ruta:
     * GET /api/entregas
     */
    @GetMapping
    public List<EntregaResponseDTO> obtenerEntregas() {
        return entregaService.obtenerTodasLasEntregas();
    }

    /*
     * Registra una nueva entrega de avance.
     *
     * Se mantienen temporalmente dos rutas:
     *
     * POST /api/entregas
     * POST /api/entregas/avance
     *
     * La primera conserva compatibilidad con el frontend existente.
     * La segunda expresa claramente que esta operación registra un avance.
     */
    @PostMapping(
            value = {"", "/avance"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EntregaResponseDTO> crearEntregaAvance(
            /*
             * Parte JSON con los identificadores del proceso,
             * hito y estudiante.
             */
            @RequestPart("entrega") EntregaDTO entregaDTO,

            /*
             * Archivo PDF obligatorio.
             */
            @RequestPart("archivo") MultipartFile archivo
    ) {
        EntregaResponseDTO nuevaEntrega =
                entregaService.crearEntrega(entregaDTO, archivo);

        /*
         * HTTP 201 indica que el registro fue creado correctamente.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaEntrega);
    }
}