package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object for cart item information.
 * This class represents individual cart item data sent to/from the client.
 * 
 * Contains:
 * - Cart item identifier
 * - Complete product information
 * - Quantity and pricing details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    
    /**
     * Unique identifier of the cart item.
     */
    private Long id;
    
    /**
     * Complete product information.
     */
    private ProductDto product;
    
    /**
     * Quantity of the product in the cart.
     */
    private Integer quantity;
    
    /**
     * Unit price of the product when added to cart.
     */
    private BigDecimal unitPrice;
    
    /**
     * Total price for this cart item (unit price × quantity).
     */
    private BigDecimal totalPrice;
}