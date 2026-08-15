package com.fingesoHito3Grupo7.entregas.service;

import com.fingesoHito3Grupo7.entregas.domain.Entrega;
import com.fingesoHito3Grupo7.entregas.repository.EntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class EntregaService {
    //conexion con base de datos
    private final EntregaRepository entregaRepository;
    private final FileStorageService fileStorageService;
    // Constructor para inyectar el repositorio
    public EntregaService(EntregaRepository entregaRepository, FileStorageService fileStorageService) {
        this.entregaRepository = entregaRepository;
        this.fileStorageService = fileStorageService;
    }

    // obtener todas las entregas
    public List<Entrega> obtenerTodasLasEntregas() {
        return entregaRepository.findAll();
    }

    // crear una nueva entrega guardando primero el archivo fisico
    public Entrega crearEntrega(Entrega entrega, MultipartFile archivo) {
        // Guardar el archivo en la carpeta del computador si viene adjunto
        if (archivo != null && !archivo.isEmpty()) {
            String rutaArchivoGuardado = fileStorageService.guardarArchivo(archivo);
            
            //Inyectarle la ruta obtenida al objeto entrega
            entrega.setRutaArchivo(rutaArchivoGuardado);
        }

        //Guardar la entrega completa con la ruta del archivo en PostgreSQL
        return entregaRepository.save(entrega);
    }
}