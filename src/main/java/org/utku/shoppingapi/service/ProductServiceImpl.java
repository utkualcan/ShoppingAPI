package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.repository.ProductRepository;

import java.util.Optional;

/**
 * Implementation of ProductService.
 * Handles all business logic for product management and inventory operations.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all active products with pagination.
     * @param pageable Pagination parameters
     * @return Page of active products
     */
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        // Retrieve all active products with pagination
        return productRepository.findAllActive(pageable);
    }

    /**
     * Finds an active product by its ID.
     * @param id Product ID
     * @return Optional containing the product if found and active
     */
    @Override
    public Optional<Product> findProductById(Long id) {
        // Find product by ID and check if active
        return productRepository.findById(id)
                .filter(Product::getActive);
    }

    /**
     * Creates a new product in the repository.
     * @param product Product entity to create
     * @return Created product entity
     */
    @Override
    public Product createProduct(Product product) {
        // Save new product to repository
        return productRepository.save(product);
    }

    /**
     * Updates an existing product's fields if provided.
     * @param id Product ID to update
     * @param product Product object containing updated fields
     * @return Updated product entity
     */
    @Override
    public Product updateProduct(Long id, Product product) {
        // Update product fields if provided
        Product existing = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id));

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

        // Save updated product
        return productRepository.save(existing);
    }

    /**
     * Deletes an active product from the repository.
     * @param id Product ID to delete
     */
    @Override
    public void deleteProduct(Long id) {
        // Delete product if found and active
        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id));
        productRepository.delete(product);
    }

    /**
     * Decreases product stock by specified quantity and saves the change.
     * @param productId Product ID
     * @param quantity Quantity to decrease
     */
    @Override
    public void decreaseStock(Long productId, int quantity) {
        // Decrease product stock and save
        Product product = productRepository.findById(productId)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    /**
     * Increases product stock by specified quantity and saves the change.
     * @param productId Product ID
     * @param quantity Quantity to increase
     */
    @Override
    public void increaseStock(Long productId, int quantity) {
        // Increase product stock and save
        Product product = productRepository.findById(productId)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        product.increaseStock(quantity);
        productRepository.save(product);
    }
}