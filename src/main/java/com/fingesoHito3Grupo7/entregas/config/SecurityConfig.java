package com.fingesoHito3Grupo7.entregas.config;

import com.fingesoHito3Grupo7.entregas.security.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 *  - El frontend conserva el JWT y la información necesaria para navegar.
 *  - El backend obtiene la identidad y el rol exclusivamente del JWT validado.
 *  - Cada petición protegida debe incluir un JWT válido.
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
                // Solamente un tesista puede registrar entregas de avance o finales
                .requestMatchers(HttpMethod.POST, "/api/entregas", "/api/entregas/**")
                    .hasRole("TESISTA")
                // Los tres roles consultan solamente las entregas autorizadas por el servicio
                .requestMatchers(HttpMethod.GET, "/api/entregas", "/api/entregas/**")
                    .hasAnyRole("TESISTA", "PROFESOR", "COORDINADOR")
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
     * JwtFilter es un componente de Spring y también se agrega manualmente a la
     * cadena de Security. Se desactiva su registro como filtro web general para
     * evitar que se ejecute antes de que Spring Security prepare su contexto.
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> desactivarRegistroWebDeJwtFilter() {
        FilterRegistrationBean<JwtFilter> registro = new FilterRegistrationBean<>(jwtFilter);
        registro.setEnabled(false);
        return registro;
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
