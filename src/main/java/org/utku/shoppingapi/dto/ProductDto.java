package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object for product information.
 * This class represents product data sent to/from the client.
 * 
 * Contains:
 * - Product identifier and basic information
 * - Pricing and inventory details
 * - Category and status information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    /**
     * Unique identifier of the product.
     */
    private Long id;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Price of the product.
     */
    private BigDecimal price;

    /**
     * Available stock quantity for the product.
     */
    private Integer stockQuantity;

    /**
     * Category classification for the product.
     */
    private String category;

    /**
     * Indicates whether the product is active (available for sale).
     */
    private boolean active;

    /**
     * Timestamp when the product was created.
     */
    private java.time.LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     */
    private java.time.LocalDateTime updatedAt;
}