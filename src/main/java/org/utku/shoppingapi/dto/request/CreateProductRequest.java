package org.utku.shoppingapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.utku.shoppingapi.constants.AppConstants;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating new products.
 * This class represents the request payload when creating a new product in the catalog.
 * 
 * Contains validation rules to ensure:
 * - Product name is provided and within length limits
 * - Price is positive and not null
 * - Stock quantity is non-negative
 * - Category name is within acceptable length
 */
@Data
public class CreateProductRequest {
    
    /**
     * The name of the product.
     * Must not be blank and must be within allowed length range.
     */
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = AppConstants.MIN_PRODUCT_NAME_LENGTH, max = AppConstants.MAX_PRODUCT_NAME_LENGTH, 
          message = "Product name must be between " + AppConstants.MIN_PRODUCT_NAME_LENGTH + 
                   " and " + AppConstants.MAX_PRODUCT_NAME_LENGTH + " characters")
    private String name;
    
    /**
     * Optional description of the product.
     * Can provide detailed information about the product features.
     */
    @Size(max = AppConstants.MAX_DESCRIPTION_LENGTH, 
          message = "Description cannot exceed " + AppConstants.MAX_DESCRIPTION_LENGTH + " characters")
    private String description;
    
    /**
     * The price of the product.
     * Must be greater than 0 and cannot be null.
     */
    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    /**
     * The available stock quantity for the product.
     * Must be 0 or positive, cannot be null.
     */
    @NotNull(message = "Stock quantity cannot be empty")
    @Min(value = 0, message = "Stock quantity must be 0 or positive")
    private Integer stockQuantity;
    
    /**
     * Optional category classification for the product.
     * Cannot exceed maximum allowed length if provided.
     */
    @Size(max = AppConstants.MAX_CATEGORY_LENGTH, 
          message = "Category cannot exceed " + AppConstants.MAX_CATEGORY_LENGTH + " characters")
    private String category;
}