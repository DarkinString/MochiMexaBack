package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    // BUSCAR DIRECCIONES POR EL ID DEL USUARIO
    List<Address> findByUsuarioIdUsuario(Integer idUsuario);

    // BUSCAR UNA DIRECCIÓN Y VERIFICAR QUE PERTENEZCA AL USUARIO
    Optional<Address> findByIdDireccionAndUsuarioIdUsuario(
            Integer idDireccion,
            Integer idUsuario
    );
}