package com.bike.store.cart.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {
    private List<CartItemDto> items;
    private BigDecimal total;

    @Data
    public static class CartItemDto {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private int quantity;
        private BigDecimal subtotal;
        private String imageUrl;
    }
}
