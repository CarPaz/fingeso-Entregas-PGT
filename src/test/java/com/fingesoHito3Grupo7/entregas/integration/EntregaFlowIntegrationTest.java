package com.fingesoHito3Grupo7.entregas.integration;

import com.fingesoHito3Grupo7.entregas.domain.HitoEntrega;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.ProcesoTesis;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.HitoEntregaRepository;
import com.fingesoHito3Grupo7.entregas.repository.ProcesoTesisRepository;
import com.fingesoHito3Grupo7.entregas.repository.TesistaRepository;
import com.fingesoHito3Grupo7.entregas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Prueba integral del flujo principal sin depender de PostgreSQL ni SMTP reales.
 * Utiliza H2, almacenamiento temporal del proyecto y un servidor de correo simulado.
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:flujo_entregas;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=clave-jwt-integracion-entregas-segura-123456789",
        "app.jwt.expiracion-ms=60000",
        "app.storage.location=target/test-storage-integration",
        "app.usuarios.csv-path=classpath:usuarios-inexistentes.csv",
        "management.health.mail.enabled=false"
})
@AutoConfigureMockMvc
class EntregaFlowIntegrationTest {

    private static final String CLAVE = "ClaveSegura123";
    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("\\\"token\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    @Autowired private MockMvc mockMvc;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private TesistaRepository tesistaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProcesoTesisRepository procesoTesisRepository;
    @Autowired private HitoEntregaRepository hitoEntregaRepository;
    @Autowired private EntregaRepository entregaRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private Tesista tesista;
    private Tesista otroTesista;
    private Profesor profesor;
    private ProcesoTesis proceso;
    private HitoEntrega hito;

    @BeforeEach
    void prepararDatosRelacionados() {
        entregaRepository.deleteAll();
        hitoEntregaRepository.deleteAll();
        procesoTesisRepository.deleteAll();
        usuarioRepository.deleteAll();

        tesista = crearTesista("11.111.111-1", "Tesista Uno", "tesista@universidad.cl");
        otroTesista = crearTesista("22.222.222-2", "Tesista Dos", "otro@universidad.cl");

        profesor = new Profesor();
        profesor.setRut("33.333.333-3");
        profesor.setNombre("Profesor Guía");
        profesor.setCorreoInstitucional("profesor@universidad.cl");
        profesor.setContrasena(passwordEncoder.encode(CLAVE));
        profesor.setEstadoCuenta(true);
        profesor = (Profesor) usuarioRepository.save(profesor);

        proceso = new ProcesoTesis();
        proceso.setTema("Sistema de gestión de entregas");
        proceso.setEstado("ACTIVO");
        proceso.setEtapaActual("DESARROLLO");
        proceso.setTesista(tesista);
        proceso.setProfesor(profesor);
        proceso = procesoTesisRepository.save(proceso);

        hito = new HitoEntrega();
        hito.setNombre("Entrega de avance");
        hito.setFechaLimite(LocalDateTime.now().plusDays(7));
        hito.setFormato("PDF");
        hito.setEstado("ABIERTO");
        hito.setProcesoTesis(proceso);
        hito = hitoEntregaRepository.save(hito);
    }

    @Test
    void ejecutaLoginSubidaPersistenciaListadoNotificacionYDescarga() throws Exception {
        String tokenTesista = iniciarSesion(tesista.getCorreoInstitucional());

        /*
         * La vista obtiene procesos e hitos asociados al JWT, por lo que el
         * tesista no necesita conocer ni escribir identificadores internos.
         */
        mockMvc.perform(get("/api/entregas/opciones")
                        .header("Authorization", "Bearer " + tokenTesista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProcesoTesis")
                        .value(proceso.getIdProcesoTesis()))
                .andExpect(jsonPath("$[0].tema").value(proceso.getTema()))
                .andExpect(jsonPath("$[0].hitos[0].idHitoEntrega")
                        .value(hito.getIdHitoEntrega()));

        MockMultipartFile datosEntrega = new MockMultipartFile(
                "entrega", "", MediaType.APPLICATION_JSON_VALUE,
                ("{\"idProcesoTesis\":" + proceso.getIdProcesoTesis()
                        + ",\"idHitoEntrega\":" + hito.getIdHitoEntrega() + "}")
                        .getBytes(StandardCharsets.UTF_8)
        );
        byte[] contenidoPdf = "%PDF-1.4 flujo integral".getBytes(StandardCharsets.US_ASCII);
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "avance-integral.pdf", MediaType.APPLICATION_PDF_VALUE, contenidoPdf
        );

        String respuestaCreacion = mockMvc.perform(
                        multipart("/api/entregas/avance")
                                .file(datosEntrega).file(archivo)
                                .header("Authorization", "Bearer " + tokenTesista))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstudiante").value(tesista.getIdUsuario()))
                .andExpect(jsonPath("$.tipoEntrega").value("AVANCE"))
                .andReturn().getResponse().getContentAsString();

        Long idEntrega = extraerLong(respuestaCreacion, "idEntrega");
        assertEquals(1L, entregaRepository.count());
        assertEquals(tesista.getIdUsuario(),
                entregaRepository.findById(idEntrega).orElseThrow()
                        .getEstudiante().getIdUsuario());
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));

        mockMvc.perform(get("/api/entregas")
                        .header("Authorization", "Bearer " + tokenTesista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEntrega").value(idEntrega));

        String tokenProfesor = iniciarSesion(profesor.getCorreoInstitucional());
        mockMvc.perform(get("/api/entregas")
                        .header("Authorization", "Bearer " + tokenProfesor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEntrega").value(idEntrega));

        mockMvc.perform(get("/api/entregas/{id}/archivo", idEntrega)
                        .header("Authorization", "Bearer " + tokenProfesor))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(contenidoPdf));

        String tokenAjeno = iniciarSesion(otroTesista.getCorreoInstitucional());
        mockMvc.perform(multipart("/api/entregas/avance")
                        .file(datosEntrega).file(archivo)
                        .header("Authorization", "Bearer " + tokenAjeno))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/entregas/{id}/archivo", idEntrega)
                        .header("Authorization", "Bearer " + tokenAjeno))
                .andExpect(status().isForbidden());
    }

    private Tesista crearTesista(String rut, String nombre, String correo) {
        Tesista usuario = new Tesista();
        usuario.setRut(rut);
        usuario.setNombre(nombre);
        usuario.setCorreoInstitucional(correo);
        usuario.setContrasena(passwordEncoder.encode(CLAVE));
        usuario.setEstadoCuenta(true);
        return tesistaRepository.save(usuario);
    }

    private String iniciarSesion(String correo) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo
                                + "\",\"contrasena\":\"" + CLAVE + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = TOKEN_PATTERN.matcher(respuesta);
        assertTrue(matcher.find(), "La respuesta de login debe contener un token");
        return matcher.group(1);
    }

    private Long extraerLong(String json, String campo) {
        Matcher matcher = Pattern.compile("\\\"" + campo + "\\\"\\s*:\\s*(\\d+)")
                .matcher(json);
        assertTrue(matcher.find(), "La respuesta debe contener " + campo);
        return Long.valueOf(matcher.group(1));
    }
}
