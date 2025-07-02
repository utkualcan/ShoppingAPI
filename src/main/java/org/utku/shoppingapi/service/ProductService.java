package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.utku.shoppingapi.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for product management operations.
 * Defines the contract for product-related business logic including:
 * - Product CRUD operations
 * - Product catalog management
 * - Inventory and pricing operations
 */
public interface ProductService {
    
    /**
     * Retrieves all products with pagination support.
     * 
     * @param pageable Pagination parameters
     * @return Page of products
     */
    Page<Product> getAllProducts(Pageable pageable);
    
    /**
     * Finds a product by its unique identifier.
     * 
     * @param id The product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findProductById(Long id);
    
    /**
     * Creates a new product in the catalog.
     * 
     * @param product The product entity to create
     * @return The created product entity
     */
    Product createProduct(Product product);
    
    /**
     * Updates an existing product's information.
     * 
     * @param id The ID of the product to update
     * @param product Product object containing updated information
     * @return The updated product entity
     */
    Product updateProduct(Long id, Product product);
    
    /**
     * Deletes a product from the catalog.
     * 
     * @param id The ID of the product to delete
     */
    void deleteProduct(Long id);
    
    /**
     * Decreases product stock by specified quantity.
     * Used when products are added to cart or ordered.
     * 
     * @param productId The ID of the product
     * @param quantity The quantity to decrease
     * @throws IllegalStateException if insufficient stock
     */
    void decreaseStock(Long productId, int quantity);
    
    /**
     * Increases product stock by specified quantity.
     * Used for restocking or order cancellations.
     * 
     * @param productId The ID of the product
     * @param quantity The quantity to increase
     */
    void increaseStock(Long productId, int quantity);
}