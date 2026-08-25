package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.OrderRequestDTO;
import com.mochimexa.ecommerce.repository.OrderRepository;
import com.mochimexa.ecommerce.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //CREA UNA LISTA DE LOS PEDIDOS
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    //BUSCA UN PEDIDO POR SU ID
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }


    @Transactional
    public Order createOrderFromCart(Long userId, OrderRequestDTO dto) {

        Order order = new Order();
        order.setFechaPedido(LocalDateTime.now());
        order.setEstado("Pendiente");
        return orderRepository.save(order);
    }

    //BUSCA UN PEDIDO Y LO ACTUALIZA
    @Transactional
    public Order update(Long id, Order orderDetails) {
        Order order = findById(id);
        order.setFechaPedido(orderDetails.getFechaPedido());
        order.setEstado(orderDetails.getEstado());
        order.setSubTotal(orderDetails.getSubTotal());
        order.setCostoEnvio(orderDetails.getCostoEnvio());
        order.setTotal(orderDetails.getTotal());
        order.setUsuario(orderDetails.getUsuario());
        order.setDireccion(orderDetails.getDireccion());
        return orderRepository.save(order);
    }

    //VERIFICA SI EXISTE UN PEDIDO POR ID Y LO ELIMINA
    @Transactional
    public void deleteById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        orderRepository.deleteById(id);
    }

    //BUSCA LOS PEDIDOS DE UN USUARIO POR SU ID
    @Transactional(readOnly = true)
    public List<Order> findByUsuarioId(Long idUsuario) {
        return orderRepository.findByUsuarioIdUsuario(idUsuario);
    }

    //BUSCA LOS PEDIDOS POR ESTADO(PENDIENTE, ENVIADO, ENTREGADO)
    @Transactional(readOnly = true)
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado);
    }

}
