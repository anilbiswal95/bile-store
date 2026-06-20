package com.bike.store.product.service;

import com.bike.store.common.exception.AppException;
import com.bike.store.common.exception.ResourceNotFoundException;
import com.bike.store.product.dto.CategoryCreateUpdateDto;
import com.bike.store.product.entity.Category;
import com.bike.store.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    /**
     * Get category by name
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
    }

    /**
     * Create new category
     */
    @Transactional
    public Category createCategory(CategoryCreateUpdateDto dto) {
        // Check if category already exists
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new AppException("Category with name '" + dto.getName() + "' already exists", HttpStatus.CONFLICT);
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();

        return categoryRepository.save(category);
    }

    /**
     * Update existing category
     */
    @Transactional
    public Category updateCategory(Long id, CategoryCreateUpdateDto dto) {
        Category category = getCategoryById(id);

        // Check if trying to rename to an existing name (excluding self)
        if (categoryRepository.findByName(dto.getName()).isPresent() &&
                !categoryRepository.findByName(dto.getName()).get().getId().equals(id)) {
            throw new AppException("Category with name '" + dto.getName() + "' already exists", HttpStatus.CONFLICT);
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        return categoryRepository.save(category);
    }

    /**
     * Delete category
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        // Check if category has products
        // If ProductRepository has method to check, use it
        // For now, we'll allow deletion but it might fail due to foreign key constraint
        categoryRepository.delete(category);
    }
}

