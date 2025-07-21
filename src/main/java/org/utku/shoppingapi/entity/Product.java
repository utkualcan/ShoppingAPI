package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.utku.shoppingapi.constants.AppConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a product in the e-commerce catalog.
 * This class contains all product-related information including:
 * - Basic product details (name, description, category)
 * - Pricing and stock information
 * - Product status and timestamps
 * 
 * Products can be active/inactive and track stock quantities for inventory management.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product.
     * Must be within allowed length range and cannot be blank.
     */
    @Column // length parameter deleted.
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = AppConstants.MIN_PRODUCT_NAME_LENGTH, max = AppConstants.MAX_PRODUCT_NAME_LENGTH, 
          message = "Product name must be between " + AppConstants.MIN_PRODUCT_NAME_LENGTH + 
                   "-" + AppConstants.MAX_PRODUCT_NAME_LENGTH + " characters")
    private String name;

    /**
     * Detailed description of the product.
     * Optional field that can contain up to maximum allowed characters.
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = AppConstants.MAX_DESCRIPTION_LENGTH, 
          message = "Description cannot exceed " + AppConstants.MAX_DESCRIPTION_LENGTH + " characters")
    private String description;

    /**
     * Price of the product with 2 decimal places precision.
     * Must be greater than 0 and cannot be null.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    /**
     * Available stock quantity for the product.
     * Must be 0 or positive, cannot be negative.
     */
    @Column(nullable = false)
    @NotNull(message = "Stock quantity cannot be empty")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    /**
     * Category classification for the product.
     * Optional field for organizing products into groups.
     */
    @Column(length = AppConstants.MAX_CATEGORY_LENGTH)
    private String category;

    /**
     * Indicates whether the product is active and available for purchase.
     * Inactive products are hidden from customers but remain in the system.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Timestamp when the product was created.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     * Automatically updated whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Checks if the product has available stock.
     * 
     * @return true if stock quantity is greater than 0, false otherwise
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Checks if the product is available for purchase.
     * A product is available if it's both active and in stock.
     * 
     * @return true if product is active and has stock, false otherwise
     */
    public boolean isAvailable() {
        return active && isInStock();
    }

    /**
     * Decreases the stock quantity by the specified amount.
     * Validates that there is sufficient stock before decreasing.
     * 
     * @param quantity The amount to decrease (must be positive)
     * @throws IllegalArgumentException if quantity is not positive
     * @throws IllegalStateException if there is insufficient stock
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to decrease must be positive");
        }
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Increases the stock quantity by the specified amount.
     * 
     * @param quantity The amount to increase (must be positive)
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to increase must be positive");
        }
        this.stockQuantity += quantity;
    }
}