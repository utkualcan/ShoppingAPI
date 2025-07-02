package org.utku.shoppingapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import org.utku.shoppingapi.constants.AppConstants;

/**
 * Request DTO for updating item quantity in cart.
 * Simplifies quantity update operations with proper validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuantityRequest {
    
    /**
     * New quantity for the cart item.
     * Must be within allowed range.
     */
    @NotNull(message = "Quantity cannot be empty")
    @Min(value = AppConstants.MIN_QUANTITY, message = "Quantity must be at least " + AppConstants.MIN_QUANTITY)
    @Max(value = AppConstants.MAX_QUANTITY, message = "Quantity cannot exceed " + AppConstants.MAX_QUANTITY)
    private Integer quantity;
}