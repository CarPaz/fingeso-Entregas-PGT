package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.CoordinadorDocente;
import com.fingesoHito3Grupo7.entregas.domain.Profesor;
import com.fingesoHito3Grupo7.entregas.domain.Tesista;
import com.fingesoHito3Grupo7.entregas.domain.Usuario;
import com.fingesoHito3Grupo7.entregas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servicio encargado de la carga automática de usuarios desde archivos CSV locales en el backend.
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          ResourceLoader resourceLoader) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Carga usuarios a partir de un recurso del sistema o classpath (ej: "classpath:usuarios.csv").
     *
     * @param rutaRecurso Ruta del archivo CSV (classpath o ruta en disco).
     */
    @Transactional
    public void cargarUsuariosDesdeRecurso(String rutaRecurso) {
        try {
            Resource resource = resourceLoader.getResource(rutaRecurso);
            if (!resource.exists()) {
                log.warn("[UsuarioService] El archivo CSV '{}' no fue encontrado. Omitiendo carga inicial.", rutaRecurso);
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                cargarUsuariosDesdeInputStream(is, rutaRecurso);
            }
        } catch (Exception e) {
            log.error("[UsuarioService] Error al cargar usuarios desde '{}': {}", rutaRecurso, e.getMessage(), e);
        }
    }

    /**
     * Parsea un InputStream de CSV e inserta/actualiza los usuarios en la base de datos con contraseñas BCrypt.
     */
    @Transactional
    public void cargarUsuariosDesdeInputStream(InputStream inputStream, String origen) {
        int totalProcesados = 0;
        int totalCreados = 0;
        int totalActualizados = 0;
        int totalFallidos = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linea = reader.readLine();
            if (linea == null || linea.isBlank()) {
                log.warn("[UsuarioService] El archivo CSV desde '{}' está vacío.", origen);
                return;
            }

            // Quitar caracter BOM si existe
            if (linea.startsWith("\uFEFF")) {
                linea = linea.substring(1);
            }

            // Detectar delimitador (coma o punto y coma)
            String delimitador = linea.contains(";") ? ";" : ",";
            Map<String, Integer> columnas = parsearEncabezados(linea, delimitador);
            validarEncabezadosRequeridos(columnas);

            int numeroLinea = 1;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }

                totalProcesados++;
                try {
                    String[] valores = linea.split(delimitador, -1);

                    String rut = extraerValor(valores, columnas, "rut");
                    String nombre = extraerValor(valores, columnas, "nombre");
                    String correo = extraerValor(valores, columnas, "correo").trim().toLowerCase();
                    String contrasena = extraerValor(valores, columnas, "contrasena");
                    String tipoUsuario = extraerValor(valores, columnas, "tipo_usuario").trim().toUpperCase();

                    if (rut.isBlank() || nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || tipoUsuario.isBlank()) {
                        throw new IllegalArgumentException("Campos obligatorios vacíos.");
                    }

                    // Buscar si ya existe por correo o RUT
                    Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreoInstitucional(correo);
                    if (usuarioExistente.isEmpty()) {
                        usuarioExistente = usuarioRepository.findByRut(rut);
                    }

                    if (usuarioExistente.isPresent()) {
                        Usuario usuario = usuarioExistente.get();
                        usuario.setNombre(nombre);
                        usuario.setRut(rut);
                        usuario.setCorreoInstitucional(correo);
                        usuario.setContrasena(passwordEncoder.encode(contrasena));
                        usuario.setEstadoCuenta(true);
                        usuarioRepository.save(usuario);
                        totalActualizados++;
                        log.debug("[UsuarioService] Usuario actualizado: {}", correo);
                    } else {
                        Usuario nuevoUsuario = instanciarUsuarioPorTipo(tipoUsuario);
                        nuevoUsuario.setRut(rut);
                        nuevoUsuario.setNombre(nombre);
                        nuevoUsuario.setCorreoInstitucional(correo);
                        nuevoUsuario.setContrasena(passwordEncoder.encode(contrasena));
                        nuevoUsuario.setEstadoCuenta(true);
                        usuarioRepository.save(nuevoUsuario);
                        totalCreados++;
                        log.info("[UsuarioService] Usuario creado ({}) -> {}", tipoUsuario, correo);
                    }

                } catch (Exception e) {
                    totalFallidos++;
                    log.error("[UsuarioService] Error en línea {}: {}", numeroLinea, e.getMessage());
                }
            }

            log.info("[UsuarioService] Carga CSV finalizada desde '{}': {} procesados, {} creados, {} actualizados, {} fallidos.",
                    origen, totalProcesados, totalCreados, totalActualizados, totalFallidos);

        } catch (Exception e) {
            log.error("[UsuarioService] Error procesando el archivo CSV: {}", e.getMessage(), e);
        }
    }

    private Map<String, Integer> parsearEncabezados(String encabezado, String delimitador) {
        Map<String, Integer> mapa = new HashMap<>();
        String[] headers = encabezado.split(delimitador);
        for (int i = 0; i < headers.length; i++) {
            String col = headers[i].trim().toLowerCase().replace("\"", "");
            if (col.equals("rut") || col.equals("identificador")) {
                mapa.put("rut", i);
            } else if (col.equals("nombre") || col.equals("nombre_completo") || col.equals("nombres")) {
                mapa.put("nombre", i);
            } else if (col.contains("correo") || col.contains("email") || col.equals("mail")) {
                mapa.put("correo", i);
            } else if (col.contains("contrasena") || col.contains("password") || col.contains("clave")) {
                mapa.put("contrasena", i);
            } else if (col.contains("tipo") || col.contains("rol") || col.contains("perfil")) {
                mapa.put("tipo_usuario", i);
            }
        }
        return mapa;
    }

    private void validarEncabezadosRequeridos(Map<String, Integer> columnas) {
        List<String> faltantes = new ArrayList<>();
        if (!columnas.containsKey("rut")) faltantes.add("rut");
        if (!columnas.containsKey("nombre")) faltantes.add("nombre");
        if (!columnas.containsKey("correo")) faltantes.add("correo");
        if (!columnas.containsKey("contrasena")) faltantes.add("contrasena");
        if (!columnas.containsKey("tipo_usuario")) faltantes.add("tipo_usuario");

        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV no contiene las columnas requeridas: " + String.join(", ", faltantes));
        }
    }

    private String extraerValor(String[] valores, Map<String, Integer> columnas, String clave) {
        Integer index = columnas.get(clave);
        if (index == null || index >= valores.length) {
            return "";
        }
        return valores[index].trim().replace("\"", "");
    }

    private Usuario instanciarUsuarioPorTipo(String tipo) {
        return switch (tipo) {
            case "TESISTA", "ESTUDIANTE", "ALUMNO" -> new Tesista();
            case "PROFESOR", "DOCENTE", "GUIA" -> new Profesor();
            case "COORDINADOR", "COORDINADOR_DOCENTE", "ADMIN" -> new CoordinadorDocente();
            default -> throw new IllegalArgumentException("Tipo de usuario no reconocido: '" + tipo + "'. Valores permitidos: TESISTA, PROFESOR, COORDINADOR.");
        };
    }
}