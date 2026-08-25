package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.AddToCartRequestDTO;
import com.mochimexa.ecommerce.DTO.UpdateCartItemDTO;
import com.mochimexa.ecommerce.model.Cart;
import com.mochimexa.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping("/user/{userId}")
    public Cart getCartByUserId(@PathVariable Long userId) {
        return cartService.findByUserId(userId);
    }
    @PostMapping("/user/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addItem(@PathVariable Long userId, @Valid @RequestBody AddToCartRequestDTO dto) {
        return cartService.addItem(userId, dto);
    }
    @PutMapping("/items/{cartDetailId}")
    public Cart updateItemQuantity(@PathVariable Long cartDetailId, @Valid @RequestBody UpdateCartItemDTO dto) {
        return cartService.updateItemQuantity(cartDetailId, dto.getCantidad());
    }
    @DeleteMapping("/items/{cartDetailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long cartDetailId) {
        cartService.removeItem(cartDetailId);
    }
    @DeleteMapping("/user/{userId}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}