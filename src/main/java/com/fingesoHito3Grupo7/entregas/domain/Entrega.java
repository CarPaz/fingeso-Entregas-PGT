package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Permite diferenciar una entrega de AVANCE de una entrega FINAL.
    @Column(name = "tipo_entrega", nullable = false)
    private String tipoEntrega;

    // Momento efectivo en el que se registró la entrega.
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Nombre que tenía el archivo cuando lo seleccionó el estudiante.
    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    // Nombre interno y único utilizado para almacenar el archivo.
    @Column(name = "nombre_almacenado", nullable = false, unique = true)
    private String nombreAlmacenado;

    // Ubicación utilizada para recuperar el archivo.
    @Column(name = "ruta_relativa", nullable = false)
    private String rutaRelativa;

    // Tipo MIME del archivo. Para este proyecto debería ser application/pdf.
    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    // Tamaño real del archivo expresado en bytes.
    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    // Ejemplos: PENDIENTE_REVISION, APROBADA o CORRECCION_REQUERIDA.
    @Column(name = "estado", nullable = false)
    private String estado;

    // ID del proceso de tesis proveniente del módulo correspondiente.
    @Column(name = "proceso_tesis_id", nullable = false)
    private Long procesoTesisId;

    // ID del hito al que corresponde la entrega.
    @Column(name = "hito_entrega_id", nullable = false)
    private Long hitoEntregaId;

    // ID del estudiante que presentó el documento.
    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    // La primera entrega será versión 1.
    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion = 1;

    // Constructor vacío requerido por JPA.
    public Entrega() {
    }

    /*
     * Se ejecuta automáticamente antes de guardar una entrega nueva.
     * Completa los valores iniciales cuando todavía no fueron asignados.
     */
    @PrePersist
    protected void antesDeGuardar() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }

        if (estado == null || estado.isBlank()) {
            estado = "PENDIENTE_REVISION";
        }

        if (numeroVersion == null) {
            numeroVersion = 1;
        }
    }

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

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }
}