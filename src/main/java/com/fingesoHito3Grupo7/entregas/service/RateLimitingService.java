package com.fingesoHito3Grupo7.entregas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de Rate Limiting para el endpoint de login.
 *
 * Política (configurable en application.properties):
 *   - Máximo N intentos fallidos consecutivos por clave (IP + correo).
 *   - Si se supera el límite, la clave queda bloqueada por M segundos.
 *
 * Se usa ConcurrentHashMap para ser thread-safe sin bloquear todo el servicio.
 * Los datos se guardan en memoria (reiniciar la app limpia el estado).
 */
@Service
public class RateLimitingService {

    // Máximo de intentos fallidos antes de bloquear. Configurable en application.properties
    @Value("${app.security.rate-limit.max-intentos:5}")
    private int maxIntentos;

    // Tiempo de bloqueo en segundos. Configurable en application.properties
    @Value("${app.security.rate-limit.bloqueo-segundos:900}")
    private long bloqueoSegundos;

    // Estructura interna para registrar el estado de cada clave
    private static class EstadoIntento {
        private final int intentosFallidos;
        private final Instant bloqueadoHasta;

        EstadoIntento(int intentosFallidos, Instant bloqueadoHasta) {
            this.intentosFallidos = intentosFallidos;
            this.bloqueadoHasta = bloqueadoHasta;
        }
    }

    // Mapa concurrente: clave → estado de intentos
    private final ConcurrentHashMap<String, EstadoIntento> intentos = new ConcurrentHashMap<>();

    /**
     * Construye la clave de rate limiting combinando IP y correo normalizado.
     * Normalizar el correo evita bypass con mayúsculas/espacios.
     */
    public String construirClave(String ip, String correo) {
        return ip + ":" + correo.trim().toLowerCase();
    }

    /**
     * Verifica si la clave está actualmente bloqueada.
     * @throws RateLimitExcedidoException si está bloqueada, con segundos restantes.
     */
    public void verificarLimite(String clave) {
        EstadoIntento estado = intentos.get(clave);
        if (estado == null) return;

        if (estado.bloqueadoHasta != null && Instant.now().isBefore(estado.bloqueadoHasta)) {
            long segundosRestantes = estado.bloqueadoHasta.getEpochSecond() - Instant.now().getEpochSecond();
            throw new RateLimitExcedidoException(
                "Demasiados intentos fallidos. Intente nuevamente en " + segundosRestantes + " segundos.",
                segundosRestantes
            );
        }

        // Si el bloqueo ya expiró, limpiar el estado
        if (estado.bloqueadoHasta != null && !Instant.now().isBefore(estado.bloqueadoHasta)) {
            intentos.remove(clave, estado);
        }
    }

    /**
     * Registra un intento fallido. Si se supera el límite, activa el bloqueo.
     */
    public void registrarIntentoFallido(String clave) {
        /*
         * compute ejecuta toda la actualización de una clave como una sola operación.
         * Así dos solicitudes simultáneas no pueden leer el mismo contador y perder
         * uno de los incrementos.
         */
        intentos.compute(clave, (k, estadoActual) -> {
            int nuevosIntentos = estadoActual == null
                    ? 1
                    : estadoActual.intentosFallidos + 1;

            Instant bloqueadoHasta = nuevosIntentos >= maxIntentos
                    ? Instant.now().plusSeconds(bloqueoSegundos)
                    : null;

            return new EstadoIntento(nuevosIntentos, bloqueadoHasta);
        });
    }

    /**
     * Limpia el registro de intentos fallidos al hacer login exitoso.
     */
    public void limpiarIntentos(String clave) {
        intentos.remove(clave);
    }

    // -------------------------------------------------------------------------
    // Excepción interna para el rate limit (HTTP 429)
    // -------------------------------------------------------------------------
    public static class RateLimitExcedidoException extends RuntimeException {
        private final long retryAfterSegundos;

        public RateLimitExcedidoException(String message, long retryAfterSegundos) {
            super(message);
            this.retryAfterSegundos = retryAfterSegundos;
        }

        public long getRetryAfterSegundos() {
            return retryAfterSegundos;
        }
    }
}
