package com.bike.store.admin.controller;

import com.bike.store.order.entity.OrderStatus;
import com.bike.store.order.service.OrderService;
import com.bike.store.product.dto.CategoryCreateUpdateDto;
import com.bike.store.product.dto.CategoryResponseDto;
import com.bike.store.product.dto.ProductCreateDto;
import com.bike.store.product.dto.ProductDto;
import com.bike.store.product.entity.Category;
import com.bike.store.product.service.CategoryService;
import com.bike.store.product.service.ProductService;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin API Controller for managing products, categories, and orders.
 * All endpoints are restricted to users with ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    // ==================== PRODUCT ENDPOINTS ====================

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @Valid @RequestBody ProductCreateDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CATEGORY ENDPOINTS ====================

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(toDto(category));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryCreateUpdateDto dto) {
        Category category = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                                              @Valid @RequestBody CategoryCreateUpdateDto dto) {
        Category category = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(toDto(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ORDER ENDPOINTS ====================

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    // ==================== HELPER METHODS ====================

    private CategoryResponseDto toDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl()
        );
    }
}
