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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Pruebas unitarias del registro de entregas.
 *
 * Los repositorios y el almacenamiento se simulan con Mockito,
 * por lo que esta prueba no modifica PostgreSQL ni crea archivos reales.
 */
@ExtendWith(MockitoExtension.class)
class EntregaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProcesoTesisRepository procesoTesisRepository;

    @Mock
    private HitoEntregaRepository hitoEntregaRepository;

    @Mock
    private TesistaRepository tesistaRepository;

    @InjectMocks
    private EntregaService entregaService;

    @Test
    void debeRegistrarAvanceConLaSiguienteVersion() {
        /*
         * Datos que simulan registros existentes.
         */
        ProcesoTesis proceso = new ProcesoTesis();
        proceso.setIdProcesoTesis(1L);

        HitoEntrega hito = new HitoEntrega();
        hito.setIdHitoEntrega(2L);
        hito.setProcesoTesis(proceso);

        Tesista tesista = new Tesista();
        tesista.setIdUsuario(3L);
        tesista.setCorreoInstitucional("tesista@universidad.cl");
        proceso.setTesista(tesista);

        /*
         * Ya existe una versión 2, por lo que la nueva debe ser 3.
         */
        Entrega entregaAnterior = new Entrega();
        entregaAnterior.setNumeroVersion(2);

        EntregaDTO dto = new EntregaDTO();
        dto.setIdProcesoTesis(1L);
        dto.setIdHitoEntrega(2L);

        /*
         * Archivo simulado que contiene la firma real de un PDF.
         */
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "avance.pdf",
                "application/pdf",
                "%PDF-1.4 contenido de prueba"
                        .getBytes(StandardCharsets.US_ASCII)
        );

        /*
         * Respuestas simuladas de los repositorios.
         */
        when(procesoTesisRepository.findById(1L))
                .thenReturn(Optional.of(proceso));

        when(hitoEntregaRepository.findById(2L))
                .thenReturn(Optional.of(hito));

        when(tesistaRepository.findByCorreoInstitucionalIgnoreCase("tesista@universidad.cl"))
                .thenReturn(Optional.of(tesista));

        when(entregaRepository
                .findTopByProcesoTesisAndHitoEntregaAndTipoEntregaOrderByNumeroVersionDesc(
                        proceso,
                        hito,
                        "AVANCE"
                ))
                .thenReturn(Optional.of(entregaAnterior));

        when(fileStorageService.guardarArchivo(archivo))
                .thenReturn(
                        "./storage/entregas/archivo-interno.pdf"
                );

        /*
         * Se simula que PostgreSQL genera el ID 10 al guardar.
         */
        when(entregaRepository.save(any(Entrega.class)))
                .thenAnswer(invocacion -> {
                    Entrega entrega = invocacion.getArgument(0);
                    entrega.setIdEntrega(10L);
                    return entrega;
                });

        EntregaResponseDTO respuesta
                = entregaService.crearEntrega(dto, archivo, "tesista@universidad.cl");

        /*
         * Captura de la entidad enviada al repositorio.
         */
        ArgumentCaptor<Entrega> captor
                = ArgumentCaptor.forClass(Entrega.class);

        verify(entregaRepository).save(captor.capture());

        Entrega registrada = captor.getValue();

        /*
         * Comprobación de las reglas de negocio.
         */
        assertEquals("AVANCE", registrada.getTipoEntrega());
        assertEquals(
                "PENDIENTE_REVISION",
                registrada.getEstado()
        );
        assertEquals(3, registrada.getNumeroVersion());
        assertNotNull(registrada.getFechaHora());

        assertSame(proceso, registrada.getProcesoTesis());
        assertSame(hito, registrada.getHitoEntrega());
        assertSame(tesista, registrada.getEstudiante());

        assertEquals(
                "avance.pdf",
                registrada.getNombreOriginal()
        );
        assertEquals(
                "archivo-interno.pdf",
                registrada.getNombreAlmacenado()
        );
        assertEquals(
                "application/pdf",
                registrada.getMimeType()
        );

        /*
         * También se verifica el DTO devuelto al frontend.
         */
        assertEquals(10L, respuesta.getIdEntrega());
        assertEquals(1L, respuesta.getIdProcesoTesis());
        assertEquals(2L, respuesta.getIdHitoEntrega());
        assertEquals(3L, respuesta.getIdEstudiante());
        assertEquals(3, respuesta.getNumeroVersion());
    }

    @Test
    void debeRegistrarEntregaFinalConLaSiguienteVersion() {
        /*
     * Datos que simulan registros existentes.
         */
        ProcesoTesis proceso = new ProcesoTesis();
        proceso.setIdProcesoTesis(1L);

        HitoEntrega hito = new HitoEntrega();
        hito.setIdHitoEntrega(2L);
        hito.setProcesoTesis(proceso);

        Tesista tesista = new Tesista();
        tesista.setIdUsuario(3L);
        tesista.setCorreoInstitucional("tesista@universidad.cl");
        proceso.setTesista(tesista);

        /*
     * Ya existe una entrega FINAL con versión 1.
     * La nueva entrega FINAL debe registrarse como versión 2.
         */
        Entrega entregaFinalAnterior = new Entrega();
        entregaFinalAnterior.setNumeroVersion(1);

        EntregaDTO dto = new EntregaDTO();
        dto.setIdProcesoTesis(1L);
        dto.setIdHitoEntrega(2L);

        /*
     * Archivo simulado con nombre de entrega final
     * y con la firma válida de un PDF.
         */
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "entrega-final.pdf",
                "application/pdf",
                "%PDF-1.4 contenido final de prueba"
                        .getBytes(StandardCharsets.US_ASCII)
        );

        /*
     * Respuestas simuladas de los repositorios.
         */
        when(procesoTesisRepository.findById(1L))
                .thenReturn(Optional.of(proceso));

        when(hitoEntregaRepository.findById(2L))
                .thenReturn(Optional.of(hito));

        when(tesistaRepository.findByCorreoInstitucionalIgnoreCase("tesista@universidad.cl"))
                .thenReturn(Optional.of(tesista));

        /*
     * La búsqueda utiliza FINAL, por lo que su versionado
     * es independiente del versionado de AVANCE.
         */
        when(entregaRepository
                .findTopByProcesoTesisAndHitoEntregaAndTipoEntregaOrderByNumeroVersionDesc(
                        proceso,
                        hito,
                        "FINAL"
                ))
                .thenReturn(Optional.of(entregaFinalAnterior));

        when(fileStorageService.guardarArchivo(archivo))
                .thenReturn(
                        "./storage/entregas/final-interno.pdf"
                );

        /*
     * Se simula que PostgreSQL genera el ID 20 al guardar.
         */
        when(entregaRepository.save(any(Entrega.class)))
                .thenAnswer(invocacion -> {
                    Entrega entrega = invocacion.getArgument(0);
                    entrega.setIdEntrega(20L);
                    return entrega;
                });

        /*
     * Se ejecuta específicamente el registro de entrega FINAL.
         */
        EntregaResponseDTO respuesta
                = entregaService.crearEntregaFinal(dto, archivo, "tesista@universidad.cl");

        /*
     * Se captura la entidad que fue enviada al repositorio.
         */
        ArgumentCaptor<Entrega> captor
                = ArgumentCaptor.forClass(Entrega.class);

        verify(entregaRepository).save(captor.capture());

        Entrega registrada = captor.getValue();

        /*
     * Comprobación de las reglas de la entrega final.
         */
        assertEquals("FINAL", registrada.getTipoEntrega());
        assertEquals(
                "PENDIENTE_REVISION",
                registrada.getEstado()
        );
        assertEquals(2, registrada.getNumeroVersion());
        assertNotNull(registrada.getFechaHora());

        assertSame(proceso, registrada.getProcesoTesis());
        assertSame(hito, registrada.getHitoEntrega());
        assertSame(tesista, registrada.getEstudiante());

        assertEquals(
                "entrega-final.pdf",
                registrada.getNombreOriginal()
        );
        assertEquals(
                "final-interno.pdf",
                registrada.getNombreAlmacenado()
        );
        assertEquals(
                "application/pdf",
                registrada.getMimeType()
        );

        /*
     * También se verifica el DTO devuelto al frontend.
         */
        assertEquals(20L, respuesta.getIdEntrega());
        assertEquals(1L, respuesta.getIdProcesoTesis());
        assertEquals(2L, respuesta.getIdHitoEntrega());
        assertEquals(3L, respuesta.getIdEstudiante());
        assertEquals("FINAL", respuesta.getTipoEntrega());
        assertEquals(2, respuesta.getNumeroVersion());

        /*
     * Se confirma que la consulta de versión utilizó FINAL.
         */
        verify(entregaRepository)
                .findTopByProcesoTesisAndHitoEntregaAndTipoEntregaOrderByNumeroVersionDesc(
                        proceso,
                        hito,
                        "FINAL"
                );
    }
}
