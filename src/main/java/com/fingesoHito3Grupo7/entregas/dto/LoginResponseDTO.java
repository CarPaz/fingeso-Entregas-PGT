package com.fingesoHito3Grupo7.entregas.dto;

/**
 * DTO de salida para el endpoint POST /api/auth/login.
 * Lo que recibe el frontend al autenticarse con éxito.
 * NO incluye la contraseña ni datos sensibles.
 */
public class LoginResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private String rol; // valor del @DiscriminatorValue: "TESISTA", "PROFESOR", "COORDINADOR_DOCENTE"
    private String token; // Token JWT para autenticar requests posteriores

    // Constructor vacío
    public LoginResponseDTO() {}

    public LoginResponseDTO(Long idUsuario, String nombre, String correo, String rol, String token) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.token = token;
    }
    //Getters y Setters
    
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
