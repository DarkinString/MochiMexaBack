package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "carrito_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito_detalle")
    private Long idCarritoDetalle;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @ManyToOne
    @JoinColumn(name = "id_carrito", nullable = false)
    @ToString.Exclude
    private Cart carrito;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @ToString.Exclude
    private Product producto;

}
