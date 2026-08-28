package com.mochimexa.ecommerce.config;

import com.mochimexa.ecommerce.model.Address;
import com.mochimexa.ecommerce.model.Category;
import com.mochimexa.ecommerce.model.Order;
import com.mochimexa.ecommerce.model.Product;
import com.mochimexa.ecommerce.model.Rol;
import com.mochimexa.ecommerce.repository.AddressRepository;
import com.mochimexa.ecommerce.repository.CategoryRepository;
import com.mochimexa.ecommerce.repository.OrderRepository;
import com.mochimexa.ecommerce.repository.ProductRepository;
import com.mochimexa.ecommerce.repository.RolRepository;
import com.mochimexa.ecommerce.service.StoreSettingsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.Normalizer;
import java.util.Locale;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final StoreSettingsService storeSettingsService;

    public DataInitializer(RolRepository rolRepository, CategoryRepository categoryRepository,
                           ProductRepository productRepository, AddressRepository addressRepository,
                           OrderRepository orderRepository, StoreSettingsService storeSettingsService) {
        this.rolRepository = rolRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.storeSettingsService = storeSettingsService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRole("CLIENTE", "Cliente de la tienda");
        seedRole("ADMIN", "Administrador de la tienda");
        Category mochis = seedCategory("Mochis");
        Category bebidas = seedCategory("Bebidas");
        Category pocky = seedCategory("Pocky");
        Category snacks = seedCategory("Otros snacks");
        if (productRepository.count() == 0) seedProducts(mochis, bebidas, pocky, snacks);
        storeSettingsService.getEntity();
        backfillLegacyRows();
    }

    private void seedRole(String name, String description) {
        if (rolRepository.findByRolAsignadoIgnoreCase(name).isPresent()) return;
        Rol role = new Rol();
        role.setRolAsignado(name);
        role.setNombre(name);
        role.setDescripcion(description);
        rolRepository.save(role);
    }

    private Category seedCategory(String name) {
        return categoryRepository.findByNombreIgnoreCase(name).orElseGet(() -> {
            Category category = new Category();
            category.setNombre(name);
            category.setDescripcion("Productos de " + name);
            category.setActivo(true);
            return categoryRepository.save(category);
        });
    }

    private void seedProducts(Category mochis, Category bebidas, Category pocky, Category snacks) {
        add("mochi-matcha", "Mochi Matcha", "Matcha de Uji + Toque de Vainilla.", "45.00", mochis, "mochis/Mochi Matcha.png");
        add("pocky-fresa", "Poky Fresa", "Doble cobertura de Fresa Natural.", "35.00", pocky, "pokis/Pokys Fresa.png");
        add("ramune-natural", "Ramune Natural", "Refresco icónico con canica sabor natural.", "85.00", bebidas, "ramune/Ramune Natural.png");
        add("mochi-fresa", "Mochi Fresa", "Mochi con Fresa Natural.", "45.00", mochis, "mochis/Mochi Fresa.png");
        add("pocky-chocolate", "Poky Chocolate", "Doble cobertura de Chocolate.", "35.00", pocky, "pokis/Pokys Cholocate.png");
        add("ramune-lychee", "Ramune Lychee", "Refresco icónico con canica sabor Lychee.", "50.00", bebidas, "ramune/Ramune Lychee.png");
        add("mochi-mango", "Mochi Mango", "Mochi Mango natural.", "45.00", mochis, "mochis/Mochi Mango.png");
        add("pocky-oreo", "Pokys Oreo", "Doble cobertura de oreo.", "85.00", pocky, "pokis/Pokys Cookies & Cream.png");
        add("ramune-fresa", "Ramune de Fresa", "Refresco icónico con canica sabor fresa.", "85.00", bebidas, "ramune/Ramune  Fresa.png");
        add("mochi-taro", "Mochi Taro", "Mochi Taro Natural.", "45.00", mochis, "mochis/Mochi Taro.png");
        add("kitkat-sake", "KitKat Sake", "Edición especial Japón.", "62.00", snacks, "kitkat/kitKatSake.jpg");
        add("ramune-uva", "Ramune Uva", "Refresco icónico con canica sabor uva.", "55.00", bebidas, "ramune/Ramune Uva.png");
        add("mochi-lychee", "Mochi sabor Lychee", "Edición Lychee Natural.", "45.00", mochis, "mochis/Mochi Lychee.png");
        add("pocky-matcha", "Pokis Matcha", "Edición especial Matcha.", "38.00", pocky, "pokis/Pokys Matcha.png");
        add("ramune-melon", "Ramune Melón", "Refresco icónico con canica sabor melón.", "55.00", bebidas, "ramune/Ramune Melon.png");
    }

    private void add(String slug, String name, String description, String price, Category category, String image) {
        Product product = new Product();
        product.setSlug(slug);
        product.setNombre(name);
        product.setDescripcion(description);
        product.setPrecio(new BigDecimal(price));
        product.setStock(50);
        product.setMarca("MochiMexa");
        product.setImagen("../assets/imagenes/productosCatalogo/" + image);
        product.setBadge("EN STOCK");
        product.setActivo(true);
        product.setCategoria(category);
        productRepository.save(product);
    }

    private void backfillLegacyRows() {
        Set<String> slugs = new HashSet<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            String base = product.getSlug() == null || product.getSlug().isBlank() ? slugify(product.getNombre()) : product.getSlug();
            String slug = base;
            int suffix = 2;
            while (slugs.contains(slug)) slug = base + "-" + suffix++;
            product.setSlug(slug);
            slugs.add(slug);
            if (product.getBadge() == null) product.setBadge(product.getStock() == 0 ? "AGOTADO" : "EN STOCK");
        }
        productRepository.saveAll(products);

        Set<Integer> primaryUsers = new HashSet<>();
        List<Address> addresses = addressRepository.findAll();
        for (Address address : addresses) {
            if (address.getAlias() == null || address.getAlias().isBlank()) address.setAlias("Dirección " + address.getIdDireccion());
            Integer userId = address.getUsuario().getIdUsuario();
            if (!primaryUsers.contains(userId)) {
                address.setPrincipal(true);
                primaryUsers.add(userId);
            } else address.setPrincipal(false);
        }
        addressRepository.saveAll(addresses);

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getDescuento() == null) order.setDescuento(BigDecimal.ZERO);
            if (order.getMetodoPago() == null) order.setMetodoPago("sin especificar");
            if (order.getEstadoPago() == null) order.setEstadoPago("Sin cobrar");
            if (order.getSolicitudId() == null) order.setSolicitudId("legacy-" + order.getIdPedido());
        }
        orderRepository.saveAll(orders);
    }

    private String slugify(String value) {
        String slug = Normalizer.normalize(value == null ? "producto" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "producto" : slug;
    }
}
