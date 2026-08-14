package com.fingesoHito3Grupo7.entregas.service;

//Para Errores de lectura o escritura en el disco
import java.io.IOException;
//Para leer y copiar archivos al disco duro
import java.io.InputStream;
import java.nio.file.Files;
//Librerias nativas de java para interactuar con el disco duro local
//Para manejar rutas de carpetas, directorios y borrar archivos del sist
import java.nio.file.Path;
import java.nio.file.Paths;
//Para trabajar con archivos ya existentes
import java.nio.file.StandardCopyOption;
//Para generar los Universally Unique Identifiers para los archivos
import java.util.UUID;

//Librerias para el Logger de error
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//Para usar @Value para leer los datos de application.properties
import org.springframework.beans.factory.annotation.Value;
//Para manejar archivos 
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
//Librerias de Spring Framework
//Para notación @Service
import org.springframework.stereotype.Service;
//Para manipular textos
import org.springframework.util.StringUtils;
//Para leer los datos del archivo que se subio desde el front
import org.springframework.web.multipart.MultipartFile;

//Para asegurar la creacion de la carpeta de guardado
import jakarta.annotation.PostConstruct;


@Service
public class FileStorageService {
    //Inicializacion del logger asociado
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    //Se ingresa la ruta configurada en application.properties (configurada localmente en application-local.properties)
    @Value("${APP_STORAGE_LOCATION}")
    private String storageLocation;
    private Path rootLocation;

    //Se crea la carpeta de forma automatica al iniciar el back si es que aun no existe (se ejecuta una sola vez)
    @PostConstruct
    public void init(){
        try {
            //transformar la varible de texto storageLocation en un Path 
            this.rootLocation = Paths.get(storageLocation);
            //creacion de la carpeta :3
            Files.createDirectories(rootLocation);
        } catch (IOException e){
            //en caso de error, se "atrapa" y se detiene el arranque del Spring Boot y muestra error en la terminar
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento ", e);
        }
    }

    //Valiacion, nombramiento y guardado del archivo PDF
    public String guardarArchivo(MultipartFile file){
        //Comprobar que el archivo no este vacio 
        if(file.isEmpty()){
            throw new IllegalArgumentException("El archivo esta vacio");
        }
        //Validar que el contenido corresponde a un archivo pdf
        //No se valida el tamño del archivo ya que eso de valida de forma automatica en application.properties de spring boot
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")){
            throw new IllegalArgumentException("El archivo debe ser de tipo PDF");
        }

        try {
            //Limpiar nombre original para impedir el uso de rutas maliciosas
            String nombreOriginalLimpio = StringUtils.cleanPath(file.getOriginalFilename());
            if (nombreOriginalLimpio.contains("..")){
                throw new SecurityException("El nombre del archivo contiene una ruta inválida");
            }
            //Se genera un nombre unico interno usando UUID
            String extension = nombreOriginalLimpio.substring(nombreOriginalLimpio.lastIndexOf("."));
            String nombreGenerado = UUID.randomUUID().toString() + extension;

            //Guardar archivo en el directorio configurado
            Path destino = this.rootLocation.resolve(nombreGenerado).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            //Devolver ruta relativa o indentificador
            return nombreGenerado;


        }catch(IOException e){
            throw new RuntimeException("Fallo al guardar el archivo",e);
        }

    }
    //Recupera el archivo para descarga o visualizacion
    public Resource recuperarArchivo(String nombreArchivo) {
        try {
            //Tomar la capeta base y le indenta al final el nombre del archivo a buscar y se asegura que la ruta este limpia
            Path rutaArchivo = rootLocation.resolve(nombreArchivo).normalize();
            //Se toma la ruta del archivo y se trasnforma en tipo URI y lo empaqueta en un UrlResouyrse para poder enviarlo mas adelante mediante HTTP
            Resource resource = new UrlResource(rutaArchivo.toUri());
            //Verificacion de existencia de archivos y permisos para leerlo
            if (resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("No se pudo leer el archivo: " + nombreArchivo);
            }
        //En caso de problema, se atrapa y muestra mensaje de error
        }catch (Exception e){
            throw new RuntimeException("Error al recuperar el archivo", e);
        }
    }

    //Eliminacion de archivo en caso de error en BD
    public void eliminarArchivo(String nombreArchivo){
        try{
            Path rutaArchivo = rootLocation.resolve(nombreArchivo).normalize();
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e){
            //Se registra el error en la consola 
            logger.error("Alerta: No se pudo eliminar el archivo '{}'", nombreArchivo,e);
            //Excepcion para el sistema
            throw new RuntimeException("No se pudo eliminar el archivo:" + nombreArchivo,e);

        }
    }

}
