package com.bike.store.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    private String imageUrl;

    @NotNull
    private Long categoryId;

    private String bikeModel;

    // CHANGED: Changed from Integer to int with validation
    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be 0 or greater")
    private Integer stock;

    private boolean featured= false;
}
