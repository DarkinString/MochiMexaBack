package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.ReviewRequestDTO;
import com.mochimexa.ecommerce.model.Review;
import com.mochimexa.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @GetMapping("/product/{productId}")
    public List<Review> getByProductId(@PathVariable Long productId) {
        return reviewService.findByProductoId(productId);
    }
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@PathVariable Long userId, @Valid @RequestBody ReviewRequestDTO dto) {
        return reviewService.create(dto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.deleteById(id);
    }
}