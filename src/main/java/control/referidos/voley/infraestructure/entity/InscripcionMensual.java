package control.referidos.voley.infraestructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscripciones_mensuales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class InscripcionMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "monto_total")
    private double montoTotal = 40.0;

    @Column(name = "monto_pagado")
    private double montoPagado = 0.0;

    @Column(name = "periodo_mes")
    private String periodoMes;

    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}