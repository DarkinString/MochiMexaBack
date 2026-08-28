package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {

    // BUSCAR ENVIO POR NUMERO DE GUIA
    Optional<Shipping> findByNumeroGuia(String numeroGuia);

    // BUSCAR ENVIO POR PEDIDO
    Optional<Shipping> findByPedidoIdPedido(Integer idPedido);
}