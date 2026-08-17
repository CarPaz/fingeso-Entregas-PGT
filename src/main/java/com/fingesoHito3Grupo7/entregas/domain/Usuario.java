package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //para heredar a Tesista, Profesor y coordinador
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    // RUT del usuario, debe ser único y no nulo
    @Column(nullable = false, unique = true)
    private String rut;
    // Nombre del usuario.
    @Column(nullable = false, length = 50)
    private String nombre;
    //correo institucional unico para cada usuario
    @Column(name = "correo_institucional", nullable = false, unique = true)
    private String correoInstitucional;

    // Contraseña cifrada del usuario. ver como se implementa el cifrado y seguridad !!!!!!!!!!!
    @Column(nullable = false)
    private String contrasena; 
    // Indica si la cuenta de usuario está activa o desactivada. true = activa, false = desactivada
    @Column(name = "estado_cuenta", nullable = false)
    private Boolean estadoCuenta = true;

    //constructor vacio requerido por JPA
    public Usuario() {}


    // Getters y Setters
    
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(Boolean estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    
    



    
}