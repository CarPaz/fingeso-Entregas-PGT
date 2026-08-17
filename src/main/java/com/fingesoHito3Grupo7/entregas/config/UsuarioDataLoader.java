package com.fingesoHito3Grupo7.entregas.config;

import com.fingesoHito3Grupo7.entregas.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cargador automático que se ejecuta al iniciar la aplicación Spring Boot.
 * Lee el archivo CSV configurado e inserta/actualiza los usuarios iniciales.
 */
@Component
public class UsuarioDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDataLoader.class);

    private final UsuarioService usuarioService;

    @Value("${app.usuarios.csv-path:classpath:usuarios.csv}")
    private String rutaCsv;

    public UsuarioDataLoader(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        log.info("[UsuarioDataLoader] Iniciando carga automática de usuarios desde: {}", rutaCsv);
        usuarioService.cargarUsuariosDesdeRecurso(rutaCsv);
    }
}