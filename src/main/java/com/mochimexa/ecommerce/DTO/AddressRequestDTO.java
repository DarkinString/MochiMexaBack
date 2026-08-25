package com.mochimexa.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 150, message = "La calle no puede exceder los 150 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 20, message = "El número no puede exceder los 20 caracteres")
    private String numero;

    @NotBlank(message = "La colonia es obligatoria")
    @Size(max = 20, message = "La colonia no puede exceder los 20 caracteres")
    private String colonia;

    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 10, message = "El código postal no puede exceder los 10 caracteres")
    private String codigoPostal;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres")
    private String ciudad;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 10, message = "El estado no puede exceder los 10 caracteres")
    private String estado;

    @Size(max = 150, message = "La referencia no puede exceder los 150 caracteres")
    private String referencia; // Opcional
}