package com.bike.store.user.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {

    private Long id;

    private boolean isDefault;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String pinCode;
}
