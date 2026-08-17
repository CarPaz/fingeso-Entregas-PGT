package com.fingesoHito3Grupo7.entregas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint POST /api/auth/login.
 * Contiene las credenciales que envía el frontend.
 */
public class LoginRequestDTO {

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "El formato del correo no es válido.")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena;

    // Constructor vacío requerido para deserialización JSON
    public LoginRequestDTO() {}

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
