package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simplified Data Transfer Object for order information.
 * This version reduces ID complexity for better readability.
 * 
 * Contains:
 * - Order identifier and customer information
 * - Simplified item list without complex ID references
 * - Total calculations and timestamps
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderDto {
    
    /**
     * Order number for customer reference.
     */
    private Long orderId;
    
    /**
     * Customer name who placed the order.
     */
    private String customerName;
    
    /**
     * Simplified list of ordered products.
     */
    private List<SimpleOrderItemDto> items;
    
    /**
     * Total amount of the order.
     */
    private BigDecimal totalAmount;
    
    /**
     * When the order was placed.
     */
    private LocalDateTime orderedAt;
    
    /**
     * Total number of items in the order.
     */
    private Integer totalItems;
}