package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.domain.HitoEntrega;
import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.HitoEntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.ProcesoTesisRepository;
import com.fingesoHito3Grupo7.entregas.repository.TesistaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * Servicio encargado de aplicar las reglas de negocio relacionadas
 * con el registro y consulta de entregas.
 */
@Service
public class EntregaService {

    /*
     * Estos valores son definidos por el servidor.
     * El cliente no decide el tipo ni el estado inicial de la entrega.
     */
    private static final String TIPO_AVANCE = "AVANCE";
    private static final String TIPO_FINAL = "FINAL";

    private static final String ESTADO_PENDIENTE_REVISION =
            "PENDIENTE_REVISION";

    /*
     * Repositorios utilizados para consultar y guardar información
     * en PostgreSQL.
     */
    private final EntregaRepository entregaRepository;
    private final ProcesoTesisRepository procesoTesisRepository;
    private final HitoEntregaRepository hitoEntregaRepository;
    private final TesistaRepository tesistaRepository;

    /*
     * Servicio responsable de guardar físicamente el archivo PDF.
     */
    private final FileStorageService fileStorageService;

    /*
     * Spring inyecta automáticamente todas estas dependencias
     * cuando crea el servicio.
     */
    public EntregaService(
            EntregaRepository entregaRepository,
            FileStorageService fileStorageService,
            ProcesoTesisRepository procesoTesisRepository,
            HitoEntregaRepository hitoEntregaRepository,
            TesistaRepository tesistaRepository
    ) {
        this.entregaRepository = entregaRepository;
        this.fileStorageService = fileStorageService;
        this.procesoTesisRepository = procesoTesisRepository;
        this.hitoEntregaRepository = hitoEntregaRepository;
        this.tesistaRepository = tesistaRepository;
    }

    /*
     * Convierte una entidad Entrega en un DTO de respuesta.
     *
     * Esto evita enviar directamente las entidades JPA al frontend
     * y permite controlar exactamente qué información se devuelve.
     */
    private EntregaResponseDTO convertirADTO(Entrega entrega) {
        EntregaResponseDTO dto = new EntregaResponseDTO();

        dto.setIdEntrega(entrega.getIdEntrega());

        /*
         * Se comprueba que las relaciones existan antes de acceder
         * a sus identificadores.
         */
        if (entrega.getProcesoTesis() != null) {
            dto.setIdProcesoTesis(
                    entrega.getProcesoTesis().getIdProcesoTesis()
            );
        }

        if (entrega.getHitoEntrega() != null) {
            dto.setIdHitoEntrega(
                    entrega.getHitoEntrega().getIdHitoEntrega()
            );
        }

        if (entrega.getEstudiante() != null) {
            dto.setIdEstudiante(
                    entrega.getEstudiante().getIdUsuario()
            );
        }

        /*
         * Información correspondiente al registro de la entrega.
         */
        dto.setTipoEntrega(entrega.getTipoEntrega());
        dto.setFechaHora(entrega.getFechaHora());
        dto.setEstado(entrega.getEstado());
        dto.setNumeroVersion(entrega.getNumeroVersion());

        /*
         * Metadatos del archivo PDF almacenado.
         */
        dto.setNombreOriginal(entrega.getNombreOriginal());
        dto.setNombreAlmacenado(entrega.getNombreAlmacenado());
        dto.setMimeType(entrega.getMimeType());
        dto.setTamanoBytes(entrega.getTamanoBytes());
        dto.setRutaRelativaArchivo(
                entrega.getRutaRelativaArchivo()
        );

        return dto;
    }

    /*
     * Registra una entrega de avance.
     *
     * Se conserva este nombre para mantener compatibilidad
     * con el controlador existente.
     *
     * @Transactional mantiene las operaciones de base de datos dentro
     * de una misma transacción. Si ocurre un error al guardar en PostgreSQL,
     * la operación de base de datos se revierte.
     */
    @Transactional
    public EntregaResponseDTO crearEntrega(
            EntregaDTO entregaDTO,
            MultipartFile archivo
    ) {
        return registrarEntrega(
                entregaDTO,
                archivo,
                TIPO_AVANCE
        );
    }

    /*
     * Registra una entrega final.
     *
     * Utiliza las mismas validaciones y reglas generales del avance,
     * pero almacena el tipo de entrega como FINAL.
     */
    @Transactional
    public EntregaResponseDTO crearEntregaFinal(
            EntregaDTO entregaDTO,
            MultipartFile archivo
    ) {
        return registrarEntrega(
                entregaDTO,
                archivo,
                TIPO_FINAL
        );
    }

    /*
     * Contiene las validaciones y operaciones compartidas
     * por las entregas de AVANCE y FINAL.
     */
    private EntregaResponseDTO registrarEntrega(
            EntregaDTO entregaDTO,
            MultipartFile archivo,
            String tipoEntrega
    ) {
        /*
         * El DTO contiene los identificadores enviados por el frontend.
         */
        if (entregaDTO == null) {
            throw new IllegalArgumentException(
                    "Los datos de la entrega son obligatorios."
            );
        }

        /*
         * Los tres identificadores son obligatorios antes de consultar
         * sus respectivos repositorios.
         */
        if (entregaDTO.getIdProcesoTesis() == null) {
            throw new IllegalArgumentException(
                    "El ID del proceso de tesis es obligatorio."
            );
        }

        if (entregaDTO.getIdHitoEntrega() == null) {
            throw new IllegalArgumentException(
                    "El ID del hito de entrega es obligatorio."
            );
        }

        if (entregaDTO.getIdEstudiante() == null) {
            throw new IllegalArgumentException(
                    "El ID del estudiante es obligatorio."
            );
        }

        /*
         * Se busca el proceso de tesis.
         * No se crea una entrega asociada a un proceso inexistente.
         */
        ProcesoTesis proceso = procesoTesisRepository
                .findById(entregaDTO.getIdProcesoTesis())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proceso de tesis no encontrado."
                ));

        /*
         * Se busca el hito seleccionado para la entrega.
         */
        HitoEntrega hito = hitoEntregaRepository
                .findById(entregaDTO.getIdHitoEntrega())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Hito de entrega no encontrado."
                ));

        /*
         * Se comprueba que el hito realmente pertenezca al proceso
         * de tesis indicado. Esto evita combinar datos de procesos distintos.
         */
        if (hito.getProcesoTesis() == null
                || !Objects.equals(
                        hito.getProcesoTesis().getIdProcesoTesis(),
                        proceso.getIdProcesoTesis()
                )) {
            throw new IllegalArgumentException(
                    "El hito no pertenece al proceso de tesis indicado."
            );
        }

        /*
         * Se busca al tesista responsable de realizar la entrega.
         */
        Tesista tesista = tesistaRepository
                .findById(entregaDTO.getIdEstudiante())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tesista no encontrado."
                ));

        /*
         * Antes de guardar el archivo se comprueba:
         * - que exista;
         * - que no esté vacío;
         * - que informe el MIME de PDF;
         * - que su contenido realmente comience con la firma de un PDF.
         */
        validarArchivoPdf(archivo);

        /*
         * Se obtiene y limpia el nombre original informado por el navegador.
         */
        String nombreOriginal = obtenerNombreOriginal(archivo);

        /*
         * Se busca la última entrega correspondiente al mismo proceso,
         * hito y tipo de entrega.
         *
         * AVANCE y FINAL mantienen secuencias de versiones independientes.
         *
         * Si existe una entrega anterior del mismo tipo, la nueva versión
         * será la anterior más uno. Si no existe, será la versión uno.
         */
        int siguienteVersion = entregaRepository
                .findTopByProcesoTesisAndHitoEntregaAndTipoEntregaOrderByNumeroVersionDesc(
                        proceso,
                        hito,
                        tipoEntrega
                )
                .map(ultimaEntrega ->
                        ultimaEntrega.getNumeroVersion() + 1
                )
                .orElse(1);

        /*
         * Se guarda físicamente el archivo.
         * El servicio devuelve la ruta utilizada para recuperarlo.
         */
        String rutaGuardada =
                fileStorageService.guardarArchivo(archivo);

        /*
         * De la ruta obtenemos solamente el nombre interno generado.
         *
         * Ejemplo:
         * rutaGuardada = storage/entregas/uuid_documento.pdf
         * nombreAlmacenado = uuid_documento.pdf
         */
        String nombreAlmacenado = Path.of(rutaGuardada)
                .getFileName()
                .toString();

        /*
         * Se crea la entidad que posteriormente será almacenada
         * como una fila nueva en la tabla entrega.
         */
        Entrega entrega = new Entrega();

        /*
         * Se asignan las relaciones con proceso, hito y tesista.
         */
        entrega.setProcesoTesis(proceso);
        entrega.setHitoEntrega(hito);
        entrega.setEstudiante(tesista);

        /*
         * Datos de negocio establecidos automáticamente por el servidor.
         *
         * El tipo recibido por este método solamente puede provenir
         * de crearEntrega o crearEntregaFinal.
         */
        entrega.setTipoEntrega(tipoEntrega);
        entrega.setFechaHora(LocalDateTime.now());
        entrega.setEstado(ESTADO_PENDIENTE_REVISION);
        entrega.setNumeroVersion(siguienteVersion);

        /*
         * Metadatos obtenidos directamente desde el archivo.
         */
        entrega.setNombreOriginal(nombreOriginal);
        entrega.setNombreAlmacenado(nombreAlmacenado);

        /*
         * Se reemplazan las barras invertidas de Windows para almacenar
         * una ruta consistente e independiente del sistema operativo.
         */
        entrega.setRutaRelativaArchivo(
                rutaGuardada.replace('\\', '/')
        );

        /*
         * El MIME no se toma libremente desde el cliente porque el archivo
         * ya fue validado como PDF.
         */
        entrega.setMimeType("application/pdf");
        entrega.setTamanoBytes(archivo.getSize());

        /*
         * Cada subida se guarda como una fila nueva.
         * Las versiones anteriores no se reemplazan ni se eliminan.
         */
        Entrega entregaGuardada =
                entregaRepository.save(entrega);

        /*
         * Se devuelve al frontend un DTO con el registro creado.
         */
        return convertirADTO(entregaGuardada);
    }

    /*
     * Comprueba que el archivo recibido corresponda realmente a un PDF.
     */
    private void validarArchivoPdf(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "El archivo PDF es obligatorio."
            );
        }

        /*
         * Primera validación: el MIME informado debe ser application/pdf.
         */
        if (!"application/pdf".equalsIgnoreCase(
                archivo.getContentType()
        )) {
            throw new IllegalArgumentException(
                    "El archivo debe tener tipo MIME application/pdf."
            );
        }

        /*
         * Segunda validación: un PDF válido debe comenzar con %PDF-.
         * Así no se confía solamente en la extensión o en el MIME.
         */
        try (InputStream inputStream = archivo.getInputStream()) {
            byte[] encabezado = inputStream.readNBytes(5);

            String firma = new String(
                    encabezado,
                    StandardCharsets.US_ASCII
            );

            if (!"%PDF-".equals(firma)) {
                throw new IllegalArgumentException(
                        "El contenido del archivo no corresponde a un PDF."
                );
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "No fue posible leer el archivo.",
                    exception
            );
        }
    }

    /*
     * Limpia y valida el nombre original del archivo.
     * Esto evita rutas maliciosas y nombres que no terminen en .pdf.
     */
    private String obtenerNombreOriginal(MultipartFile archivo) {
        String nombreInformado = archivo.getOriginalFilename();

        if (nombreInformado == null || nombreInformado.isBlank()) {
            throw new IllegalArgumentException(
                    "El archivo debe tener un nombre."
            );
        }

        /*
         * Se normalizan las barras para soportar nombres enviados
         * desde distintos sistemas operativos.
         */
        String nombreLimpio =
                StringUtils.cleanPath(nombreInformado)
                        .replace('\\', '/');

        /*
         * Se rechazan intentos de desplazamiento entre carpetas.
         */
        if (nombreLimpio.contains("..")) {
            throw new IllegalArgumentException(
                    "El nombre del archivo contiene una ruta inválida."
            );
        }

        /*
         * Si el navegador envía una ruta, se conserva solamente
         * el último elemento, que corresponde al nombre del archivo.
         */
        nombreLimpio = nombreLimpio.substring(
                nombreLimpio.lastIndexOf('/') + 1
        );

        /*
         * La comparación se realiza en minúsculas para aceptar
         * extensiones como .pdf, .PDF o .Pdf.
         */
        if (!nombreLimpio
                .toLowerCase(Locale.ROOT)
                .endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "El archivo debe tener extensión .pdf."
            );
        }

        return nombreLimpio;
    }

    /*
     * Obtiene todas las entregas y las convierte a DTO.
     *
     * readOnly = true indica que esta operación solamente consulta datos
     * y no realizará cambios en PostgreSQL.
     */
    @Transactional(readOnly = true)
    public List<EntregaResponseDTO> obtenerTodasLasEntregas() {
        return entregaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}