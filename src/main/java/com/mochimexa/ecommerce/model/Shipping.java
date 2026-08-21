package com.mochimexa.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "envió")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envió")
    private Long idEnvio;

    @Column(name = "empresa_envió", length = 100)
    private String empresaEnvio;

    @Column(name = "numero_guia", nullable = false, length = 150)
    private String numeroGuia;

    @Column(name = "fecha_envió", nullable = false)
    private LocalDateTime fecha_envio;

    @Column(name = "estado", nullable = false, length = 45)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Order pedido;
}
