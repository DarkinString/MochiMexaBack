package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dirección")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dirección")
    private Long IdDireccion;

    @Column(name ="calle", nullable = false,length = 150)
    private String calle;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "colonia",nullable = false, length = 20)
    private String colonia;

    @Column(name = "codigo_postal", nullable = false, length = 10)
    private String codigoPostal;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "estado", nullable = false,length = 10)
    private String estado;

    @Column(name = "referencia",length = 150)
    private String referencia;

    @ManyToOne
    @JoinColumn(name = "id_usuario",nullable = false)
    @ToString.Exclude
    private User usuario;

}
