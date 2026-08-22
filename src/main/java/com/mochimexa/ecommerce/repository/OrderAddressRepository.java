package com.mochimexa.ecommerce.repository;

import com.mochimexa.ecommerce.model.OrderAddress;
import com.mochimexa.ecommerce.model.OrderAddressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, OrderAddressId> {

}