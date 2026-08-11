package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "membresias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin; // Vence en periodo de los últimos 5 días del año
    private double costoAnual; // S/. 60 inicial
    private boolean activa;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}
