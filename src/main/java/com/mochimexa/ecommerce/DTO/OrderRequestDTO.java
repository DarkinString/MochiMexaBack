package com.mochimexa.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private List<Integer> idDirecciones;

    @NotNull(message = "Debes seleccionar una dirección de envío")
    private Integer idDireccion;

    @NotBlank(message = "Debes seleccionar un método de pago")
    private String metodoPago;

    @NotBlank(message = "Falta el identificador de la solicitud")
    private String solicitudId;
}
