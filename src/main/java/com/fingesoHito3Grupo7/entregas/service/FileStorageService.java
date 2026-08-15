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

            // Genera un nombre unico para evitar que dos archivos con el mismo nombre se sobrescriban
            String nombreOriginal = file.getOriginalFilename();
            String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
            
            Path destino = this.rootLocation.resolve(nombreUnico);

            // Copia el archivo recibido a la carpeta destino
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retorna la ruta en formato texto para que sea guardada en PostgreSQL
            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en disco", e);
        }
    }

    
}
