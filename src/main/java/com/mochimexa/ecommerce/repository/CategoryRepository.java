package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //BUSCAR CATEGORIA POR NOMBRE
    Optional<Category> findByNombreIgnoreCase(String nombre);

    //LISTAR CATEGORIAS ACTIVAS
    List<Category> findByActivoTrue();
}
