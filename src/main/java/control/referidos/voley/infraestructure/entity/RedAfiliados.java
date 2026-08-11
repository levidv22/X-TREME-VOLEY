package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "red_afiliados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RedAfiliados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patrocinador_id", referencedColumnName = "id")
    private Usuario patrocinador; // Patrocinador directo

    @ManyToOne
    @JoinColumn(name = "referido_id", referencedColumnName = "id")
    private Usuario referido; // Usuario referido o integrado por derrame

    private int nivel; // Nivel dentro de la estructura unilevel (1 al 10)

    private boolean esDerrame; // Indica si fue ubicado por regla de derrame (hasta 3er nivel o red profunda)
}