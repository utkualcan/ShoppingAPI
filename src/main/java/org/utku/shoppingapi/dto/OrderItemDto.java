package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object for order item information.
 * This class represents individual order item data sent to/from the client.
 * 
 * Contains:
 * - Order item identifier
 * - Complete product information
 * - Quantity and historical pricing details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    
    /**
     * Unique identifier of the order item.
     */
    private Long id;
    
    /**
     * Complete product information.
     */
    private ProductDto product;
    
    /**
     * Quantity of the product ordered.
     */
    private Integer quantity;
    
    /**
     * Unit price of the product at the time of order.
     */
    private BigDecimal unitPrice;
    
    /**
     * Total price for this order item (unit price × quantity).
     */
    private BigDecimal totalPrice;
}