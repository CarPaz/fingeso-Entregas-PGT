package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.domain.HitoEntrega;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.HitoEntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.ProcesoTesisRepository;
import com.fingesoHito3Grupo7.entregas.repository.TesistaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class EntregaService {

    private static final Logger logger =
            LoggerFactory.getLogger(EntregaService.class);

    //conexion con base de datos
    private final EntregaRepository entregaRepository;
    private final FileStorageService fileStorageService;

    private final ProcesoTesisRepository procesoTesisRepository;
    private final HitoEntregaRepository hitoEntregaRepository;
    private final TesistaRepository tesistaRepository;
    private final EmailService emailService;
    // Constructor para inyectar el repositorio
    public EntregaService(EntregaRepository entregaRepository, 
                          FileStorageService fileStorageService,
                          ProcesoTesisRepository procesoTesisRepository,
                          HitoEntregaRepository hitoEntregaRepository,
                          TesistaRepository tesistaRepository,
                          EmailService emailService) {
        this.entregaRepository = entregaRepository;
        this.fileStorageService = fileStorageService;
        this.procesoTesisRepository = procesoTesisRepository;
        this.hitoEntregaRepository = hitoEntregaRepository;
        this.tesistaRepository = tesistaRepository;
        this.emailService = emailService;
    }

    //covertir entidad a DTO de respuesta
    private EntregaResponseDTO convertirADTO(Entrega entrega) {
        EntregaResponseDTO dto = new EntregaResponseDTO();
        // Cambia getIdEntrega() por el nombre real del getter de tu ID en la entidad Entrega
        dto.setIdEntrega(entrega.getIdEntrega()); 
        
        if (entrega.getProcesoTesis() != null) {
            dto.setIdProcesoTesis(entrega.getProcesoTesis().getIdProcesoTesis());
        }
        if (entrega.getHitoEntrega() != null) {
            dto.setIdHitoEntrega(entrega.getHitoEntrega().getIdHitoEntrega());
        }
        
        dto.setNombreOriginal(entrega.getNombreOriginal());
        dto.setNombreAlmacenado(entrega.getNombreAlmacenado());
        dto.setMimeType(entrega.getMimeType());
        dto.setTamanoBytes(entrega.getTamanoBytes());
        dto.setRutaRelativaArchivo(entrega.getRutaRelativaArchivo());
        
        return dto;
    }

    
    @Transactional
    public EntregaResponseDTO crearEntrega(EntregaDTO entregaDTO, MultipartFile archivo) {
        
        // Creamos una entidad vacia que se ira llenando
        Entrega entrega = new Entrega();

        // Validar y asociar el Proceso de Tesis usando el ID que viene del DTO
        if (entregaDTO.getIdProcesoTesis() != null) {
            ProcesoTesis proceso = procesoTesisRepository.findById(entregaDTO.getIdProcesoTesis())
                    .orElseThrow(() -> new RuntimeException("Error: Proceso de Tesis no encontrado en la base de datos."));
            entrega.setProcesoTesis(proceso);
        } else {
            throw new IllegalArgumentException("Error: La entrega debe incluir el ID de un Proceso de Tesis.");
        }

        // Validar y asociar el Hito de Entrega usando el ID que viene del DTO
        if (entregaDTO.getIdHitoEntrega() != null) {
            HitoEntrega hito = hitoEntregaRepository.findById(entregaDTO.getIdHitoEntrega())
                    .orElseThrow(() -> new RuntimeException("Error: Hito de Entrega no encontrado en la base de datos."));
            entrega.setHitoEntrega(hito);
        } else {
            throw new IllegalArgumentException("Error: La entrega debe incluir el ID de un Hito de Entrega.");
        }
         
        //Validar y asignar los datos obligatorios del DTO
        if (entregaDTO.getTipoEntrega() == null || entregaDTO.getTipoEntrega().isBlank()) {
            throw new IllegalArgumentException("Error: El tipo de entrega es obligatorio.");
        }
        entrega.setTipoEntrega(entregaDTO.getTipoEntrega());

        if (entregaDTO.getIdEstudiante() == null) {
            throw new IllegalArgumentException("Error: El ID del estudiante es obligatorio.");
        }

        Tesista tesista = tesistaRepository.findById(entregaDTO.getIdEstudiante())
                .orElseThrow(() -> new RuntimeException("Error: Estudiante no encontrado en la base de datos."));

        entrega.setEstudiante(tesista);

        /// Validacion y guardado del archivo fisico 
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Error: El archivo PDF es obligatorio y no puede estar vacío.");
        }
        

        //  Extraer metadatos del archivo 
        String nombreOriginal = archivo.getOriginalFilename();
        String mimeType = archivo.getContentType();
        Long tamanoBytes = archivo.getSize();

        // Guardar el archivo físicamente
        String rutaArchivoGuardado = fileStorageService.guardarArchivo(archivo);

        // Asignar los metadatos a la entidad Entrega 
        entrega.setNombreOriginal(nombreOriginal);
        entrega.setNombreAlmacenado(rutaArchivoGuardado);
        entrega.setMimeType(mimeType);
        entrega.setTamanoBytes(tamanoBytes);
        
        //Se mantiene la ruta relativa original configurada
        entrega.setRutaRelativaArchivo(rutaArchivoGuardado);

        //Guardar la entrega completa en PostgreSQL
        Entrega entregaGuardada = entregaRepository.save(entrega);

        /*
         * La notificación utiliza el correo institucional del profesor
         * asociado al proceso de tesis. Una falla de SMTP se registra,
         * pero no invalida la entrega que ya fue guardada.
         */
        notificarProfesor(entregaGuardada);

        return convertirADTO(entregaGuardada);
    }

    /*
     * Envía al profesor responsable los datos principales de la entrega.
     * Los procesos antiguos pueden no tener profesor mientras se completa
     * la migración de datos; en ese caso se registra una advertencia.
     */
    private void notificarProfesor(Entrega entrega) {
        ProcesoTesis proceso = entrega.getProcesoTesis();

        if (proceso == null) {
            logger.warn(
                    "No se envió la notificación porque la entrega no tiene proceso de tesis."
            );
            return;
        }

        Profesor profesor = proceso.getProfesor();

        if (profesor == null) {
            logger.warn(
                    "No se envió la notificación de la entrega del proceso {} porque no tiene profesor asociado.",
                    proceso.getIdProcesoTesis()
            );
            return;
        }

        String correoProfesor = profesor.getCorreoInstitucional();

        if (!StringUtils.hasText(correoProfesor)) {
            logger.warn(
                    "No se envió la notificación de la entrega del proceso {} porque el profesor no tiene correo institucional.",
                    proceso.getIdProcesoTesis()
            );
            return;
        }

        Tesista tesista = entrega.getEstudiante();
        String identificacionTesista = tesista != null
                ? String.valueOf(tesista.getIdUsuario())
                : "no disponible";

        String asunto =
                "Nueva entrega de tesis: " + entrega.getTipoEntrega();

        String mensaje = "Hola,\n\n"
                + "El tesista (ID: " + identificacionTesista
                + ") ha subido un nuevo documento al sistema.\n\n"
                + "Detalles de la entrega:\n"
                + "- Tipo de entrega: " + entrega.getTipoEntrega() + "\n"
                + "- Nombre del archivo: " + entrega.getNombreOriginal() + "\n"
                + "- ID proceso de tesis: "
                + proceso.getIdProcesoTesis() + "\n\n"
                + "El documento fue guardado exitosamente.";

        boolean enviado = emailService.enviarCorreoSimple(
                correoProfesor,
                asunto,
                mensaje
        );

        if (!enviado) {
            logger.warn(
                    "La entrega del proceso {} se guardó, pero su notificación no pudo enviarse.",
                    proceso.getIdProcesoTesis()
            );
        }
    }

    public List<EntregaResponseDTO> obtenerTodasLasEntregas() {
        List<Entrega> entregas = entregaRepository.findAll();
        
        // Convertir la lista de entidades a lista de DTOs
        return entregas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

}
