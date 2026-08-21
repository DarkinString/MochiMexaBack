package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Long>{

    //PROMOCIONES ASOCIADAS A PRODUCTO
    List<ProductPromotion> findByProductoIdProducto(Long idProducto);

    //PRODUCTOS INCLUIDOS EN PROMOCION
    List<ProductPromotion> findByPromocionesIdPromociones(Long idPromociones);
}
