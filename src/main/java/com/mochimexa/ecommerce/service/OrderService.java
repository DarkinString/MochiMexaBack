package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.OrderItemResponseDTO;
import com.mochimexa.ecommerce.DTO.OrderRequestDTO;
import com.mochimexa.ecommerce.DTO.OrderResponseDTO;
import com.mochimexa.ecommerce.model.Address;
import com.mochimexa.ecommerce.model.Cart;
import com.mochimexa.ecommerce.model.CartDetail;
import com.mochimexa.ecommerce.model.Order;
import com.mochimexa.ecommerce.model.OrderAddress;
import com.mochimexa.ecommerce.model.OrderDetail;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.model.StoreSettings;
import com.mochimexa.ecommerce.repository.AddressRepository;
import com.mochimexa.ecommerce.repository.OrderAddressRepository;
import com.mochimexa.ecommerce.repository.OrderDetailRepository;
import com.mochimexa.ecommerce.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private static final Set<String> ESTADOS = Set.of("Pendiente", "En camino", "Entregado", "Cancelado");

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final CartService cartService;
    private final ProductService productService;
    private final StoreSettingsService storeSettingsService;

    public OrderService(
            OrderRepository orderRepository,
            OrderAddressRepository orderAddressRepository,
            OrderDetailRepository orderDetailRepository,
            AddressRepository addressRepository,
            UserService userService,
            AddressService addressService,
            CartService cartService,
            ProductService productService,
            StoreSettingsService storeSettingsService
    ) {
        this.orderRepository = orderRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.productService = productService;
        this.storeSettingsService = storeSettingsService;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    @Transactional
    public OrderResponseDTO createForUser(Integer userId, OrderRequestDTO dto) {
        Order repeated = orderRepository.findBySolicitudIdAndUsuarioIdUsuario(dto.getSolicitudId(), userId).orElse(null);
        if (repeated != null) return toResponse(repeated);
        StoreSettings settings = storeSettingsService.getEntity();
        if (!storeSettingsService.paymentEnabled(settings, dto.getMetodoPago()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Método de pago no disponible");

        User user = userService.findById(userId);
        Address address = addressRepository.findByIdDireccionAndUsuarioIdUsuario(dto.getIdDireccion(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        Cart cart = cartService.findByUserId(userId);
        if (cart.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartDetail item : cart.getDetalles()) {
            Product product = productService.findById(item.getProducto().getIdProducto());
            if (!Boolean.TRUE.equals(product.getActivo()) || product.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente de " + product.getNombre());
            }
            subtotal = subtotal.add(product.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        BigDecimal shipping = "Ciudad de México".equalsIgnoreCase(address.getEstado())
                ? settings.getEnvioCdmx() : settings.getEnvioInterior();
        Order order = new Order();
        order.setFechaPedido(LocalDateTime.now());
        order.setEstado("Pendiente");
        order.setSubTotal(subtotal);
        order.setCostoEnvio(shipping);
        order.setDescuento(BigDecimal.ZERO);
        order.setTotal(subtotal.add(shipping));
        order.setMetodoPago(dto.getMetodoPago().toLowerCase());
        order.setEstadoPago("Sin cobrar");
        order.setSolicitudId(dto.getSolicitudId());
        order.setUsuario(user);
        Order saved = orderRepository.save(order);

        for (CartDetail item : List.copyOf(cart.getDetalles())) {
            Product product = productService.findById(item.getProducto().getIdProducto());
            BigDecimal itemSubtotal = product.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            OrderDetail detail = new OrderDetail();
            detail.setPedido(saved);
            detail.setProducto(product);
            detail.setCantidad(item.getCantidad());
            detail.setPrecioUnitario(product.getPrecio());
            detail.setSubtotal(itemSubtotal);
            orderDetailRepository.save(detail);
            productService.updateStock(product.getIdProducto(), item.getCantidad());
        }

        orderAddressRepository.save(new OrderAddress(address, saved));
        cartService.clearCart(userId);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findResponsesByUser(Integer userId) {
        return orderRepository.findByUsuarioIdUsuario(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAllResponses() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponseDTO updateStatus(Integer id, String status) {
        if (!ESTADOS.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de pedido no válido");
        }
        Order order = findById(id);
        order.setEstado(status);
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<Order> findByUsuarioId(Integer idUsuario) {
        return orderRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        orderRepository.deleteById(id);
    }

    public OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> items = orderDetailRepository.findByPedidoIdPedido(order.getIdPedido()).stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProducto().getIdProducto(),
                        item.getProducto().getSlug(),
                        item.getProducto().getNombre(),
                        item.getProducto().getImagen(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                )).toList();
        var address = orderAddressRepository.findFirstByPedidoIdPedido(order.getIdPedido())
                .map(link -> addressService.findById(link.getDireccion().getIdDireccion()))
                .orElse(null);
        return new OrderResponseDTO(
                order.getIdPedido(), order.getSolicitudId(), order.getFechaPedido(), order.getEstado(),
                order.getSubTotal(), order.getCostoEnvio(), order.getDescuento(), order.getTotal(),
                order.getMetodoPago(), order.getEstadoPago(), userService.toResponse(order.getUsuario()), address, items
        );
    }
}
