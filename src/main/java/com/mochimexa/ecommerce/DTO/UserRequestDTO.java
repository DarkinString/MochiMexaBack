package com.mochimexa.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * DTO = Data Transfer Object.
 * Representa el JSON que el cliente envía al crear o actualizar un producto.
 * No es @Entity porque este objeto no representa una tabla de MySQL.
 */
@Getter
@Setter
public class UserRequestDTO {

    private String nombre;
    private String apellido;
    private String correo;
    private String contrasenia;
    private String telefono;
    //private Long categoryId;
    //private LocalDateTime fechaRegistro;
    //private Boolean activo;
    //private String rol;
    private Long idRol;



    public UserRequestDTO() {}

}