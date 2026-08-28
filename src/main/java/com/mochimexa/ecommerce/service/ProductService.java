package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.ProductRequestDTO;
import com.mochimexa.ecommerce.model.Category;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.repository.CategoryRepository;
import com.mochimexa.ecommerce.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREA UNA LISTA DE LOS PRODUCTOS
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // BUSCA UN PRODUCTO POR SU ID
    @Transactional(readOnly = true)
    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"
                ));
    }

    // BUSCA UN PRODUCTO POR NOMBRE
    @Transactional(readOnly = true)
    public List<Product> findByNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // BUSCA PRODUCTOS CON PRECIO MAYOR AL INDICADO
    @Transactional(readOnly = true)
    public List<Product> findByPrecioGreaterThan(BigDecimal precio) {
        return productRepository.findByPrecioGreaterThan(precio);
    }

    // BUSCA PRODUCTOS POR RANGO DE PRECIO
    @Transactional(readOnly = true)
    public List<Product> findByPrecioBetween(
            BigDecimal precioMin,
            BigDecimal precioMax
    ) {
        return productRepository.findByPrecioBetween(precioMin, precioMax);
    }

    // BUSCA PRODUCTOS POR CATEGORIA
    @Transactional(readOnly = true)
    public List<Product> findByCategoriaId(Integer idCategoria) {
        return productRepository.findByCategoriaIdCategoria(idCategoria);
    }

    // CREA UN PRODUCTO Y VERIFICA QUE LA CATEGORIA EXISTA
    @Transactional
    public Product create(ProductRequestDTO dto) {

        Category categoria = categoryRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));

        Product producto = new Product();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setMarca(dto.getMarca());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        producto.setCategoria(categoria);

        return productRepository.save(producto);
    }

    // MODIFICA UN PRODUCTO POR SU ID
    @Transactional
    public Product update(Integer id, ProductRequestDTO dto) {

        Product producto = findById(id);

        Category categoria = categoryRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setMarca(dto.getMarca());

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        producto.setCategoria(categoria);

        return productRepository.save(producto);
    }

    // ACTUALIZA EL STOCK USANDO EL ID Y LA CANTIDAD
    @Transactional
    public void updateStock(Integer id, Integer cantidad) {

        Product producto = findById(id);

        if (producto.getStock() < cantidad) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock insuficiente"
            );
        }

        producto.setStock(producto.getStock() - cantidad);
        productRepository.save(producto);
    }

    // ELIMINA UN PRODUCTO VALIDANDO QUE EXISTA POR ID
    @Transactional
    public void deleteById(Integer id) {

        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no encontrado"
            );
        }

        productRepository.deleteById(id);
    }
}