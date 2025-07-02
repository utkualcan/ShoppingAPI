package org.utku.shoppingapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing shopping cart.
 * Allows updating cart metadata without affecting items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {
    
    /**
     * New user ID to assign the cart to.
     * Optional - only update if provided.
     */
    private Long userId;
    
    /**
     * New session ID for the cart.
     * Optional - only update if provided.
     */
    private String sessionId;
}