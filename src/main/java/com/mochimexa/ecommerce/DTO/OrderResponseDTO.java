package com.mochimexa.ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDTO {
    private Integer idPedido;
    private String solicitudId;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal subTotal;
    private BigDecimal costoEnvio;
    private BigDecimal descuento;
    private BigDecimal total;
    private String metodoPago;
    private String estadoPago;
    private UserResponseDTO usuario;
    private AddressResponseDTO direccion;
    private List<OrderItemResponseDTO> items;
}
