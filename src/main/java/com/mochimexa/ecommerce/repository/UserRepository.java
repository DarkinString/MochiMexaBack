package com.mochimexa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mochimexa.ecommerce.model.User;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //BUSCAR USUARIO POR ID
    Optional<User> findByIdUsuario(Long idUsuario);

    //BUSCAR USUARIO POR CORREO
    Optional<User> findByCorreo(String correo);

    //VERIFICAR SI EXISTE EL CORREO
    boolean existsByCorreo (String correo);

    //BUSCAR USUARIOS ACTIVOS
    List<User> findByActivo(boolean activo);

    //BUSCAR USUARIOS POR ID DE SU ROL
    List<User> findByRolIdRol(Long idRol);

}
