package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "método_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Order pedido;


}
