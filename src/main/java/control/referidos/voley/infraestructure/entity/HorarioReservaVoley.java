package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "horarios_reserva_voley")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HorarioReservaVoley {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String equipoCampo; // Team A o Team B (Campo de Arena Xtreme Volleyball)
    private LocalDateTime fechaHora;
    private boolean disponible;
    private String contactoReserva; // Ej: OMAR DIAZ / ALEX DÍAZ

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}