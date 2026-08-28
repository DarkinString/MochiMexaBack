package com.mochimexa.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contrasenia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contrasenia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrasenia")
    private Integer idContrasenia;

    @Column(name = "password_hash", nullable = false, length = 255)
    @ToString.Exclude
    @JsonIgnore
    private String passwordHash;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private User usuario;
}