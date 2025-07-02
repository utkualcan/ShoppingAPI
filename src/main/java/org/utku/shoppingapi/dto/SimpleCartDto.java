package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Simplified Data Transfer Object for shopping cart information.
 * This version reduces ID complexity for better readability.
 * 
 * Contains:
 * - Cart identifier and customer information
 * - Simplified item list without complex ID references
 * - Total calculations and product counts
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCartDto {
    
    /**
     * Cart number for customer reference.
     */
    private Long cartId;
    
    /**
     * Customer name who owns the cart.
     */
    private String customerName;
    
    /**
     * Simplified list of cart items.
     */
    private List<SimpleCartItemDto> items;
    
    /**
     * Total price of all items in cart.
     */
    private BigDecimal totalPrice;
    
    /**
     * Total number of items in cart.
     */
    private Integer totalItems;
    
    /**
     * Number of different products in cart.
     */
    private Integer uniqueProducts;
}