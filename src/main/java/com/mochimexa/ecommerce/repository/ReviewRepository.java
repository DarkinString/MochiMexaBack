package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    //BUSCAR RESEÑAS POR PRODUCTO
    List<Review> findByProductoIdProducto(Long idProducto);

    //BUSCAR RESEÑAS POR USUARIO
    List<Review> findByUsuarioIdUsuario(Long idUsuario);
}
