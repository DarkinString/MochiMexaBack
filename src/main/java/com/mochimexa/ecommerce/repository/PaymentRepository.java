package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

    //BUSCAR PAGO POR ID PEDIDO
    Optional<Payment> findByPedidoIdPedido(Long idPedido);


}
