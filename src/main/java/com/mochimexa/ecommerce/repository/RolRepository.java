package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    //BUSCAR ROL POR NOMBRE
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    //Optional<Rol> findByIdRol(Long idRol);
}
