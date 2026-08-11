package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenUrl;
    private int stock;
}