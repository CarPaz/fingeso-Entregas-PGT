package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
public class Entrega {
//VS permite seleccionar source action y generate getters and setter para los atributos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getNombreAlmacenado() {
        return nombreAlmacenado;
    }

    public void setNombreAlmacenado(String nombreAlmacenado) {
        this.nombreAlmacenado = nombreAlmacenado;
    }

    public String getRutaRelativa() {
        return rutaRelativa;
    }

    public void setRutaRelativa(String rutaRelativa) {
        this.rutaRelativa = rutaRelativa;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getProcesoTesisId() {
        return procesoTesisId;
    }

    public void setProcesoTesisId(Long procesoTesisId) {
        this.procesoTesisId = procesoTesisId;
    }

    public Long getHitoEntregaId() {
        return hitoEntregaId;
    }

    public void setHitoEntregaId(Long hitoEntregaId) {
        this.hitoEntregaId = hitoEntregaId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único

    @Column(name = "tipo_entrega", nullable = false)
    private String tipoEntrega; // AVANCE o FINAL

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora; // Momento efectivo de presentación

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal; // Nombre informado por el archivo del usuario

    @Column(name = "nombre_almacenado", nullable = false, unique = true)
    private String nombreAlmacenado; // Nombre interno único

    @Column(name = "ruta_relativa", nullable = false)
    private String rutaRelativa; // Ruta o identificador usado para recuperar el archivo

    @Column(name = "mime_type", nullable = false)
    private String mimeType; // Tipo MIME validado

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes; // Tamaño real del archivo

    @Column(nullable = false)
    private String estado; // Estado definido por el proceso de negocio

    // Al ser un modelo modular, se guarda el ID 
    // en lugar de la relación directa @ManyToOne si las entidades están en módulos separados.
    @Column(name = "proceso_tesis_id", nullable = false)
    private Long procesoTesisId; // Clave foránea hacia ProcesoTesis

    @Column(name = "hito_entrega_id")
    private Long hitoEntregaId; // Clave foránea hacia HitoEntrega

    // JPA exige tener un constructor vacío
    public Entrega() {
    }

    
}