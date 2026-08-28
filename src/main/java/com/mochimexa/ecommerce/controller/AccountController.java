package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.*;
import com.mochimexa.ecommerce.model.Cart;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.service.*;
import com.mochimexa.ecommerce.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RestController
@RequestMapping("/api/me")
public class AccountController {

    private final UserService userService;
    private final AddressService addressService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AccountController(UserService userService, AddressService addressService, CartService cartService,
                             OrderService orderService, ReviewService reviewService,
                             UserDetailsService userDetailsService, JwtService jwtService) {
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public UserResponseDTO getProfile(Principal principal) {
        User user = current(principal);
        return userService.toResponse(user);
    }

    @PutMapping
    public AuthResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequestDTO dto) {
        User user = current(principal);
        User updated = userService.updateOwnProfile(user.getIdUsuario(), dto);
        UserDetails details = userDetailsService.loadUserByUsername(updated.getCorreo());
        return new AuthResponse(jwtService.generateToken(details), "Bearer", jwtService.getExpirationTimeMs(), userService.toResponse(updated));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequestDTO dto) {
        userService.changePassword(current(principal).getIdUsuario(), dto.getActual(), dto.getNueva());
    }

    @GetMapping("/addresses")
    public List<AddressResponseDTO> getAddresses(Principal principal) {
        return addressService.findByUserId(current(principal).getIdUsuario());
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO createAddress(Principal principal, @Valid @RequestBody AddressRequestDTO dto) {
        return addressService.create(current(principal).getIdUsuario(), dto);
    }

    @PutMapping("/addresses/{id}")
    public AddressResponseDTO updateAddress(Principal principal, @PathVariable Integer id,
                                            @Valid @RequestBody AddressRequestDTO dto) {
        return addressService.updateForUser(current(principal).getIdUsuario(), id, dto);
    }

    @DeleteMapping("/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(Principal principal, @PathVariable Integer id) {
        addressService.deleteForUser(current(principal).getIdUsuario(), id);
    }

    @GetMapping("/cart")
    public Cart getCart(Principal principal) {
        return cartService.getOrCreateByUserId(current(principal).getIdUsuario());
    }

    @PostMapping("/cart/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addCartItem(Principal principal, @Valid @RequestBody AddToCartRequestDTO dto) {
        return cartService.addItem(current(principal).getIdUsuario(), dto);
    }

    @PutMapping("/cart/items/{id}")
    public Cart updateCartItem(Principal principal, @PathVariable Integer id,
                               @Valid @RequestBody UpdateCartItemDTO dto) {
        return cartService.updateItemQuantityForUser(current(principal).getIdUsuario(), id, dto.getCantidad());
    }

    @DeleteMapping("/cart/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(Principal principal, @PathVariable Integer id) {
        cartService.removeItemForUser(current(principal).getIdUsuario(), id);
    }

    @DeleteMapping("/cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal) {
        cartService.clearCart(current(principal).getIdUsuario());
    }

    @GetMapping("/orders")
    public List<OrderResponseDTO> getOrders(Principal principal) {
        return orderService.findResponsesByUser(current(principal).getIdUsuario());
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(Principal principal, @Valid @RequestBody OrderRequestDTO dto) {
        return orderService.createForUser(current(principal).getIdUsuario(), dto);
    }

    @PostMapping("/reviews/product/{productId}")
    public ReviewResponseDTO saveReview(Principal principal, @PathVariable Integer productId,
                                        @Valid @RequestBody ReviewRequestDTO dto) {
        return reviewService.saveForUser(current(principal).getIdUsuario(), productId, dto);
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(Principal principal, @PathVariable Integer id) {
        reviewService.deleteForUser(current(principal).getIdUsuario(), id);
    }

    private User current(Principal principal) {
        return userService.findByCorreo(principal.getName());
    }
}
