package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //PEDIDOS DE UN USUARIO
    List<Order> findByUsuarioIdUsuario(Long idUsuario);

    //PEDIDOS POR ESTADO
    List<Order> findByEstado(String estado);

}
