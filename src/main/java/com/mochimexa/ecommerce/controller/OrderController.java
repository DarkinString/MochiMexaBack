package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.OrderRequestDTO;
import com.mochimexa.ecommerce.model.Order;
import com.mochimexa.ecommerce.DTO.OrderResponseDTO;
import com.mochimexa.ecommerce.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(
            @PathVariable Integer userId,
            @Valid @RequestBody OrderRequestDTO dto
    ) {
        return orderService.createForUser(userId, dto);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(
            @PathVariable Integer userId
    ) {
        return orderService.findByUsuarioId(userId);
    }

    @GetMapping("/{id}")
    public Order getById(
            @PathVariable Integer id
    ) {
        return orderService.findById(id);
    }
}
