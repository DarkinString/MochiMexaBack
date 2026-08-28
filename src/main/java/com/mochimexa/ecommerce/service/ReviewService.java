package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.ReviewRequestDTO;
import com.mochimexa.ecommerce.DTO.ReviewResponseDTO;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.Review;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.ReviewRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserService userService,
            ProductService productService
    ) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.productService = productService;
    }

    // CREA UNA LISTA CON TODAS LAS RESEÑAS
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    // BUSCA UNA RESEÑA POR SU ID
    @Transactional(readOnly = true)
    public Review findById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reseña no encontrada"
                ));
    }

    // BUSCA RESEÑAS POR EL ID DEL PRODUCTO
    @Transactional(readOnly = true)
    public List<Review> findByProductoId(Integer idProducto) {
        return reviewRepository.findByProductoIdProducto(idProducto);
    }

    // BUSCA RESEÑAS POR EL ID DEL USUARIO
    @Transactional(readOnly = true)
    public List<Review> findByUsuarioId(Integer idUsuario) {
        return reviewRepository.findByUsuarioIdUsuario(idUsuario);
    }

    // BUSCA AL USUARIO Y PRODUCTO PARA CREAR UNA NUEVA RESEÑA
    @Transactional
    public Review create(ReviewRequestDTO dto) {

        User usuario = userService.findById(dto.getIdUsuario());
        Product producto = productService.findById(dto.getIdProducto());

        Review review = new Review();
        review.setCalificacion(dto.getCalificacion());
        review.setComentario(dto.getComentario());
        review.setFecha(
                dto.getFecha() != null
                        ? dto.getFecha()
                        : LocalDateTime.now()
        );
        review.setUsuario(usuario);
        review.setProducto(producto);

        return reviewRepository.save(review);
    }

    @Transactional
    public ReviewResponseDTO saveForUser(Integer userId, Integer productId, ReviewRequestDTO dto) {
        User usuario = userService.findById(userId);
        Product producto = productService.findById(productId);
        Review review = reviewRepository.findByProductoIdProductoAndUsuarioIdUsuario(productId, userId)
                .orElseGet(Review::new);
        review.setUsuario(usuario);
        review.setProducto(producto);
        review.setCalificacion(dto.getCalificacion());
        review.setComentario(dto.getComentario() == null ? null : dto.getComentario().trim());
        review.setFecha(LocalDateTime.now());
        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> findResponsesByProduct(Integer productId) {
        return findByProductoId(productId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteForUser(Integer userId, Integer id) {
        Review review = findById(id);
        if (!review.getUsuario().getIdUsuario().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada");
        }
        reviewRepository.delete(review);
    }

    // BUSCA LA RESEÑA POR SU ID Y LA ELIMINA
    @Transactional
    public void deleteById(Integer id) {

        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reseña no encontrada"
            );
        }

        reviewRepository.deleteById(id);
    }

    public ReviewResponseDTO toResponse(Review review) {
        User user = review.getUsuario();
        return new ReviewResponseDTO(
                review.getIdResenias(),
                review.getProducto().getIdProducto(),
                user.getIdUsuario(),
                (user.getNombre() + " " + user.getApellido()).trim(),
                review.getCalificacion(),
                review.getComentario(),
                review.getFecha()
        );
    }
}
