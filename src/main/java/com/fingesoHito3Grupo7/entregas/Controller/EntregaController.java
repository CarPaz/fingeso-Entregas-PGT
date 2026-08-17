package com.fingesoHito3Grupo7.entregas.Controller;

import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

/*
 * Controlador encargado de recibir las peticiones HTTP
 * relacionadas con las entregas.
 */
@RestController
@RequestMapping("/api/entregas")
@CrossOrigin("*")
public class Entregacontroller {

    /*
     * Servicio que contiene las reglas de negocio.
     * El controlador solamente recibe y devuelve información.
     */
    private final EntregaService entregaService;

    /*
     * Spring inyecta automáticamente EntregaService.
     */
    public Entregacontroller(EntregaService entregaService) {
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
                entregaService.crearEntrega(
                        entregaDTO,
                        archivo
                );

        /*
         * HTTP 201 indica que el registro fue creado correctamente.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaEntrega);
    }

    /*
     * Registra una nueva entrega final.
     *
     * POST /api/entregas/final
     *
     * El tipo FINAL no se recibe libremente desde el frontend.
     * El servicio lo asigna automáticamente para evitar datos incorrectos.
     */
    @PostMapping(
            value = "/final",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EntregaResponseDTO> crearEntregaFinal(
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
                entregaService.crearEntregaFinal(
                        entregaDTO,
                        archivo
                );

        /*
         * HTTP 201 indica que la entrega final fue creada correctamente.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaEntrega);
    }

    /*
     * Descarga o visualiza el archivo PDF de una entrega específica.
     *
     * GET /api/entregas/{id}/archivo
     */
    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable("id") Long idEntrega) {
        try {
            Path rutaArchivo = entregaService.obtenerRutaFisicaArchivo(idEntrega);
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + recurso.getFilename() + "\""
                    )
                    .body(recurso);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}