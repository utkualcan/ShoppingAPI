package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService interface.
 * This service class handles all business logic related to product management including:
 * - CRUD operations for products
 * - Product validation and data processing
 * - Integration with ProductRepository for data persistence
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param productRepository Repository for product data access
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products with pagination support.
     * 
     * @param pageable Pagination parameters
     * @return Page of products
     */
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Finds a product by its unique identifier.
     * 
     * @param id The product ID
     * @return Optional containing the product if found
     */
    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Creates a new product in the catalog.
     * 
     * @param product The product entity to create
     * @return The created product entity
     */
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Updates an existing product's information.
     * Only updates non-null fields to support partial updates.
     * 
     * @param id The ID of the product to update
     * @param product Product object containing updated information
     * @return The updated product entity
     * @throws RuntimeException if product is not found
     */
    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id));
        
        // Update only non-null fields for partial update support
        if (product.getName() != null) {
            existing.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existing.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existing.setPrice(product.getPrice());
        }
        if (product.getStockQuantity() != null) {
            existing.setStockQuantity(product.getStockQuantity());
        }
        if (product.getCategory() != null) {
            existing.setCategory(product.getCategory());
        }
        if (product.getActive() != null) {
            existing.setActive(product.getActive());
        }
        
        return productRepository.save(existing);
    }

    /**
     * Deletes a product from the catalog by its ID.
     * Validates that the product exists before deletion.
     * 
     * @param id The ID of the product to delete
     * @throws ResourceNotFoundException if product is not found
     */
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id);
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        product.decreaseStock(quantity);
        productRepository.save(product);
    }
    
    @Override
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        product.increaseStock(quantity);
        productRepository.save(product);
    }
}