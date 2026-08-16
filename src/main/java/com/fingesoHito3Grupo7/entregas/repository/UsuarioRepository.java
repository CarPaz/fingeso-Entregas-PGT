package com.fingesoHito3Grupo7.entregas.repository;

import com.fingesoHito3Grupo7.entregas.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su correo institucional (usado en el login)
    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);
}
