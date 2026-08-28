package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // BUSCAR RESEÑAS POR PRODUCTO
    List<Review> findByProductoIdProducto(Integer idProducto);

    // BUSCAR RESEÑAS POR USUARIO
    List<Review> findByUsuarioIdUsuario(Integer idUsuario);

    Optional<Review> findByProductoIdProductoAndUsuarioIdUsuario(Integer idProducto, Integer idUsuario);
}
