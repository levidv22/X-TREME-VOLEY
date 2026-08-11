package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "puntos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Puntos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int puntosMensuales;
    private int puntosTotalesAcumulados;
    private LocalDate periodoMes; // Año y mes de los puntos

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}