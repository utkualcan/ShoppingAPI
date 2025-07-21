package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Simplified Data Transfer Object for order item information.
 * Reduces complexity by showing only essential product info.
 * 
 * Contains:
 * - Product name and category instead of full product object
 * - Quantity and pricing information
 * - No complex ID references for better readability
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderItemDto {
    
    /**
     * Product name.
     */
    private String productName;
    /**
     * Product category.
     */
    private String category;
    /**
     * Quantity ordered.
     */
    private Integer quantity;
    /**
     * Price per unit at time of order.
     */
    private BigDecimal unitPrice;
    /**
     * Total price for this item.
     */
    private BigDecimal totalPrice;
}