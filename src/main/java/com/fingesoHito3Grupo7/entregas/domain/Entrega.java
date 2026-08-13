package com.fingesoHito3Grupo7.entregas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
public class Entrega {

    // Clave primaria autogenerada.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Entrega")
    private Long id;

    // Permite diferenciar una entrega de AVANCE de una entrega FINAL.
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private String tipoEntrega;

    // Momento efectivo en el que se registró la entrega.
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Nombre que tenía el archivo cuando lo seleccionó el estudiante.
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
    @Column(name = "ruta_relativa", nullable = false, length = 500)
    private String rutaRelativa;

    // Tipo MIME del archivo. Para este proyecto debería ser application/pdf.
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    // Tamaño real del archivo expresado en bytes.
    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    // Ejemplos: PENDIENTE_REVISION, APROBADA o CORRECCION_REQUERIDA.
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    /*
     * Referencia al proceso de tesis.
     * Será FK hacia proceso_tesis(id) si la tabla está en la misma base.
     */

    @ManyToOne // un proceso de tesis puede estar asociado a varias entregas 
    @JoinColumn(name = "id_proceso_tesis", nullable = false)
    private ProcesoTesis procesoTesis;

    /*
     * Referencia al hito correspondiente.
     * Será FK hacia hito_entrega(id) si la tabla está en la misma base.
     */
    @ManyToOne // un hito puede estar asociado a varias entregas 
    @JoinColumn(name = "id_hito_entrega", nullable = false)
    private HitoEntrega hitoEntrega;

    /*
     * Referencia al estudiante que presentó el documento.
     * En el modelo conceptual, Estudiante corresponde al Tesista.
     * Será FK hacia estudiante(id) o tesista(id), según el nombre acordado.
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
     * Se ejecuta automáticamente antes de guardar una entrega nueva.
     * Completa los valores iniciales que todavía no fueron asignados.
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

    // |Getters y Setters|

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