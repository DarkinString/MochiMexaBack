package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.ReviewRequestDTO;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.Review;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.productService = productService;
    }

    //CREA UNA LISTA CON TODAS LAS RESEÑAS
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    //BUSCA UNA RESEÑA POR SU ID
    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));
    }

    //BUSCA RESEÑAS POR EL ID DEL PRODUCTO
    @Transactional(readOnly = true)
    public List<Review> findByProductoId(Long idProducto) {
        return reviewRepository.findByProductoIdProducto(idProducto);
    }

    //BUSCA RESEÑAS POR EL ID DEL USUARIO
    @Transactional(readOnly = true)
    public List<Review> findByUsuarioId(Long idUsuario) {
        return reviewRepository.findByUsuarioIdUsuario(idUsuario);
    }

    //BUSCA AL USUARIO Y PRODUCTO PARA CREAR UNA NUEVA RESEÑA
    @Transactional
    public Review create(ReviewRequestDTO dto) {
        User usuario = userService.findById(dto.getIdUsuario());
        Product producto = productService.findById(dto.getIdProducto());

        Review review = new Review();
        review.setCalificacion(dto.getCalificacion());
        review.setComentario(dto.getComentario());
        review.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        review.setUsuario(usuario);
        review.setProducto(producto);

        return reviewRepository.save(review);
    }

    //BUSCA LA RESEÑA POR SU ID Y LA ELIMINA
    @Transactional
    public void deleteById(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada");
        }
        reviewRepository.deleteById(id);
    }
}
