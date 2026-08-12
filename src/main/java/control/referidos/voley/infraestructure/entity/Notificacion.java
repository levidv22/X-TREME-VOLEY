package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private String urlDestino; // Ej: /inscripciones/cliente/5
    private boolean leido = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Si es null, aplica para todos los ADMINS
}