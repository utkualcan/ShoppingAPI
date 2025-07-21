package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Simplified Data Transfer Object for cart item information.
 * Reduces complexity by showing only essential product info without IDs.
 * 
 * Contains:
 * - Product name and category instead of full product object
 * - Quantity and pricing information
 * - Stock status for inventory awareness
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCartItemDto {
    
    /**
     * Product name.
     */
    private String productName;
    /**
     * Product category.
     */
    private String category;
    /**
     * Current price per unit.
     */
    private BigDecimal unitPrice;
    /**
     * Quantity in cart.
     */
    private Integer quantity;
    /**
     * Total price for this item (unitPrice * quantity).
     */
    private BigDecimal totalPrice;
    /**
     * Stock availability status.
     */
    private String stockStatus;
}