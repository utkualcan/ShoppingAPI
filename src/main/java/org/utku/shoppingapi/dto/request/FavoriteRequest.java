package org.utku.shoppingapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for favorite operations.
 * Used for adding/removing products from favorites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {
    
    /**
     * ID of the user performing the favorite operation.
     */
    @NotNull(message = "User ID is required")
    private Long userId;
    
    /**
     * ID of the product to add/remove from favorites.
     */
    @NotNull(message = "Product ID is required")
    private Long productId;
}