package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.OrderResponseDTO;
import com.mochimexa.ecommerce.DTO.OrderStatusRequestDTO;
import com.mochimexa.ecommerce.DTO.UserResponseDTO;
import com.mochimexa.ecommerce.service.OrderService;
import com.mochimexa.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/orders")
    public List<OrderResponseDTO> getOrders() {
        return orderService.findAllResponses();
    }

    @PatchMapping("/orders/{id}/status")
    public OrderResponseDTO updateOrderStatus(@PathVariable Integer id,
                                               @Valid @RequestBody OrderStatusRequestDTO request) {
        return orderService.updateStatus(id, request.getEstado());
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getUsers() {
        return userService.findAll().stream().map(userService::toResponse).toList();
    }
}
