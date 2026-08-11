package control.referidos.voley.dto;

import control.referidos.voley.infraestructure.entity.RangoCarrera;
import control.referidos.voley.infraestructure.entity.TipoUsuarioRed;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter @lombok.Setter @lombok.NoArgsConstructor @lombok.AllArgsConstructor
public class NodoRedDto {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String dni;
    private String telefono;
    private String fotoUrl;
    private TipoUsuarioRed tipoRed;
    private RangoCarrera rangoActual;
    private int nivel;
    private boolean esDerrame;
    private int totalReferidos;
    private int puntosTotales;
    private List<NodoRedDto> hijos = new ArrayList<>();
}
