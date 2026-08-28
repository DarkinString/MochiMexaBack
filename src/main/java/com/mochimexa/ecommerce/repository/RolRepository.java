package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // BUSCAR ROL POR NOMBRE ASIGNADO
    Optional<Rol> findByRolAsignadoIgnoreCase(String rolAsignado);
}