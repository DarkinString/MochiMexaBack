package com.mochimexa.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequestDTO {
    @NotBlank
    private String estado;
}
