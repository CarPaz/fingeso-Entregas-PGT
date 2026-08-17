package com.fingesoHito3Grupo7.entregas.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitingServiceTest {

    @Test
    void bloqueaDespuesDelMaximoDeIntentos() {
        RateLimitingService servicio = crearServicio(3, 60);
        String clave = servicio.construirClave("127.0.0.1", "USUARIO@Universidad.cl ");

        servicio.registrarIntentoFallido(clave);
        servicio.registrarIntentoFallido(clave);
        servicio.registrarIntentoFallido(clave);

        RateLimitingService.RateLimitExcedidoException error = assertThrows(
                RateLimitingService.RateLimitExcedidoException.class,
                () -> servicio.verificarLimite(clave)
        );

        assertTrue(error.getRetryAfterSegundos() > 0);
        assertEquals("127.0.0.1:usuario@universidad.cl", clave);
    }

    @Test
    void conservaTodosLosIncrementosConcurrentesDelContador() throws Exception {
        int cantidadIntentos = 20;
        RateLimitingService servicio = crearServicio(cantidadIntentos, 60);
        String clave = servicio.construirClave("127.0.0.1", "tesista@universidad.cl");
        ExecutorService ejecutor = Executors.newFixedThreadPool(cantidadIntentos);
        CountDownLatch listos = new CountDownLatch(cantidadIntentos);
        CountDownLatch comenzar = new CountDownLatch(1);
        List<Future<?>> tareas = new ArrayList<>();

        try {
            for (int i = 0; i < cantidadIntentos; i++) {
                tareas.add(ejecutor.submit(() -> {
                    listos.countDown();
                    comenzar.await();
                    servicio.registrarIntentoFallido(clave);
                    return null;
                }));
            }

            listos.await();
            comenzar.countDown();
            for (Future<?> tarea : tareas) {
                tarea.get();
            }
        } finally {
            ejecutor.shutdownNow();
        }

        assertThrows(
                RateLimitingService.RateLimitExcedidoException.class,
                () -> servicio.verificarLimite(clave)
        );
    }

    private RateLimitingService crearServicio(int maxIntentos, long bloqueoSegundos) {
        RateLimitingService servicio = new RateLimitingService();
        ReflectionTestUtils.setField(servicio, "maxIntentos", maxIntentos);
        ReflectionTestUtils.setField(servicio, "bloqueoSegundos", bloqueoSegundos);
        return servicio;
    }
}
