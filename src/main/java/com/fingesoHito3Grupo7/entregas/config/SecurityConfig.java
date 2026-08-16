package com.fingesoHito3Grupo7.entregas.config;

import com.fingesoHito3Grupo7.entregas.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 *
 * Estrategia elegida: STATELESS (sin sesión HTTP en el servidor).
 *  - El frontend es responsable de guardar el idUsuario y el rol en localStorage.
 *  - No hay JWT por ahora; la autenticación se hace en cada request
 *    si el front envía los datos del usuario (o se amplía con JWT en el futuro).
 *
 * Rutas públicas (sin autenticación):
 *  - POST /api/auth/login   → punto de entrada del login
 *  - GET  /api/auth/me      → verificación de sesión activa (frontend)
 *
 * El resto de rutas requiere autenticación (evaluado en la rúbrica).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Bean de BCryptPasswordEncoder para hashear y verificar contraseñas.
     * Se inyecta en AuthService.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cadena de filtros de seguridad principal.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF: no aplica para APIs REST stateless
            .csrf(csrf -> csrf.disable())

            // Sin sesión HTTP en el servidor (stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Reglas de acceso a los endpoints
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de autenticación
                .requestMatchers("/api/auth/**").permitAll()
                // Actuator (salud de la app) accesible sin autenticar
                .requestMatchers("/actuator/**").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Deshabilitar login por formulario HTML (usamos REST, no MVC)
            .formLogin(form -> form.disable())
            // Deshabilitar autenticacion HTTP Basic
            .httpBasic(basic -> basic.disable())
            // Registrar el filtro JWT antes del filtro de usuario/contrasena
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean de UserDetailsService vacio para suprimir el usuario generado automaticamente
     * por Spring Security. La autenticacion real la maneja AuthService + JWT.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(); // Sin usuarios en memoria
    }
}
