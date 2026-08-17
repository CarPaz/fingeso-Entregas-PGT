package com.fingesoHito3Grupo7.entregas.exception;

import com.fingesoHito3Grupo7.entregas.service.AuthService;
import com.fingesoHito3Grupo7.entregas.service.RateLimitingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones.
 *
 * Captura las excepciones de los servicios y las convierte en respuestas
 * JSON limpias con el código HTTP correcto, sin exponer stack traces ni
 * detalles internos al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // HTTP 401 — Credenciales inválidas
    // -------------------------------------------------------------------------
    @ExceptionHandler(AuthService.CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(
            AuthService.CredencialesInvalidasException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(crearCuerpoError(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    // -------------------------------------------------------------------------
    // HTTP 429 — Rate limit superado
    // -------------------------------------------------------------------------
    @ExceptionHandler(RateLimitingService.RateLimitExcedidoException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExcedido(
            RateLimitingService.RateLimitExcedidoException ex) {

        HttpHeaders headers = new HttpHeaders();
        // Cabecera estándar que indica cuántos segundos esperar antes de reintentar
        headers.set("Retry-After", String.valueOf(ex.getRetryAfterSegundos()));

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers)
                .body(crearCuerpoError(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
    }

    // -------------------------------------------------------------------------
    // HTTP 400 — Validación de DTOs (@Valid / @NotBlank / etc.)
    // -------------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(crearCuerpoError(HttpStatus.BAD_REQUEST, mensaje));
    }

    // -------------------------------------------------------------------------
    // HTTP 400 — Argumentos ilegales del servicio (IllegalArgumentException)
    // -------------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentoIlegal(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(crearCuerpoError(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // -------------------------------------------------------------------------
    // Errores HTTP explícitos del dominio (401, 403 y 404)
    // -------------------------------------------------------------------------
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensaje = ex.getReason() != null
                ? ex.getReason()
                : status.getReasonPhrase();

        return ResponseEntity
                .status(status)
                .body(crearCuerpoError(status, mensaje));
    }

    // -------------------------------------------------------------------------
    // HTTP 500 — Cualquier otra excepción no prevista
    // -------------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        // No exponer el mensaje interno al cliente en producción
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearCuerpoError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor."));
    }

    // -------------------------------------------------------------------------
    // Utilitario: construye el cuerpo de error en formato JSON consistente
    // -------------------------------------------------------------------------
    private Map<String, Object> crearCuerpoError(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", Instant.now().toString());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}
