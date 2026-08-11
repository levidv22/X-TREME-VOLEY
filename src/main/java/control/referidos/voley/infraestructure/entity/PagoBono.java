package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_bonos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PagoBono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double monto;
    private LocalDate fechaPago;
    private String tipoBono; // Patrocinio
    private String metodoPago; // Yape, Transferencia, Efectivo, Saldo

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "inscripcion_id", referencedColumnName = "id")
    private InscripcionMensual inscripcionMensual;
}