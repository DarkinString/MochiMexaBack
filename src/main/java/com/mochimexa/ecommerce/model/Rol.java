package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "rol_asignado", nullable = false, length = 50, unique = true)
    private String rolAsignado;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;
}
