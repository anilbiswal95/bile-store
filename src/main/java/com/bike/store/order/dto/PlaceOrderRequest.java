package com.bike.store.order.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotBlank
    private String shippingAddress;

    private String paymentMethod = "COD";
}
