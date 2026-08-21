package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aspectj.lang.annotation.Around;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promociones")
    private Long idPromociones;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripción", nullable = false, length = 300)
    private String descripcion;

    @Column(name = "tipo_descuento", nullable = false, length = 45)
    private String tipoDescuento;

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo;


}
