package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for order information.
 * This class represents order data sent to/from the client.
 * 
 * Contains:
 * - Order identifier and user reference
 * - List of ordered items with product details
 * - Total amount and order timestamp
 * 
 * ID Açıklaması:
 * - id: Siparişin benzersiz kimliği
 * - userId: Siparişi veren kullanıcının kimliği
 * - items[].id: Her sipariş kalemi için ayrı kimlik (sipariş detayı için)
 * - items[].product.id: Ürünün benzersiz kimliği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    /**
     * Unique identifier of the order.
     */
    private Long id;
    
    /**
     * ID of the user who placed the order.
     */
    private Long userId;
    
    /**
     * List of items in the order.
     */
    private List<OrderItemDto> items;
    
    /**
     * Total amount of the order.
     */
    private BigDecimal totalAmount;
    
    /**
     * Timestamp when the order was placed.
     */
    private LocalDateTime orderedAt;
}