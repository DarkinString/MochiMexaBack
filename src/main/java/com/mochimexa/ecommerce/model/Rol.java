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

    /**
     * Compatibilidad con la base de datos existente, donde la tabla rol
     * conserva esta columna obligatoria además de rol_asignado.
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;
}
