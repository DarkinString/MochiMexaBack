package com.mochimexa.ecommerce.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "producto_promocion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_promocion")
    private Long idProductPromocion;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @ToString.Exclude
    private Product producto;



}
