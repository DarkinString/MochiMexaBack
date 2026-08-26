package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.AddToCartRequestDTO;
import com.mochimexa.ecommerce.model.Cart;
import com.mochimexa.ecommerce.model.CartDetail;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.CartRepository;
import com.mochimexa.ecommerce.repository.CartDetailRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartService(CartRepository cartRepository,
                       CartDetailRepository cartDetailRepository,
                       ProductService productService,
                       UserService userService) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productService = productService;
        this.userService = userService;
    }

    // BUSCA UN CARRITO ACTIVO POR MEDIO DEL ID DEL USUARIO
    @Transactional(readOnly = true)
    public Cart findByUserId(Long idUsuario) {
        return cartRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, "ACTIVO")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Carrito activo no encontrado para el usuario con ID: " + idUsuario));
    }

    // VERIFICA SI EXISTE CARRITO ACTIVO, SI NO CREA UNO Y AGREGA EL PRODUCTO
    @Transactional
    public Cart addItem(Long idUsuario, AddToCartRequestDTO dto) {
        Cart cart = cartRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, "ACTIVO")
                .orElseGet(() -> crearNuevoCarrito(idUsuario));

        Product product = productService.findById(dto.getIdProducto());

        // Verificar si el producto ya existe en el carrito
        CartDetail detalleExistente = cart.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto().equals(product.getIdProducto()))
                .findFirst()
                .orElse(null);

        if (detalleExistente != null) {
            // Si ya existe, actualiza la cantidad acumulada
            detalleExistente.setCantidad(detalleExistente.getCantidad() + dto.getCantidad());
        } else {
            // Si no existe, crea un nuevo detalle
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
    public Cart updateItemQuantity(Long cartDetailId, Integer cantidad) {
        CartDetail detalle = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Detalle de carrito no encontrado con ID: " + cartDetailId));

        detalle.setCantidad(cantidad);
        cartDetailRepository.save(detalle);

        return detalle.getCarrito();
    }

    // ELIMINA UN ÍTEM ESPECÍFICO DEL CARRITO
    @Transactional
    public void removeItem(Long cartDetailId) {
        CartDetail detalle = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Detalle de carrito no encontrado con ID: " + cartDetailId));

        Cart cart = detalle.getCarrito();
        cart.getDetalles().remove(detalle); // Mantiene la consistencia bidireccional
        cartDetailRepository.delete(detalle);
    }

    // VACÍA TODOS LOS ÍTEMS DEL CARRITO ACTIVO DE UN USUARIO
    @Transactional
    public void clearCart(Long idUsuario) {
        Cart cart = findByUserId(idUsuario);

        // Al usar orphanRemoval = true en la relación Cart -> CartDetail,
        // al limpiar la lista los registros se eliminan automáticamente de la base de datos
        cart.getDetalles().clear();
        cartRepository.save(cart);
    }

    // CREA UN NUEVO CARRITO Y LO ASIGNA A SU USUARIO
    private Cart crearNuevoCarrito(Long idUsuario) {
        User usuario = userService.findById(idUsuario);

        Cart cart = new Cart();
        cart.setFechaCreacion(LocalDateTime.now());
        cart.setEstado("ACTIVO");
        cart.setUsuario(usuario);

        return cartRepository.save(cart);
    }
}
