package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.CategoryRequestDTO;
import com.mochimexa.ecommerce.model.Category;
import com.mochimexa.ecommerce.repository.CategoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));
    }

    @Transactional
    public Category create(CategoryRequestDTO dto) {
        Category category = new Category();

        category.setNombre(dto.getNombre());
        category.setDescripcion(dto.getDescripcion());
        category.setActivo(dto.getActivo());

        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Integer id, CategoryRequestDTO dto) {
        Category category = findById(id);

        category.setNombre(dto.getNombre());
        category.setDescripcion(dto.getDescripcion());
        category.setActivo(dto.getActivo());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Categoría no encontrada"
            );
        }

        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Category findByName(String nombre) {
        return categoryRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));
    }

    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {
        return categoryRepository.findByActivoTrue();
    }
}