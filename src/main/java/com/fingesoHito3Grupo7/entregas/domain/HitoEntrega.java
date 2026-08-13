package com.fingesoHito3Grupo7.entregas.domain;



import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hito_entrega")
public class HitoEntrega {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hito_entrega", nullable = false)
    private Long idHitoEntrega;


    @Column(name = "nombre",length = 100, nullable = false)
    private String nombre;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    //formato pdf ?
    @Column(name ="formato", nullable = false)
    private String formato;

    // no estoy seguro si tenemos que avisar al ususario si se acerca un hito
    // ejemplos Atrasado Enviado Pendiente
    @Column(name = "estado", nullable = false)
    private String estado;

    //relaciones

    //puede estar relacionado con mas de una entrega
    @OneToMany(mappedBy = "hitoEntrega", cascade = CascadeType.ALL, orphanRemoval = true) // tener cuidado con cascade!! si se borra un proceso se borraran sus entregas en la bd
    private List<Entrega> entregas = new ArrayList<>(); //lista de entregas

    //hito entrega esta relacionado con proceso de tesis, el cual puede tener varios hitos
    @ManyToOne
    @JoinColumn(name = "id_proceso_tesis", nullable = false)
    private ProcesoTesis procesoTesis;
    
    // Constructor vacío requerido por JPA.
    public HitoEntrega() {
    }

    //getters y setters 
    public Long getIdHitoEntrega() {
        return idHitoEntrega;
    }

    public void setIdHitoEntrega(Long idHitoEntrega) {
        this.idHitoEntrega = idHitoEntrega;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Entrega> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<Entrega> entregas) {
        this.entregas = entregas;
    }

    public ProcesoTesis getProcesoTesis() {
        return procesoTesis;
    }

    public void setProcesoTesis(ProcesoTesis procesoTesis) {
        this.procesoTesis = procesoTesis;
    }

    


}
