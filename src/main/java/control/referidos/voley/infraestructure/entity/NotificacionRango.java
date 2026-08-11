package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones_rango")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRango {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String rangoAlcanzado;
    private String premio;
    private LocalDateTime fechaNotificacion;
    private boolean leido = false;
}
