package org.utku.shoppingapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new shopping cart.
 * Simplifies cart creation by requiring only essential information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartRequest {
    
    /**
     * ID of the user who will own this cart.
     * Required for authenticated users.
     */
    @NotNull(message = "User ID is required")
    private Long userId;
    
    /**
     * Optional session ID for guest carts.
     * Used when user is not authenticated.
     */
    private String sessionId;
}