package com.mochimexa.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/*
 * DTO = Data Transfer Object.
 * Representa el JSON que el cliente envía al crear o actualizar un usuario.
 * No es @Entity porque este objeto no representa una tabla de MySQL.
 */
@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nombre;
    @NotBlank
    @Size(max = 100)
    private String apellido;
    @NotBlank
    @Email
    @Size(max = 150)
    private String correo;
    @NotBlank
    @Size(min = 8, max = 128)
    private String contrasenia;
    private String telefono;
    private String foto;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    private Integer idRol;

    public UserRequestDTO() {}
}
