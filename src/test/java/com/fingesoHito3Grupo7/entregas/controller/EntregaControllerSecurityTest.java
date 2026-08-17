package com.fingesoHito3Grupo7.entregas.controller;

import com.fingesoHito3Grupo7.entregas.config.SecurityConfig;
import com.fingesoHito3Grupo7.entregas.dto.EntregaResponseDTO;
import com.fingesoHito3Grupo7.entregas.security.JwtFilter;
import com.fingesoHito3Grupo7.entregas.security.JwtUtil;
import com.fingesoHito3Grupo7.entregas.service.EntregaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = EntregaController.class,
        properties = {
                "app.jwt.secret=clave-jwt-exclusiva-para-pruebas-automatizadas-123456",
                "app.jwt.expiracion-ms=60000"
        }
)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class EntregaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntregaService entregaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void rechazaSubidaSinAutenticacion() throws Exception {
        mockMvc.perform(solicitudEntregaAvance(null))
                .andExpect(status().isForbidden());
    }

    @Test
    void rechazaSubidaDeProfesor() throws Exception {
        String token = jwtUtil.generarToken("profesor@universidad.cl", "PROFESOR");

        mockMvc.perform(solicitudEntregaAvance(token))
                .andExpect(status().isForbidden());
    }

    @Test
    void rechazaSubidaDeCoordinador() throws Exception {
        String token = jwtUtil.generarToken("coordinador@universidad.cl", "COORDINADOR");

        mockMvc.perform(solicitudEntregaAvance(token))
                .andExpect(status().isForbidden());
    }

    @Test
    void permiteSubidaDeTesista() throws Exception {
        String token = jwtUtil.generarToken("tesista@universidad.cl", "TESISTA");
        EntregaResponseDTO respuesta = new EntregaResponseDTO();
        respuesta.setIdEntrega(1L);
        when(entregaService.crearEntrega(any(), any(), anyString())).thenReturn(respuesta);

        mockMvc.perform(solicitudEntregaAvance(token))
                .andExpect(status().isCreated());
    }

    @Test
    void rechazaListadoSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/entregas"))
                .andExpect(status().isForbidden());
    }

    @Test
    void permiteListadoDeTesista() throws Exception {
        String token = jwtUtil.generarToken("tesista@universidad.cl", "TESISTA");
        when(entregaService.obtenerEntregasAutorizadas(anyString(), anyString()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/entregas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void permiteListadoDeProfesor() throws Exception {
        String token = jwtUtil.generarToken("profesor@universidad.cl", "PROFESOR");
        when(entregaService.obtenerEntregasAutorizadas(anyString(), anyString()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/entregas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void permiteListadoDeCoordinador() throws Exception {
        String token = jwtUtil.generarToken("coordinador@universidad.cl", "COORDINADOR");
        when(entregaService.obtenerEntregasAutorizadas(anyString(), anyString()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/entregas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private MockMultipartHttpServletRequestBuilder solicitudEntregaAvance(String token) {
        MockMultipartFile entrega = new MockMultipartFile(
                "entrega",
                "",
                "application/json",
                "{\"idProcesoTesis\":1,\"idHitoEntrega\":1}"
                        .getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "avance.pdf",
                "application/pdf",
                "%PDF-1.4 contenido de prueba".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartHttpServletRequestBuilder solicitud = multipart("/api/entregas/avance")
                .file(entrega)
                .file(archivo);

        if (token != null) {
            solicitud.header("Authorization", "Bearer " + token);
        }
        return solicitud;
    }
}
