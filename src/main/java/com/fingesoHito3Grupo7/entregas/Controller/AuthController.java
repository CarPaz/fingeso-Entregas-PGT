package com.fingesoHito3Grupo7.entregas.Controller;

import com.fingesoHito3Grupo7.entregas.dto.LoginRequestDTO;
import com.fingesoHito3Grupo7.entregas.dto.LoginResponseDTO;
import com.fingesoHito3Grupo7.entregas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 *
 * Endpoints:
 *  POST /api/auth/login  → Autenticar usuario con correo + contraseña.
 *
 * Gestión de la IP del cliente:
 *  Se extrae primero el header X-Forwarded-For (cuando hay proxy/balanceador)
 *  y si no existe se usa la IP directa de la conexión.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class Authcontroller {

    private final AuthService authService;

    public Authcontroller(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     *
     * Body JSON esperado:
     * {
     *   "correo": "usuario@usach.cl",
     *   "contrasena": "MiContrasena123"
     * }
     *
     * Respuestas posibles:
     *  200 OK            → Login exitoso, devuelve LoginResponseDTO
     *  400 Bad Request   → Campos vacíos o formato de correo inválido
     *  401 Unauthorized  → Credenciales incorrectas o cuenta desactivada
     *  429 Too Many Requests → Límite de intentos superado (header Retry-After incluido)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest) {

        String ip = obtenerIpCliente(httpRequest);
        LoginResponseDTO respuesta = authService.login(request, ip);
        return ResponseEntity.ok(respuesta);
    }

    // -------------------------------------------------------------------------
    // Utilitario: extrae la IP real del cliente respetando proxies/balanceadores
    // -------------------------------------------------------------------------
    private String obtenerIpCliente(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For puede tener varias IPs separadas por coma; la primera es la del cliente
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
