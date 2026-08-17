package com.fingesoHito3Grupo7.entregas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation;
    public FileStorageService(@Value("${app.storage.location:./storage/entregas}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
    }
    //Metodo que guarda el archivo físico en el disco duro y retorna la ruta
    public String guardarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No se puede guardar un archivo vacío.");
        }
        try {
            // Crea la carpeta de almacenamiento si no existe
            Files.createDirectories(this.rootLocation);

            // limpiar el nombre original del archivo
            String nombreOriginal = file.getOriginalFilename();
            nombreOriginal = nombreOriginal.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            //General nombre unico
            String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
            //Construir ruta de destino final
            Path destino = this.rootLocation.resolve(nombreUnico).normalize();
            if (!destino.startsWith(this.rootLocation)) {
                throw new SecurityException("La ruta de almacenamiento no es válida.");
            }

            // Copia el archivo recibido a la carpeta destino
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retornar el nombre unico(ruta relativa)
            return nombreUnico;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en disco", e);
        }
    }

    /*
     * Recupera un archivo almacenado sin permitir desplazamientos fuera
     * del directorio configurado.
     */
    public Resource recuperarArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            throw new IllegalArgumentException("La entrega no tiene un archivo asociado.");
        }

        try {
            Path ruta = rootLocation.resolve(rutaRelativa).normalize();
            if (!ruta.startsWith(rootLocation)) {
                throw new SecurityException("La ruta solicitada no es válida.");
            }

            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new IllegalArgumentException("El archivo solicitado no está disponible.");
            }
            return recurso;
        } catch (IOException exception) {
            throw new RuntimeException("No fue posible recuperar el archivo.", exception);
        }
    }

    
}
