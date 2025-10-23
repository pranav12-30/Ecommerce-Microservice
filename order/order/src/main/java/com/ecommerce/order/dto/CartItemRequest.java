package com.ecommerce.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}
