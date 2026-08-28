package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // BUSCAR CARRITO ACTIVO/PENDIENTE DE UN USUARIO
    Optional<Cart> findByUsuarioIdUsuarioAndEstado(Integer idUsuario, String estado);
}