package com.bike.store.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for admin order responses without lazy loading issues
 * CHANGED: New DTO specifically for admin responses
 */
@Data
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private String userEmail;
    private String userName;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}