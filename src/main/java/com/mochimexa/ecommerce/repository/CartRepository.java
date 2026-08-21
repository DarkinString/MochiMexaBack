package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{

    //BUSCAR CARRITO ACTIVO/PENDIENTE DE UN USUARIO
    Optional<Cart> findByUsuarioIdUsuarioAndEstado(Long idUsuario, String estado);
}
