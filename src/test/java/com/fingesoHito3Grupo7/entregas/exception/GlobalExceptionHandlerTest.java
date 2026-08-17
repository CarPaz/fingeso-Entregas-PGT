package com.fingesoHito3Grupo7.entregas.exception;

import com.fingesoHito3Grupo7.entregas.service.RateLimitingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void responde429YRetryAfterCuandoSeSuperaElLimite() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        RateLimitingService.RateLimitExcedidoException excepcion =
                new RateLimitingService.RateLimitExcedidoException(
                        "Demasiados intentos fallidos.",
                        45
                );

        ResponseEntity<Map<String, Object>> respuesta =
                handler.handleRateLimitExcedido(excepcion);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, respuesta.getStatusCode());
        assertEquals("45", respuesta.getHeaders().getFirst("Retry-After"));
        assertEquals(429, respuesta.getBody().get("status"));
    }
}
