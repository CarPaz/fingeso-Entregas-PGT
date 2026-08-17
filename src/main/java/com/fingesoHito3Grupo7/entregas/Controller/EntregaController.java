package com.fingesoHito3Grupo7.entregas.controller;

import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.nio.charset.StandardCharsets;

/*
 * Controlador encargado de recibir las peticiones HTTP
 * relacionadas con las entregas.
 */
@RestController
@RequestMapping("/api/entregas")
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
    public List<EntregaResponseDTO> obtenerEntregas(Authentication autenticacion) {
        return entregaService.obtenerEntregasAutorizadas(
                autenticacion.getName(),
                obtenerRol(autenticacion)
        );
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
            @RequestPart("archivo") MultipartFile archivo,
            Authentication autenticacion
    ) {
        EntregaResponseDTO nuevaEntrega =
                entregaService.crearEntrega(
                        entregaDTO,
                        archivo,
                        autenticacion.getName()
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
            @RequestPart("archivo") MultipartFile archivo,
            Authentication autenticacion
    ) {
        EntregaResponseDTO nuevaEntrega =
                entregaService.crearEntregaFinal(
                        entregaDTO,
                        archivo,
                        autenticacion.getName()
                );

        /*
         * HTTP 201 indica que la entrega final fue creada correctamente.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaEntrega);
    }

    /*
     * Descarga el PDF asociado a una entrega.
     * El servicio verifica que el archivo corresponda al usuario autenticado.
     */
    @GetMapping("/{idEntrega}/archivo")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable Long idEntrega,
            Authentication autenticacion
    ) {
        EntregaService.ArchivoEntrega archivo =
                entregaService.obtenerArchivoAutorizado(
                        idEntrega,
                        autenticacion.getName(),
                        obtenerRol(autenticacion)
                );

        ContentDisposition disposicion = ContentDisposition
                .attachment()
                .filename(archivo.nombreOriginal(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .body(archivo.recurso());
    }

    private String obtenerRol(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .map(autoridad -> autoridad.getAuthority())
                .filter(autoridad -> autoridad.startsWith("ROLE_"))
                .map(autoridad -> autoridad.substring("ROLE_".length()))
                .findFirst()
                .orElse("");
    }
}
