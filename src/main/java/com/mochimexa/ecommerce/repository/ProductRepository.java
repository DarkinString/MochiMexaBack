package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //BUSCAR PRODUCTOS POR NOMBRE
    List<Product> findByNameContainingIgnoreCase(String nombre);

    //BUSCAR PRODUCTOS CON PRECIO MAYOR AL INDICADO
    List<Product> findByPriceGreaterThan(BigDecimal precio);

    // BUSCAR PRECIO POR RANGO
    List<Product> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    //BUSCAR PRODUCTOS POR MARCA
    //List<Product> findByMarcaIgnoreCase(String marca); //FALTA AGREGAR MARCA A CLASE PRODUCTO

    //BUSCAR PRODUCTOS POR EL ID DE SU CATEGORIA
    List<Product> findByCategoryId(Long idCategoria);

}
