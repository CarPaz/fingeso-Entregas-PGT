package com.fingesoHito3Grupo7.entregas.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRETO_PRUEBA =
            "clave-jwt-exclusiva-para-pruebas-automatizadas-123456";

    @Test
    void generaYValidaJwtConCorreoYRol() {
        JwtUtil jwtUtil = new JwtUtil(SECRETO_PRUEBA, 60_000);
        String token = jwtUtil.generarToken("tesista@universidad.cl", "TESISTA");

        assertTrue(jwtUtil.esValido(token));
        assertEquals("tesista@universidad.cl", jwtUtil.extraerCorreo(token));
        assertEquals("TESISTA", jwtUtil.extraerRol(token));
    }

    @Test
    void rechazaJwtAlterado() {
        JwtUtil jwtUtil = new JwtUtil(SECRETO_PRUEBA, 60_000);
        String token = jwtUtil.generarToken("profesor@universidad.cl", "PROFESOR");

        assertFalse(jwtUtil.esValido(token + "alterado"));
    }

    @Test
    void rechazaJwtExpirado() {
        JwtUtil jwtUtil = new JwtUtil(SECRETO_PRUEBA, -1);
        String token = jwtUtil.generarToken("coordinador@universidad.cl", "COORDINADOR");

        assertFalse(jwtUtil.esValido(token));
    }
}
