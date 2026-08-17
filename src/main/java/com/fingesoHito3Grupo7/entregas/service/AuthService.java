package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.CoordinadorDocente;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.domain.Usuario;
import com.fingesoHito3Grupo7.entregas.dto.LoginRequestDTO;
import com.fingesoHito3Grupo7.entregas.dto.LoginResponseDTO;
import com.fingesoHito3Grupo7.entregas.repository.UsuarioRepository;
import com.fingesoHito3Grupo7.entregas.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación.
 *
 * Responsabilidades:
 *  1. Verificar el rate limit ANTES de consultar la BD (evita ataques de fuerza bruta).
 *  2. Buscar el usuario por correo institucional.
 *  3. Validar que la cuenta esté activa.
 *  4. Comparar la contraseña con BCrypt.
 *  5. Devolver un LoginResponseDTO con los datos del usuario autenticado.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RateLimitingService rateLimitingService;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       RateLimitingService rateLimitingService,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitingService = rateLimitingService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Procesa un intento de login.
     *
     * @param request DTO con correo y contraseña.
     * @param ipCliente IP del cliente (para rate limiting).
     * @return LoginResponseDTO con datos del usuario si las credenciales son válidas.
     * @throws CredencialesInvalidasException si el correo no existe, la cuenta está
     *         desactivada o la contraseña es incorrecta (mensaje genérico para no dar pistas).
     * @throws RateLimitingService.RateLimitExcedidoException si se superó el límite de intentos.
     */
    public LoginResponseDTO login(LoginRequestDTO request, String ipCliente) {

        // 1. Construir clave de rate limit y verificar antes de cualquier consulta a BD
        String claveLimite = rateLimitingService.construirClave(ipCliente, request.getCorreo());
        rateLimitingService.verificarLimite(claveLimite);

        // 2. Buscar usuario por correo (mensaje genérico si no existe → no revelamos si el correo está registrado)
        Usuario usuario = usuarioRepository
                .findByCorreoInstitucional(request.getCorreo().trim().toLowerCase())
                .orElse(null);

        // 3. Validar existencia, estado de cuenta y contraseña en bloque unificado
        //    para evitar timing attacks y no dar pistas sobre qué falló exactamente.
        boolean credencialesOk = usuario != null
                && Boolean.TRUE.equals(usuario.getEstadoCuenta())
                && passwordEncoder.matches(request.getContrasena(), usuario.getContrasena());

        if (!credencialesOk) {
            // Registrar fallo en el rate limiter solo si el usuario existe
            // (no contabilizamos correos que nunca han existido para no inflar el estado)
            if (usuario != null) {
                rateLimitingService.registrarIntentoFallido(claveLimite);
            }
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos, o cuenta desactivada.");
        }

        // 4. Login exitoso: limpiar intentos fallidos
        rateLimitingService.limpiarIntentos(claveLimite);

        // 5. Obtener el rol desde el discriminador de JPA (tipo_usuario en la BD)
        String rol = obtenerRol(usuario);

        // 6. Generar token JWT
        String token = jwtUtil.generarToken(usuario.getCorreoInstitucional(), rol);

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreoInstitucional(),
                rol,
                token
        );
    }

    /**
     * Extrae el nombre del rol a partir del tipo concreto de la entidad Usuario.
     * Mapea de forma segura a los valores discriminadores de JPA ("TESISTA", "PROFESOR", "COORDINADOR").
     */
    private String obtenerRol(Usuario usuario) {
        if (usuario instanceof CoordinadorDocente) {
            return "COORDINADOR";
        }
        if (usuario instanceof Profesor) {
            return "PROFESOR";
        }
        if (usuario instanceof Tesista) {
            return "TESISTA";
        }
        return usuario.getClass().getSimpleName().toUpperCase();
    }

    // -------------------------------------------------------------------------
    // Excepción interna para credenciales inválidas (HTTP 401)
    // -------------------------------------------------------------------------
    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException(String message) {
            super(message);
        }
    }
}
