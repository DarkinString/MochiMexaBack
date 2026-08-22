package com.mochimexa.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class OrderAddressId implements Serializable {
    private Long direccion;
    private Long pedido;
}
