package com.fingesoHito3Grupo7.entregas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad JWT.
 * - Genera tokens firmados (correo + rol, con expiracion).
 * - Extrae correo y rol de un token.
 * - Valida autenticidad y vigencia del token.
 *
 * Configuracion en application.properties:
 *  app.jwt.secret        -> clave secreta (min. 32 caracteres)
 *  app.jwt.expiracion-ms -> duracion en ms (86400000 = 24h)
 */
@Component
public class JwtUtil {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiracion-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    /**
     * Genera un token JWT firmado.
     * @param correo subject del token (correo institucional)
     * @param rol    claim personalizado con el rol del usuario
     * @return token JWT como String
     */
    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    /** Extrae el correo (subject) del token. */
    public String extraerCorreo(String token) {
        return parsear(token).getSubject();
    }

    /** Extrae el rol del token. */
    public String extraerRol(String token) {
        return parsear(token).get("rol", String.class);
    }

    /**
     * Valida firma y vigencia del token.
     * @return true si el token es valido, false si es invalido o expiro
     */
    public boolean esValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parsear(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}