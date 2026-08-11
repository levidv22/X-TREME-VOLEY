package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true, length = 8)
    private String dni;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private String fotoUrl;
    private String departamento;
    private String provincia;
    private String distrito;

    @Column(unique = true, length = 20)
    private String codigoReferido; // Código único que comparte el usuario

    @Enumerated(EnumType.STRING)
    private Rol rol; // ADMIN, SOCIO_REGULAR, SOCIO_LIDER, USUARIO_LIBRE

    @Enumerated(EnumType.STRING)
    private TipoUsuarioRed tipoRed; // LIBRE, SOCIO_PRIMER_NIVEL (3x3), SOCIO_LIDER_SEGUNDO_NIVEL (10x10)

    @Enumerated(EnumType.STRING)
    private RangoCarrera rangoActual = RangoCarrera.NINGUNO;

    private boolean activoMes = false; // Estado de inscripción mensual activa (S/. 60) para cobrar bonos

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Membresia membresia;
}