package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // BUSCAR PRODUCTOS POR NOMBRE
    List<Product> findByNombreContainingIgnoreCase(String nombre);

    // BUSCAR PRODUCTOS CON PRECIO MAYOR AL INDICADO
    List<Product> findByPrecioGreaterThan(BigDecimal precio);

    // BUSCAR PRECIO POR RANGO
    List<Product> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    // BUSCAR PRODUCTOS POR MARCA
    List<Product> findByMarcaIgnoreCase(String marca);

    // BUSCAR PRODUCTOS POR EL ID DE SU CATEGORIA
    List<Product> findByCategoriaIdCategoria(Integer idCategoria);
}
