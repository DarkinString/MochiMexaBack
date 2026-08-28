package com.mochimexa.ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO {
    private Integer idResenia;
    private Integer idProducto;
    private Integer idUsuario;
    private String autor;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fecha;
}
