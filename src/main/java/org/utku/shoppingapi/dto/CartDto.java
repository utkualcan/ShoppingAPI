package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Data Transfer Object for shopping cart information.
 * This class represents cart data sent to/from the client.
 * 
 * Contains:
 * - Cart identifier and user reference
 * - List of cart items with product details
 * - Calculated totals for price and item count
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    /**
     * Unique identifier of the cart.
     */
    private Long id;

    /**
     * ID of the user who owns this cart.
     */
    private Long userId;

    /**
     * List of items in the cart.
     */
    private List<CartItemDto> items;

    /**
     * Total price of all items in the cart.
     */
    private java.math.BigDecimal totalPrice;

    /**
     * Total number of items in the cart.
     */
    private Integer totalItems;
}