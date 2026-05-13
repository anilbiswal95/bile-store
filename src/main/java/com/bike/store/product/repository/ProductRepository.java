package com.bike.store.product.repository;

import com.bike.store.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.featured = true AND p.active = true")
    List<Product> findFeaturedProducts();

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:bikeModel IS NULL OR p.bikeModel = :bikeModel)",
            countQuery = "SELECT COUNT(p) FROM Product p WHERE p.active = true " +
                    "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
                    "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                    "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                    "AND (:bikeModel IS NULL OR p.bikeModel = :bikeModel)")
    Page<Product> findWithFilters(@Param("categoryId") Long categoryId,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("bikeModel") String bikeModel,
                                  Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))",
            countQuery = "SELECT COUNT(p) FROM Product p WHERE p.active = true " +
                    "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT p.bikeModel FROM Product p WHERE p.bikeModel IS NOT NULL")
    List<String> findDistinctBikeModels();
}
