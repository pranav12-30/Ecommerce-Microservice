package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // allows users to add item to cart.
    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest cartItemRequest,
                                            @RequestHeader("user-id") String userId) {
        boolean addedToCart = cartService.addToCart(userId, cartItemRequest);
        if (!addedToCart) {
            return ResponseEntity.badRequest().body("Product not found or Product Out Of Stock or User not found");
        }
        return new ResponseEntity<>("Item added to cart successfully by the user.", HttpStatus.CREATED);
    }

    // delete item from cart
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> deleteFromCart(@RequestHeader("user-id") Long userId,
                                               @PathVariable Long productId) {
        boolean isDeleted = cartService.deleteItemFromCart(userId, productId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // get all items in cart
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
