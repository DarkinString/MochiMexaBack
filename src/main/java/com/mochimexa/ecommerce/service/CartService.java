package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.AddToCartRequestDTO;
import com.mochimexa.ecommerce.model.Cart;
import com.mochimexa.ecommerce.model.CartDetail;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.CartRepository;
import com.mochimexa.ecommerce.repository.CartDetailRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartService(
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            ProductService productService,
            UserService userService
    ) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productService = productService;
        this.userService = userService;
    }

    // BUSCA UN CARRITO ACTIVO POR MEDIO DEL ID DEL USUARIO
    @Transactional(readOnly = true)
    public Cart findByUserId(Integer idUsuario) {
        return cartRepository
                .findByUsuarioIdUsuarioAndEstado(idUsuario, "ACTIVO")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carrito activo no encontrado para el usuario con ID: " + idUsuario
                ));
    }

    @Transactional
    public Cart getOrCreateByUserId(Integer idUsuario) {
        return cartRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, "ACTIVO")
                .orElseGet(() -> crearNuevoCarrito(idUsuario));
    }

    // VERIFICA SI EXISTE CARRITO ACTIVO, SI NO CREA UNO Y AGREGA EL PRODUCTO
    @Transactional
    public Cart addItem(Integer idUsuario, AddToCartRequestDTO dto) {

        Cart cart = cartRepository
                .findByUsuarioIdUsuarioAndEstado(idUsuario, "ACTIVO")
                .orElseGet(() -> crearNuevoCarrito(idUsuario));

        Product product = productService.findById(dto.getIdProducto());
        if (!Boolean.TRUE.equals(product.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no está disponible");
        }

        CartDetail detalleExistente = cart.getDetalles()
                .stream()
                .filter(detalle ->
                        detalle.getProducto()
                                .getIdProducto()
                                .equals(product.getIdProducto())
                )
                .findFirst()
                .orElse(null);

        if (detalleExistente != null) {

            int nuevaCantidad = detalleExistente.getCantidad() + dto.getCantidad();
            validateStock(product, nuevaCantidad);

            detalleExistente.setCantidad(
                    nuevaCantidad
            );

        } else {

            validateStock(product, dto.getCantidad());

            CartDetail nuevoDetalle = new CartDetail();

            nuevoDetalle.setProducto(product);
            nuevoDetalle.setCantidad(dto.getCantidad());
            nuevoDetalle.setPrecioUnitario(product.getPrecio());

            cart.addDetalle(nuevoDetalle);
        }

        return cartRepository.save(cart);
    }

    // ACTUALIZA LA CANTIDAD DE UN ÍTEM ESPECÍFICO DEL CARRITO
    @Transactional
    public Cart updateItemQuantity(Integer cartDetailId, Integer cantidad) {

        CartDetail detalle = cartDetailRepository
                .findById(cartDetailId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Detalle de carrito no encontrado con ID: " + cartDetailId
                ));

        validateStock(detalle.getProducto(), cantidad);
        detalle.setCantidad(cantidad);

        cartDetailRepository.save(detalle);

        return detalle.getCarrito();
    }

    // ELIMINA UN ÍTEM ESPECÍFICO DEL CARRITO
    @Transactional
    public void removeItem(Integer cartDetailId) {

        CartDetail detalle = cartDetailRepository
                .findById(cartDetailId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Detalle de carrito no encontrado con ID: " + cartDetailId
                ));

        Cart cart = detalle.getCarrito();

        cart.getDetalles().remove(detalle);

        cartDetailRepository.delete(detalle);
    }

    @Transactional
    public Cart updateItemQuantityForUser(Integer userId, Integer cartDetailId, Integer cantidad) {
        CartDetail detalle = findOwnedDetail(userId, cartDetailId);
        return updateItemQuantity(detalle.getIdCarritoDetalle(), cantidad);
    }

    @Transactional
    public void removeItemForUser(Integer userId, Integer cartDetailId) {
        CartDetail detalle = findOwnedDetail(userId, cartDetailId);
        removeItem(detalle.getIdCarritoDetalle());
    }

    // VACÍA TODOS LOS ÍTEMS DEL CARRITO ACTIVO DE UN USUARIO
    @Transactional
    public void clearCart(Integer idUsuario) {

        Cart cart = findByUserId(idUsuario);

        cart.getDetalles().clear();

        cartRepository.save(cart);
    }

    // CALCULA EL SUBTOTAL DEL CARRITO
    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotal(Integer idUsuario) {

        Cart cart = findByUserId(idUsuario);

        return cart.getDetalles()
                .stream()
                .map(detalle ->
                        detalle.getPrecioUnitario()
                                .multiply(
                                        BigDecimal.valueOf(detalle.getCantidad())
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    // CREA UN NUEVO CARRITO Y LO ASIGNA A SU USUARIO
    private Cart crearNuevoCarrito(Integer idUsuario) {

        User usuario = userService.findById(idUsuario);

        Cart cart = new Cart();

        cart.setFechaCreacion(LocalDateTime.now());
        cart.setEstado("ACTIVO");
        cart.setUsuario(usuario);

        return cartRepository.save(cart);
    }

    private CartDetail findOwnedDetail(Integer userId, Integer cartDetailId) {
        CartDetail detalle = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de carrito no encontrado"));
        if (!detalle.getCarrito().getUsuario().getIdUsuario().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de carrito no encontrado");
        }
        return detalle;
    }

    private void validateStock(Product product, Integer cantidad) {
        if (cantidad == null || cantidad < 1 || product.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
        }
    }
}
