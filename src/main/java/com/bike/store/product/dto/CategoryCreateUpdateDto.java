package com.bike.store.product.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateUpdateDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private String imageUrl;
}

