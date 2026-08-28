package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // DETALLES DE PEDIDO POR ID
    List<OrderDetail> findByPedidoIdPedido(Integer idPedido);
}