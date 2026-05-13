package com.bike.store.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @NotNull
    private Integer stock;

    private boolean featured;
}
