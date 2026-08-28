package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {

    // CREA UNA LISTA DE LOS ITEMS DENTRO DEL CARRITO POR SU ID
    List<CartDetail> findByCarritoIdCarrito(Integer idCarrito);

    // BUSCAR SI UN PRODUCTO ESTA EN UN CARRITO
    Optional<CartDetail> findByCarritoIdCarritoAndProductoIdProducto(
            Integer idCarrito,
            Integer idProducto
    );

    // ELIMINAR ITEMS DEL CARRITO
    void deleteByCarritoIdCarrito(Integer idCarrito);
}