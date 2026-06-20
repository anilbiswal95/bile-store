package com.bike.store.product.service;

import com.bike.store.common.dto.PagedResponse;
import com.bike.store.common.exception.ResourceNotFoundException;
import com.bike.store.product.dto.ProductCreateDto;
import com.bike.store.product.dto.ProductDto;
import com.bike.store.product.entity.Category;
import com.bike.store.product.entity.Product;
import com.bike.store.product.repository.CategoryRepository;
import com.bike.store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "products", key = "'featured'")
    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findFeaturedProducts().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "products", key = "#categoryId + '-' + #minPrice + '-' + #maxPrice + '-' + #bikeModel + '-' + #page + '-' + #size")
    public PagedResponse<ProductDto> getProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                 String bikeModel, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> products = productRepository.findWithFilters(categoryId, minPrice, maxPrice, bikeModel, pageable);
        return toPagedResponse(products);
    }

    public PagedResponse<ProductDto> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.search(query, pageable);
        return toPagedResponse(products);
    }

    /*@Cacheable(value = "productDetail", key = "#id")
    public ProductDto getProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toDto(product);
    }*/

    @Cacheable(value = "productDetail", key = "#id")
    public ProductDto getProduct(long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toDto(product);
    }


    @Transactional
    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    public ProductDto createProduct(ProductCreateDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .category(category)
                .bikeModel(dto.getBikeModel())
                .stock(dto.getStock())
                .featured(dto.isFeatured())
                .active(true)
                .build();

        return toDto(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    public ProductDto updateProduct(Long id, ProductCreateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setBikeModel(dto.getBikeModel());
        product.setStock(dto.getStock());
        product.setFeatured(dto.isFeatured());

        return toDto(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<String> getBikeModels() {
        return productRepository.findDistinctBikeModels();
    }

    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setImageUrl(p.getImageUrl());
        dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
        dto.setBikeModel(p.getBikeModel());
        dto.setStock(p.getStock());
        dto.setFeatured(p.isFeatured());
        return dto;
    }

    private PagedResponse<ProductDto> toPagedResponse(Page<Product> page) {
        List<ProductDto> content = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
