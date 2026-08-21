package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String  apellido;

    @Column(name = "correo", nullable = false, length = 150, unique = true)
    private String correo;

    @ToString.Exclude
    @Column(name = "contrasenia", nullable = false, length = 100)
    private String contrasenia;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    @ToString.Exclude
    private Rol rol;

}
