package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.OrderRequestDTO;
import com.mochimexa.ecommerce.model.Address;
import com.mochimexa.ecommerce.model.Order;
import com.mochimexa.ecommerce.model.OrderAddress;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.AddressRepository;
import com.mochimexa.ecommerce.repository.OrderAddressRepository;
import com.mochimexa.ecommerce.repository.OrderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            OrderAddressRepository orderAddressRepository,
            AddressRepository addressRepository,
            UserService userService,
            CartService cartService
    ) {
        this.orderRepository = orderRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
        this.cartService = cartService;
    }

    // BUSCA TODOS LOS PEDIDOS
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // BUSCA UN PEDIDO POR SU ID
    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));
    }

    // CREA UN PEDIDO A PARTIR DEL CARRITO DEL USUARIO
    @Transactional
    public Order createOrderFromCart(Integer userId, OrderRequestDTO dto) {

        User usuario = userService.findById(userId);

        // CALCULA EL SUBTOTAL DEL CARRITO
        BigDecimal subTotal = cartService.calcularSubtotal(userId);

        // COSTO DE ENVÍO FIJO POR AHORA
        BigDecimal costoEnvio = new BigDecimal("99.00");

        // CALCULA EL TOTAL
        BigDecimal total = subTotal.add(costoEnvio);

        Order order = new Order();

        order.setFechaPedido(LocalDateTime.now());
        order.setEstado("Pendiente");
        order.setSubTotal(subTotal);
        order.setCostoEnvio(costoEnvio);
        order.setTotal(total);
        order.setUsuario(usuario);

        Order savedOrder = orderRepository.save(order);

        // ASOCIA LAS DIRECCIONES AL PEDIDO
        for (Integer idDireccion : dto.getIdDirecciones()) {

            Address direccion = addressRepository
                    .findByIdDireccionAndUsuarioIdUsuario(idDireccion, userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La dirección con ID " + idDireccion
                                    + " no existe o no pertenece al usuario"
                    ));

            OrderAddress orderAddress = new OrderAddress();

            orderAddress.setPedido(savedOrder);
            orderAddress.setDireccion(direccion);

            orderAddressRepository.save(orderAddress);
        }

        return savedOrder;
    }

    // ACTUALIZA UN PEDIDO
    @Transactional
    public Order update(Integer id, Order orderDetails) {

        Order order = findById(id);

        order.setFechaPedido(orderDetails.getFechaPedido());
        order.setEstado(orderDetails.getEstado());
        order.setSubTotal(orderDetails.getSubTotal());
        order.setCostoEnvio(orderDetails.getCostoEnvio());
        order.setTotal(orderDetails.getTotal());
        order.setUsuario(orderDetails.getUsuario());

        return orderRepository.save(order);
    }

    // ELIMINA UN PEDIDO
    @Transactional
    public void deleteById(Integer id) {

        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pedido no encontrado"
            );
        }

        orderRepository.deleteById(id);
    }

    // BUSCA LOS PEDIDOS DE UN USUARIO
    @Transactional(readOnly = true)
    public List<Order> findByUsuarioId(Integer idUsuario) {
        return orderRepository.findByUsuarioIdUsuario(idUsuario);
    }

    // BUSCA LOS PEDIDOS POR ESTADO
    @Transactional(readOnly = true)
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado);
    }
}