package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalIdCache;

@Entity
@Table(name = "pedido_detalle")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_detalle")
    private Long idPedidoDetalle;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Product producto;

}
