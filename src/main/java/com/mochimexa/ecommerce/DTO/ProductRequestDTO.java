package com.mochimexa.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/*
 * DTO = Data Transfer Object.
 * Representa el JSON que el cliente envía al crear o actualizar un producto.
 * No es @Entity porque este objeto no representa una tabla de MySQL.
 */
@Getter
@Setter
public class ProductRequestDTO {

    private String nombre;
    private String slug;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String marca;
    private String imagen;
    private String badge;
    private Boolean activo;
    private Integer idCategoria;

    public ProductRequestDTO() {}
}
