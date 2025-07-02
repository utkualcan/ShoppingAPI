package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for favorite information.
 * This class represents favorite data sent to/from the client.
 * 
 * Contains:
 * - Favorite identifier and user reference
 * - Complete product information
 * - Creation timestamp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {
    
    /**
     * Unique identifier of the favorite record.
     */
    private Long id;
    
    /**
     * ID of the user who favorited the product.
     */
    private Long userId;
    
    /**
     * Complete product information.
     */
    private ProductDto product;
    
    /**
     * Timestamp when the favorite was created.
     */
    private LocalDateTime createdAt;
}