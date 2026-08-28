package com.mochimexa.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO {
    @NotBlank
    private String actual;

    @NotBlank
    @Size(min = 8, max = 128)
    private String nueva;
}
