package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "configuracion_tienda")
@Getter
@Setter
@NoArgsConstructor
public class StoreSettings {
    @Id
    private Integer id;

    @Column(name = "nombre_admin", nullable = false, length = 100)
    private String nombreAdmin;
    @Column(name = "correo_admin", nullable = false, length = 150)
    private String correoAdmin;
    @Column(name = "envio_cdmx", nullable = false, precision = 10, scale = 2)
    private BigDecimal envioCdmx;
    @Column(name = "envio_interior", nullable = false, precision = 10, scale = 2)
    private BigDecimal envioInterior;
    @Column(nullable = false)
    private Boolean pagoTarjeta;
    @Column(nullable = false)
    private Boolean pagoPaypal;
    @Column(nullable = false)
    private Boolean pagoSpei;
    @Column(nullable = false)
    private Boolean pagoOxxo;
    @Column(nullable = false)
    private Boolean notificarPedidos;
    @Column(nullable = false)
    private Boolean notificarStock;
    @Column(nullable = false)
    private Boolean resumenSemanal;
}
