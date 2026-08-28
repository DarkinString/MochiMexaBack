package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedido_direccion")
@IdClass(OrderAddressId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderAddress {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_direccion", nullable = false)
    @ToString.Exclude
    private Address direccion;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Order pedido;
}