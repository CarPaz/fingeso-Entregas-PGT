package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.domain.HitoEntrega;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.dto.EntregaDTO;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.HitoEntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.ProcesoTesisRepository;
import com.fingesoHito3Grupo7.entregas.repository.TesistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregaServiceEmailTest {

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

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EntregaService entregaService;

    @Test
    void debeNotificarAlProfesorAsociadoAlProceso() {
        Profesor profesor = new Profesor();
        profesor.setIdUsuario(4L);
        profesor.setCorreoInstitucional("guia@universidad.cl");

        ProcesoTesis proceso = new ProcesoTesis();
        proceso.setIdProcesoTesis(1L);
        proceso.setProfesor(profesor);

        HitoEntrega hito = new HitoEntrega();
        hito.setIdHitoEntrega(2L);
        hito.setProcesoTesis(proceso);

        Tesista tesista = new Tesista();
        tesista.setIdUsuario(3L);

        EntregaDTO dto = new EntregaDTO();
        dto.setIdProcesoTesis(1L);
        dto.setIdHitoEntrega(2L);
        dto.setIdEstudiante(3L);
        dto.setTipoEntrega("AVANCE");

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "avance.pdf",
                "application/pdf",
                "%PDF-1.4 contenido de prueba"
                        .getBytes(StandardCharsets.US_ASCII)
        );

        when(procesoTesisRepository.findById(1L))
                .thenReturn(Optional.of(proceso));
        when(hitoEntregaRepository.findById(2L))
                .thenReturn(Optional.of(hito));
        when(tesistaRepository.findById(3L))
                .thenReturn(Optional.of(tesista));
        when(fileStorageService.guardarArchivo(archivo))
                .thenReturn("./storage/entregas/archivo-interno.pdf");
        when(entregaRepository.save(any(Entrega.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(emailService.enviarCorreoSimple(
                eq("guia@universidad.cl"),
                any(String.class),
                any(String.class)
        )).thenReturn(true);

        entregaService.crearEntrega(dto, archivo);

        verify(emailService).enviarCorreoSimple(
                eq("guia@universidad.cl"),
                contains("AVANCE"),
                contains("avance.pdf")
        );
    }
}
