package org.utku.shoppingapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.constants.AppConstants;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 * 
 * Provides methods for:
 * - Finding active products
 * - Searching products by name and category
 * - Filtering products by price range and stock levels
 * - Inventory management queries
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds all active products.
     * 
     * @return List of active products
     */
    List<Product> findByActiveTrue();
    
    /**
     * Searches for active products by name with pagination.
     * Performs case-insensitive partial matching on product names.
     * 
     * @param name the name to search for (partial match)
     * @param pageable pagination parameters
     * @return Page of matching active products
     */
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Finds all products in a specific category.
     * 
     * @param category the category to filter by
     * @return List of products in the specified category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Finds all products that are active and have stock available.
     * 
     * @return List of available products (active with stock > 0)
     */
    @Query(AppConstants.Queries.FIND_AVAILABLE_PRODUCTS)
    List<Product> findAvailableProducts();
    
    /**
     * Finds active products within a specified price range.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return List of products within the price range
     */
    @Query(AppConstants.Queries.FIND_PRODUCTS_BY_PRICE_RANGE)
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Finds active products with stock below the specified threshold.
     * Useful for inventory management and low stock alerts.
     * 
     * @param threshold the stock threshold
     * @return List of products with low stock
     */
    @Query(AppConstants.Queries.FIND_LOW_STOCK_PRODUCTS)
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
