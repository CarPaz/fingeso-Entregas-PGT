package com.fingesoHito3Grupo7.entregas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation;
    public FileStorageService(@Value("${app.storage.location:./storage/entregas}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
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
            Path destino = this.rootLocation.resolve(nombreUnico);

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

// Metodo que retorna la ruta absoluta de un archivo ya guardado, a partir de su nombre único
public Path obtenerRutaArchivo(String nombreArchivo) {
    if (nombreArchivo == null || nombreArchivo.isBlank()) {
        throw new RuntimeException("El nombre del archivo es obligatorio.");
    }
    return this.rootLocation.resolve(nombreArchivo).normalize();
}    
    
}
