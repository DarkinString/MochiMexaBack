package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Contrasenia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContraseniaRepository extends JpaRepository<Contrasenia, Integer> {

    Optional<Contrasenia> findByUsuarioIdUsuario(Integer idUsuario);
}