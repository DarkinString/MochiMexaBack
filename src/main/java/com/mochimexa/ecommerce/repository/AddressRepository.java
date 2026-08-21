package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    //BUSCAR DIRECCIONES POR EL ID DEL USUSARIO
    List<Address> findByUsuarioIdUsuario(Long idUsuario);

}
