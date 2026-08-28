package com.mochimexa.ecommerce.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreSettingsDTO {
    @NotBlank
    private String nombreAdmin;
    @NotBlank
    @Email
    private String correoAdmin;
    @NotNull
    @PositiveOrZero
    private BigDecimal envioCdmx;
    @NotNull
    @PositiveOrZero
    private BigDecimal envioInterior;
    @NotNull
    private Map<String, Boolean> metodosPago;
    @NotNull
    private Map<String, Boolean> notificaciones;
}
