package org.utku.shoppingapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.utku.shoppingapi.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 *
 * Provides methods for:
 * - Finding active products
 * - Searching products by name, price range, and stock
 * - Managing product catalog persistence
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds all active products.
     * @return List of active products
     */
    List<Product> findByActiveTrue();

    /**
     * Finds active products by name (case-insensitive, partial match) with pagination.
     * @param name the product name to search for
     * @param pageable pagination information
     * @return Page of matching active products
     */
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds all active products with pagination.
     * @param pageable pagination information
     * @return Page of active products
     */
    @Query("SELECT p FROM Product p WHERE p.active = true")
    Page<Product> findAllActive(Pageable pageable);

    /**
     * Finds active products within a price range.
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return List of products in the specified price range
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Finds active products with stock quantity below a threshold.
     * @param threshold stock quantity threshold
     * @return List of products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}