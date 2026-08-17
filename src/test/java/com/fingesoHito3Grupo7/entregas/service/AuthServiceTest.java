package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.CoordinadorDocente;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.domain.Usuario;
import com.fingesoHito3Grupo7.entregas.dto.LoginRequestDTO;
import com.fingesoHito3Grupo7.entregas.dto.LoginResponseDTO;
import com.fingesoHito3Grupo7.entregas.repository.UsuarioRepository;
import com.fingesoHito3Grupo7.entregas.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RateLimitingService rateLimitingService;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void configurar() {
        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                rateLimitingService,
                jwtUtil
        );
        when(rateLimitingService.construirClave(anyString(), anyString()))
                .thenReturn("127.0.0.1:usuario@universidad.cl");
        when(passwordEncoder.matches("clave-segura", "hash-bcrypt"))
                .thenReturn(true);
        when(jwtUtil.generarToken(anyString(), anyString()))
                .thenReturn("jwt-de-prueba");
    }

    @Test
    void permiteLoginDeTesista() {
        verificarLoginPorRol(new Tesista(), "TESISTA");
    }

    @Test
    void permiteLoginDeProfesor() {
        verificarLoginPorRol(new Profesor(), "PROFESOR");
    }

    @Test
    void permiteLoginDeCoordinador() {
        verificarLoginPorRol(new CoordinadorDocente(), "COORDINADOR");
    }

    private void verificarLoginPorRol(Usuario usuario, String rolEsperado) {
        usuario.setIdUsuario(10L);
        usuario.setNombre("Usuario de prueba");
        usuario.setCorreoInstitucional("usuario@universidad.cl");
        usuario.setContrasena("hash-bcrypt");
        usuario.setEstadoCuenta(true);

        when(usuarioRepository.findByCorreoInstitucional("usuario@universidad.cl"))
                .thenReturn(Optional.of(usuario));

        LoginRequestDTO solicitud = new LoginRequestDTO();
        solicitud.setCorreo("usuario@universidad.cl");
        solicitud.setContrasena("clave-segura");

        LoginResponseDTO respuesta = authService.login(solicitud, "127.0.0.1");

        assertEquals(rolEsperado, respuesta.getRol());
        assertEquals("jwt-de-prueba", respuesta.getToken());
        assertNotNull(respuesta.getIdUsuario());
        verify(jwtUtil).generarToken("usuario@universidad.cl", rolEsperado);
        verify(rateLimitingService).limpiarIntentos("127.0.0.1:usuario@universidad.cl");
    }
}
