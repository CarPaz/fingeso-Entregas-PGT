package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
public class Entrega {

    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Long idEntrega;

    // Permite diferenciar una entrega de AVANCE de una entrega FINAL.
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private String tipoEntrega;

    // Momento efectivo en el que se registró la entrega.
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Nombre que tenía el archivo cuando fue seleccionado.
    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    // Nombre interno y único utilizado para almacenar el archivo.
    @Column(
        name = "nombre_almacenado",
        nullable = false,
        unique = true,
        length = 255
    )
    private String nombreAlmacenado;

    // Ubicación utilizada para recuperar el archivo.
    @Column(name = "ruta_relativa_archivo", nullable = false, length = 500)
    private String rutaRelativaArchivo;

    // Tipo MIME validado. Para este proyecto debería ser application/pdf.
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    // Tamaño real del archivo expresado en bytes.
    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    // Ejemplos: PENDIENTE_REVISION, APROBADA o CORRECCION_REQUERIDA.
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    /*
     * Relación (FK):
     * entrega.id_proceso_tesis -> proceso_tesis.id_proceso_tesis
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_proceso_tesis",
        referencedColumnName = "id_proceso_tesis",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_entrega_proceso_tesis")
    )
    private ProcesoTesis procesoTesis;

    /*
     * Relación (FK):
     * entrega.id_hito_entrega -> hito_entrega.id_hito_entrega
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_hito_entrega",
        referencedColumnName = "id_hito_entrega",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_entrega_hito_entrega")
    )
    private HitoEntrega hitoEntrega;

    /*
     * Referencia al Estudiante/Tesista.
     * Seguirá siendo una referencia lógica hasta que exista su entidad.
     */
    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    // Número de versión del documento entregado.
    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion = 1;

    // Constructor vacío requerido por JPA.
    public Entrega() {
    }

    /*
     * Asigna los valores iniciales antes de guardar una entrega nueva.
     */
    @PrePersist
    protected void antesDeGuardar() { // Revisa que estén los datos obligatorios y asigna valores por defecto si es necesario
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

	// |GETTERS & SETTERS|
    public Long getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Long idEntrega) {
        this.idEntrega = idEntrega;
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

    public String getRutaRelativaArchivo() {
        return rutaRelativaArchivo;
    }

    public void setRutaRelativaArchivo(String rutaArchivo) {
        this.rutaRelativaArchivo = rutaArchivo;
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

    public ProcesoTesis getProcesoTesis() {
        return procesoTesis;
    }

    public void setProcesoTesis(ProcesoTesis procesoTesis) {
        this.procesoTesis = procesoTesis;
    }

    public HitoEntrega getHitoEntrega() {
        return hitoEntrega;
    }

    public void setHitoEntrega(HitoEntrega hitoEntrega) {
        this.hitoEntrega = hitoEntrega;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }
}