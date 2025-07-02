package org.utku.shoppingapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.utku.shoppingapi.constants.AppConstants;

/**
 * Data Transfer Object for adding items to shopping cart.
 * This class represents the request payload when adding products to a cart.
 * 
 * Contains validation rules to ensure:
 * - Product ID is provided and not null
 * - Quantity is within acceptable range
 */
@Data
public class AddToCartRequest {
    
    /**
     * The unique identifier of the product to add to cart.
     * Must not be null.
     */
    @NotNull(message = "Product ID cannot be empty")
    private Long productId;
    
    /**
     * The quantity of the product to add to cart.
     * Must be between minimum and maximum allowed quantities.
     */
    @NotNull(message = "Quantity cannot be empty")
    @Min(value = AppConstants.MIN_QUANTITY, message = "Quantity must be at least " + AppConstants.MIN_QUANTITY)
    @Max(value = AppConstants.MAX_QUANTITY, message = "Quantity cannot exceed " + AppConstants.MAX_QUANTITY)
    private Integer quantity;
}