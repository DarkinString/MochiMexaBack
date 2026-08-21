package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>{

    //CREA UNA LISTA DE LOS ITEMS DENTRO DEL CARRITO POR SU ID
    List<CartDetail> findByCarritoIdCarrito(Long idCarrito);

    //BUSCAR SI UN PRODUCTO ESTA EN UN CARRITO
    Optional<CartDetail> findByCarritoIdCarritoAndProductoIdProducto(Long idCarrito, Long idProducto);

    //ELIMINAR ITEMS DEL CARRITO
    void deleteByCarritoIdCarrito(Long idCarrito);
}
