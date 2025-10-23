package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
//    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId) {
        // validate user and cartItems
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElse(null);
//        if (isNull(user)) return Optional.empty();

        List<CartItem> cartItems = cartService.getCart(userId);
        if (CollectionUtils.isEmpty(cartItems)) {
            return Optional.empty();
        }

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // create order
        Order order = Order.builder()
                .userId(Long.valueOf(userId))
                .status(OrderStatus.CONFIRMED)
                .totalAmount(totalPrice)
                .build();
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .order(order)
                        .build()
                ).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // clear the cart
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        return OrderResponse.builder()
                .id(savedOrder.getId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .createdAt(savedOrder.getCreatedAt())
                .items(savedOrder.getItems().stream()
                        .map(this::mapToOrderItemDTO)
                        .toList()
                ).build();
    }

    private OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subTotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .build();
    }
}
