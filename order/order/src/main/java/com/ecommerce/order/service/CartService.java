package com.ecommerce.order.service;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

//    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
//    private final ProductRepository productRepository;

    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {
        Integer requestedQuantity = cartItemRequest.getQuantity();
        /*Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElse(null);
        if (isNull(product) || product.getStockQuantity() < requestedQuantity) {
            return false;
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElse(null);
        if (isNull(user)) {
            return false;
        }*/

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId), cartItemRequest.getProductId());
        if (!isNull(existingCartItem)) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + requestedQuantity);
            existingCartItem.setPrice(BigDecimal.valueOf(1000));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .productId(cartItemRequest.getProductId())
                    .userId(Long.valueOf(userId))
                    .quantity(requestedQuantity)
                    .price(BigDecimal.valueOf(1000))
                    .build();
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(Long userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (isNull(cartItem)) return false;
        cartItemRepository.deleteById(cartItem.getId());
        return true;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(Long.valueOf(userId));
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(Long.valueOf(userId));
    }
}
