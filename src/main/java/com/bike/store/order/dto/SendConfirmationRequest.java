package com.bike.store.order.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendConfirmationRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @Email(message = "Invalid email format")
    private String email;
}