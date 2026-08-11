package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "asistencias_usuario_libre")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AsistenciaUsuarioLibre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_libre_id", referencedColumnName = "id")
    private Usuario usuarioLibre;

    @ManyToOne
    @JoinColumn(name = "patrocinador_id", referencedColumnName = "id")
    private Usuario patrocinador; // Solo suma al patrocinador directo, no a la red ascendente

    private LocalDate fechaAsistencia;
    private boolean contabilizadoParaPunto = false; // Cada 10 asistencias otorga 1 punto al patrocinador
}